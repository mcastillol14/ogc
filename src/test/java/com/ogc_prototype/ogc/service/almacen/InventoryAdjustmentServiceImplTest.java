package com.ogc_prototype.ogc.service.almacen;

import com.ogc_prototype.ogc.dto.request.AdjustmentLineRequest;
import com.ogc_prototype.ogc.dto.request.InventoryAdjustmentRequest;
import com.ogc_prototype.ogc.dto.response.AdjustmentLineResponse;
import com.ogc_prototype.ogc.dto.response.InventoryAdjustmentResponse;
import com.ogc_prototype.ogc.exception.InventoryAdjustmentException;
import com.ogc_prototype.ogc.exception.LotException;
import com.ogc_prototype.ogc.model.AdjustmentLine;
import com.ogc_prototype.ogc.model.InventoryAdjustment;
import com.ogc_prototype.ogc.model.Lot;
import com.ogc_prototype.ogc.model.Product;
import com.ogc_prototype.ogc.model.LotStock;
import com.ogc_prototype.ogc.repository.AdjustmentLineRepository;
import com.ogc_prototype.ogc.repository.InventoryAdjustmentRepository;
import com.ogc_prototype.ogc.repository.LotRepository;
import com.ogc_prototype.ogc.repository.LotStockRepository;
import com.ogc_prototype.ogc.repository.StockMovementRepository;
import com.ogc_prototype.ogc.service.almacen.impl.InventoryAdjustmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryAdjustmentServiceImplTest {

    @Mock
    private InventoryAdjustmentRepository adjustmentRepository;
    @Mock
    private AdjustmentLineRepository adjustmentLineRepository;
    @Mock
    private LotRepository lotRepository;
    @Mock
    private LotStockRepository lotStockRepository;
    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private InventoryAdjustmentServiceImpl service;

    private InventoryAdjustment buildAdjustment(int id) {
        return InventoryAdjustment.builder().id(id).reason("Test adjustment").build();
    }

    private Lot buildLot(int id) {
        Product product = Product.builder().id(id).name("Product A").build();
        return Lot.builder().id(id).product(product).weight(100.0).build();
    }

    @Test
    void getAll_returnsList() {
        when(adjustmentRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(buildAdjustment(1)));

        List<InventoryAdjustmentResponse> result = service.getAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void getById_found_returnsResponse() {
        when(adjustmentRepository.findById(1)).thenReturn(Optional.of(buildAdjustment(1)));

        InventoryAdjustmentResponse response = service.getById(1);

        assertThat(response).isNotNull();
    }

    @Test
    void getById_notFound_throwsException() {
        when(adjustmentRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
                .isInstanceOf(InventoryAdjustmentException.class);
    }

    @Test
    void create_savesAndReturnsAdjustment() {
        InventoryAdjustmentRequest req =
                InventoryAdjustmentRequest.builder().reason("Stock count discrepancy").build();
        when(adjustmentRepository.save(any())).thenReturn(buildAdjustment(1));

        InventoryAdjustmentResponse response = service.create(req);

        assertThat(response).isNotNull();
        verify(adjustmentRepository).save(any());
    }

    @Test
    void addLine_positiveQuantity_increasesStock() {
        InventoryAdjustment adjustment = buildAdjustment(1);
        Lot lot = buildLot(1);
        LotStock stock = LotStock.builder().lot(lot).remainingWeight(50.0).build();
        AdjustmentLine line = AdjustmentLine.builder().id(1).adjustment(adjustment).lot(lot)
                .quantityKg(10.0).build();

        when(adjustmentRepository.findById(1)).thenReturn(Optional.of(adjustment));
        when(lotRepository.findById(1)).thenReturn(Optional.of(lot));
        when(lotStockRepository.findByLotId(1)).thenReturn(Optional.of(stock));
        when(adjustmentLineRepository.save(any())).thenReturn(line);
        when(stockMovementRepository.save(any())).thenReturn(null);

        AdjustmentLineRequest req = AdjustmentLineRequest.builder().adjustmentId(1).lotId(1)
                .quantityKg(10.0).notes("added").build();

        AdjustmentLineResponse response = service.addLine(req);

        assertThat(response).isNotNull();
        assertThat(stock.getRemainingWeight()).isEqualTo(60.0);
    }

    @Test
    void addLine_negativeQuantityBeyondStock_throwsLotException() {
        InventoryAdjustment adjustment = buildAdjustment(1);
        Lot lot = buildLot(1);
        LotStock stock = LotStock.builder().lot(lot).remainingWeight(5.0).build();

        when(adjustmentRepository.findById(1)).thenReturn(Optional.of(adjustment));
        when(lotRepository.findById(1)).thenReturn(Optional.of(lot));
        when(lotStockRepository.findByLotId(1)).thenReturn(Optional.of(stock));

        AdjustmentLineRequest req = AdjustmentLineRequest.builder().adjustmentId(1).lotId(1)
                .quantityKg(-10.0).notes("remove too much").build();

        assertThatThrownBy(() -> service.addLine(req)).isInstanceOf(LotException.class);
    }

    @Test
    void getLines_adjustmentNotFound_throwsException() {
        when(adjustmentRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> service.getLines(99))
                .isInstanceOf(InventoryAdjustmentException.class);
    }

    @Test
    void getLines_returnsLinesForAdjustment() {
        InventoryAdjustment adj = buildAdjustment(1);
        Lot lot = buildLot(1);
        when(adjustmentRepository.existsById(1)).thenReturn(true);
        when(adjustmentLineRepository.findAllByAdjustmentId(1)).thenReturn(List.of(
                AdjustmentLine.builder().id(1).adjustment(adj).lot(lot).quantityKg(5.0).build()));

        List<AdjustmentLineResponse> lines = service.getLines(1);

        assertThat(lines).hasSize(1);
    }
}
