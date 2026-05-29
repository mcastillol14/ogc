package com.ogc_prototype.ogc.service.ventas.impl;

import com.ogc_prototype.ogc.dto.request.SaleLineRequest;
import com.ogc_prototype.ogc.dto.response.SaleLineResponse;
import com.ogc_prototype.ogc.exception.LotException;
import com.ogc_prototype.ogc.exception.SaleException;
import com.ogc_prototype.ogc.mapper.SaleLineMapper;
import com.ogc_prototype.ogc.model.Customer;
import com.ogc_prototype.ogc.model.Lot;
import com.ogc_prototype.ogc.model.LotStock;
import com.ogc_prototype.ogc.model.Sale;
import com.ogc_prototype.ogc.model.SaleLine;
import com.ogc_prototype.ogc.model.StockMovement;
import com.ogc_prototype.ogc.model.enums.MovementType;
import com.ogc_prototype.ogc.repository.CustomerRepository;
import com.ogc_prototype.ogc.repository.LotRepository;
import com.ogc_prototype.ogc.repository.LotStockRepository;
import com.ogc_prototype.ogc.repository.SaleLineRepository;
import com.ogc_prototype.ogc.repository.SaleRepository;
import com.ogc_prototype.ogc.repository.StockMovementRepository;
import com.ogc_prototype.ogc.service.ventas.SaleLineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SaleLineServiceImpl implements SaleLineService {

    private final SaleLineRepository saleLineRepository;
    private final SaleRepository saleRepository;
    private final LotRepository lotRepository;
    private final LotStockRepository lotStockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final CustomerRepository customerRepository;

    public SaleLineServiceImpl(SaleLineRepository saleLineRepository, SaleRepository saleRepository,
            LotRepository lotRepository, LotStockRepository lotStockRepository,
            StockMovementRepository stockMovementRepository,
            CustomerRepository customerRepository) {
        this.saleLineRepository = saleLineRepository;
        this.saleRepository = saleRepository;
        this.lotRepository = lotRepository;
        this.lotStockRepository = lotStockRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<SaleLineResponse> getBySaleId(Integer saleId) {
        if (!saleRepository.existsById(saleId)) {
            throw SaleException.notFound(saleId);
        }
        return saleLineRepository.findAllBySaleId(saleId).stream().map(SaleLineMapper::toResponse)
                .toList();
    }

    @Override
    public SaleLineResponse getById(Integer saleId, Integer lineId) {
        SaleLine line = saleLineRepository.findByIdAndSaleId(lineId, saleId)
                .orElseThrow(() -> SaleException.lineNotFound(lineId));
        return SaleLineMapper.toResponse(line);
    }

    @Override
    @Transactional
    public SaleLineResponse create(SaleLineRequest request) {
        Sale sale = saleRepository.findById(request.getSaleId())
                .orElseThrow(() -> SaleException.notFound(request.getSaleId()));
        Lot lot = lotRepository.findById(request.getLotId())
                .orElseThrow(() -> LotException.notFound(request.getLotId()));

        // Validar que el lote no esté caducado
        if (lot.getExpirationDate().before(new Date())) {
            throw LotException.expired(lot.getId(), lot.getExpirationDate());
        }

        // Validar stock suficiente y decrementar
        LotStock lotStock = lotStockRepository.findByLotId(lot.getId())
                .orElseThrow(() -> LotException.stockNotFound(lot.getId()));
        if (lotStock.getRemainingWeight() < request.getWeightKg()) {
            throw LotException.insufficientStock(lot.getId(), request.getWeightKg(),
                    lotStock.getRemainingWeight());
        }
        lotStock.setRemainingWeight(lotStock.getRemainingWeight() - request.getWeightKg());
        lotStockRepository.save(lotStock);

        // Crear línea de venta
        SaleLine line = saleLineRepository.save(SaleLineMapper.toEntity(request, sale, lot));

        // Registrar movimiento de stock SALIDA
        stockMovementRepository.save(StockMovement.builder().lot(lot).type(MovementType.SALIDA)
                .weightKg(request.getWeightKg()).sale(sale).build());

        // Acumular puntos de fidelidad (1 punto por kg)
        Customer customer = sale.getCustomer();
        customer.setFidelityPoints(
                customer.getFidelityPoints() + (int) Math.floor(request.getWeightKg()));
        customerRepository.save(customer);

        // Recalcular totalAmount de la venta
        sale.setTotalAmount(
                sale.getTotalAmount() + request.getWeightKg() * request.getUnitPricePerKg());
        saleRepository.save(sale);

        return SaleLineMapper.toResponse(line);
    }

    @Override
    @Transactional
    public void delete(Integer saleId, Integer lineId) {
        SaleLine line = saleLineRepository.findByIdAndSaleId(lineId, saleId)
                .orElseThrow(() -> SaleException.lineNotFound(lineId));
        Sale sale = line.getSale();
        Lot lot = line.getLot();

        // Revertir stock
        LotStock lotStock = lotStockRepository.findByLotId(lot.getId())
                .orElseThrow(() -> LotException.stockNotFound(lot.getId()));
        lotStock.setRemainingWeight(lotStock.getRemainingWeight() + line.getWeightKg());
        lotStockRepository.save(lotStock);

        // Movimiento compensatorio ENTRADA
        stockMovementRepository.save(StockMovement.builder().lot(lot).type(MovementType.ENTRADA)
                .weightKg(line.getWeightKg()).sale(sale)
                .notes("Reversión por eliminación de línea de venta " + lineId).build());

        // Revertir puntos de fidelidad
        Customer customer = sale.getCustomer();
        customer.setFidelityPoints(
                Math.max(0, customer.getFidelityPoints() - (int) Math.floor(line.getWeightKg())));
        customerRepository.save(customer);

        // Recalcular totalAmount
        sale.setTotalAmount(Math.max(0.0,
                sale.getTotalAmount() - line.getWeightKg() * line.getUnitPricePerKg()));
        saleRepository.save(sale);

        saleLineRepository.delete(line);
    }
}
