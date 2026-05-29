package com.ogc_prototype.ogc.service.almacen.impl;

import com.ogc_prototype.ogc.dto.request.AdjustmentLineRequest;
import com.ogc_prototype.ogc.dto.request.InventoryAdjustmentRequest;
import com.ogc_prototype.ogc.dto.response.AdjustmentLineResponse;
import com.ogc_prototype.ogc.dto.response.InventoryAdjustmentResponse;
import com.ogc_prototype.ogc.exception.InventoryAdjustmentException;
import com.ogc_prototype.ogc.exception.LotException;
import com.ogc_prototype.ogc.mapper.AdjustmentLineMapper;
import com.ogc_prototype.ogc.mapper.InventoryAdjustmentMapper;
import com.ogc_prototype.ogc.model.AdjustmentLine;
import com.ogc_prototype.ogc.model.InventoryAdjustment;
import com.ogc_prototype.ogc.model.Lot;
import com.ogc_prototype.ogc.model.LotStock;
import com.ogc_prototype.ogc.model.StockMovement;
import com.ogc_prototype.ogc.model.enums.MovementType;
import com.ogc_prototype.ogc.repository.AdjustmentLineRepository;
import com.ogc_prototype.ogc.repository.InventoryAdjustmentRepository;
import com.ogc_prototype.ogc.repository.LotRepository;
import com.ogc_prototype.ogc.repository.LotStockRepository;
import com.ogc_prototype.ogc.repository.StockMovementRepository;
import com.ogc_prototype.ogc.service.almacen.InventoryAdjustmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class InventoryAdjustmentServiceImpl implements InventoryAdjustmentService {

    private final InventoryAdjustmentRepository adjustmentRepository;
    private final AdjustmentLineRepository adjustmentLineRepository;
    private final LotRepository lotRepository;
    private final LotStockRepository lotStockRepository;
    private final StockMovementRepository stockMovementRepository;

    public InventoryAdjustmentServiceImpl(InventoryAdjustmentRepository adjustmentRepository,
            AdjustmentLineRepository adjustmentLineRepository, LotRepository lotRepository,
            LotStockRepository lotStockRepository,
            StockMovementRepository stockMovementRepository) {
        this.adjustmentRepository = adjustmentRepository;
        this.adjustmentLineRepository = adjustmentLineRepository;
        this.lotRepository = lotRepository;
        this.lotStockRepository = lotStockRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Override
    public List<InventoryAdjustmentResponse> getAll() {
        return adjustmentRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(InventoryAdjustmentMapper::toResponse).toList();
    }

    @Override
    public InventoryAdjustmentResponse getById(Integer id) {
        InventoryAdjustment adjustment = adjustmentRepository.findById(id)
                .orElseThrow(() -> InventoryAdjustmentException.notFound(id));
        return InventoryAdjustmentMapper.toResponse(adjustment);
    }

    @Override
    @Transactional
    public InventoryAdjustmentResponse create(InventoryAdjustmentRequest request) {
        return InventoryAdjustmentMapper
                .toResponse(adjustmentRepository.save(InventoryAdjustmentMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public AdjustmentLineResponse addLine(AdjustmentLineRequest request) {
        InventoryAdjustment adjustment =
                adjustmentRepository.findById(request.getAdjustmentId()).orElseThrow(
                        () -> InventoryAdjustmentException.notFound(request.getAdjustmentId()));
        Lot lot = lotRepository.findById(request.getLotId())
                .orElseThrow(() -> LotException.notFound(request.getLotId()));
        LotStock lotStock = lotStockRepository.findByLotId(lot.getId())
                .orElseThrow(() -> LotException.stockNotFound(lot.getId()));

        double newRemaining = lotStock.getRemainingWeight() + request.getQuantityKg();
        if (newRemaining < 0) {
            throw LotException.insufficientStock(lot.getId(), Math.abs(request.getQuantityKg()),
                    lotStock.getRemainingWeight());
        }
        lotStock.setRemainingWeight(newRemaining);
        lotStockRepository.save(lotStock);

        AdjustmentLine line = adjustmentLineRepository
                .save(AdjustmentLineMapper.toEntity(request, adjustment, lot));

        MovementType type =
                request.getQuantityKg() >= 0 ? MovementType.ENTRADA : MovementType.SALIDA;
        stockMovementRepository.save(StockMovement.builder().lot(lot).type(type)
                .weightKg(Math.abs(request.getQuantityKg())).notes(request.getNotes()).build());

        return AdjustmentLineMapper.toResponse(line);
    }

    @Override
    public List<AdjustmentLineResponse> getLines(Integer adjustmentId) {
        if (!adjustmentRepository.existsById(adjustmentId)) {
            throw InventoryAdjustmentException.notFound(adjustmentId);
        }
        return adjustmentLineRepository.findAllByAdjustmentId(adjustmentId).stream()
                .map(AdjustmentLineMapper::toResponse).toList();
    }

    @Override
    public AdjustmentLineResponse getLine(Integer adjustmentId, Integer lineId) {
        return adjustmentLineRepository.findAllByAdjustmentId(adjustmentId).stream()
                .filter(l -> l.getId().equals(lineId)).findFirst()
                .map(AdjustmentLineMapper::toResponse)
                .orElseThrow(() -> InventoryAdjustmentException.lineNotFound(lineId));
    }
}
