package com.ogc_prototype.ogc.service.ventas;

import com.ogc_prototype.ogc.dto.request.SaleLineRequest;
import com.ogc_prototype.ogc.dto.response.SaleLineResponse;
import com.ogc_prototype.ogc.exception.LotException;
import com.ogc_prototype.ogc.exception.SaleException;
import com.ogc_prototype.ogc.model.*;
import com.ogc_prototype.ogc.model.enums.MovementType;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.repository.*;
import com.ogc_prototype.ogc.service.ventas.impl.SaleLineServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleLineServiceImplTest {

    @Mock
    private SaleLineRepository saleLineRepository;
    @Mock
    private SaleRepository saleRepository;
    @Mock
    private LotRepository lotRepository;
    @Mock
    private LotStockRepository lotStockRepository;
    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private SaleLineServiceImpl service;

    private Customer buildCustomer(int id) {
        return Customer.builder().id(id).name("Alice").lastName("Smith").email("alice@example.com")
                .userName("alice").password("hash").role(Role.CUSTOMER).phoneNumber("+34600000001")
                .address("Calle 1").city("Madrid").zipCode(28001).country("ES").build();
    }

    private Sale buildSale(int id) {
        return Sale.builder().id(id).customer(buildCustomer(1)).saleDate(LocalDate.now())
                .totalAmount(0.0).build();
    }

    private Lot buildLot(int id, Date expirationDate) {
        Product product = Product.builder().id(id).name("Product A").build();
        return Lot.builder().id(id).product(product).weight(100.0).expirationDate(expirationDate)
                .build();
    }

    private LotStock buildLotStock(Lot lot, double remaining) {
        return LotStock.builder().lot(lot).remainingWeight(remaining).build();
    }

    private SaleLine buildLine(int id, Sale sale, Lot lot) {
        return SaleLine.builder().id(id).sale(sale).lot(lot).weightKg(5.0).unitPricePerKg(30.0)
                .build();
    }

    // ─── getBySaleId ───

    @Test
    void getBySaleId_saleNotFound_throwsSaleException() {
        when(saleRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> service.getBySaleId(99)).isInstanceOf(SaleException.class);
    }

    @Test
    void getBySaleId_found_returnsList() {
        Sale sale = buildSale(1);
        Date futureDate = new Date(System.currentTimeMillis() + 86_400_000L);
        Lot lot = buildLot(1, futureDate);
        when(saleRepository.existsById(1)).thenReturn(true);
        when(saleLineRepository.findAllBySaleId(1)).thenReturn(List.of(buildLine(1, sale, lot)));

        List<SaleLineResponse> result = service.getBySaleId(1);

        assertThat(result).hasSize(1);
    }

    // ─── getById ───

    @Test
    void getById_found_returnsResponse() {
        Sale sale = buildSale(1);
        Date futureDate = new Date(System.currentTimeMillis() + 86_400_000L);
        Lot lot = buildLot(1, futureDate);
        when(saleLineRepository.findByIdAndSaleId(1, 1))
                .thenReturn(Optional.of(buildLine(1, sale, lot)));

        SaleLineResponse response = service.getById(1, 1);

        assertThat(response).isNotNull();
    }

    @Test
    void getById_notFound_throwsSaleException() {
        when(saleLineRepository.findByIdAndSaleId(99, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(1, 99)).isInstanceOf(SaleException.class);
    }

    // ─── create ───

    @Test
    void create_expiredLot_throwsLotException() {
        Sale sale = buildSale(1);
        Date pastDate = new Date(System.currentTimeMillis() - 86_400_000L);
        Lot expiredLot = buildLot(1, pastDate);
        SaleLineRequest req = SaleLineRequest.builder().saleId(1).lotId(1).weightKg(5.0)
                .unitPricePerKg(30.0).build();
        when(saleRepository.findById(1)).thenReturn(Optional.of(sale));
        when(lotRepository.findById(1)).thenReturn(Optional.of(expiredLot));

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(LotException.class);
    }

    @Test
    void create_insufficientStock_throwsLotException() {
        Sale sale = buildSale(1);
        Date futureDate = new Date(System.currentTimeMillis() + 86_400_000L);
        Lot lot = buildLot(1, futureDate);
        LotStock stock = buildLotStock(lot, 2.0); // only 2kg available, requesting 5
        SaleLineRequest req = SaleLineRequest.builder().saleId(1).lotId(1).weightKg(5.0)
                .unitPricePerKg(30.0).build();
        when(saleRepository.findById(1)).thenReturn(Optional.of(sale));
        when(lotRepository.findById(1)).thenReturn(Optional.of(lot));
        when(lotStockRepository.findByLotId(1)).thenReturn(Optional.of(stock));

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(LotException.class);
    }

    @Test
    void create_valid_decrementsStockSavesMovement() {
        Sale sale = buildSale(1);
        Date futureDate = new Date(System.currentTimeMillis() + 86_400_000L);
        Lot lot = buildLot(1, futureDate);
        LotStock stock = buildLotStock(lot, 50.0);
        SaleLine savedLine = buildLine(1, sale, lot);
        SaleLineRequest req = SaleLineRequest.builder().saleId(1).lotId(1).weightKg(5.0)
                .unitPricePerKg(30.0).build();

        when(saleRepository.findById(1)).thenReturn(Optional.of(sale));
        when(lotRepository.findById(1)).thenReturn(Optional.of(lot));
        when(lotStockRepository.findByLotId(1)).thenReturn(Optional.of(stock));
        when(saleLineRepository.save(any())).thenReturn(savedLine);
        when(stockMovementRepository.save(any())).thenReturn(mock(StockMovement.class));
        when(saleRepository.save(any())).thenReturn(sale);

        SaleLineResponse response = service.create(req);

        assertThat(response).isNotNull();
        assertThat(stock.getRemainingWeight()).isEqualTo(45.0); // 50 - 5
        assertThat(sale.getTotalAmount()).isEqualTo(150.0); // 5 * 30
        verify(stockMovementRepository).save(
                argThat(mv -> mv.getType() == MovementType.SALIDA && mv.getWeightKg() == 5.0));
    }

    // ─── delete ───

    @Test
    void delete_existing_revertsStockAndTotalAmount() {
        Sale sale = buildSale(1);
        sale.setTotalAmount(150.0);
        Date futureDate = new Date(System.currentTimeMillis() + 86_400_000L);
        Lot lot = buildLot(1, futureDate);
        LotStock stock = buildLotStock(lot, 45.0);
        SaleLine line = buildLine(1, sale, lot);

        when(saleLineRepository.findByIdAndSaleId(1, 1)).thenReturn(Optional.of(line));
        when(lotStockRepository.findByLotId(1)).thenReturn(Optional.of(stock));
        when(stockMovementRepository.save(any())).thenReturn(mock(StockMovement.class));
        when(saleRepository.save(any())).thenReturn(sale);

        service.delete(1, 1);

        assertThat(stock.getRemainingWeight()).isEqualTo(50.0); // 45 + 5 reverted
        assertThat(sale.getTotalAmount()).isEqualTo(0.0); // 150 - 5*30 = 0
        verify(saleLineRepository).delete(line);
    }

    @Test
    void delete_lineNotFound_throwsSaleException() {
        when(saleLineRepository.findByIdAndSaleId(99, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(1, 99)).isInstanceOf(SaleException.class);
    }
}
