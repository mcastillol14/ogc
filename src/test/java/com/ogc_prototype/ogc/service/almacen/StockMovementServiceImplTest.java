package com.ogc_prototype.ogc.service.almacen;

import com.ogc_prototype.ogc.dto.response.StockMovementResponse;
import com.ogc_prototype.ogc.model.Lot;
import com.ogc_prototype.ogc.model.Product;
import com.ogc_prototype.ogc.model.StockMovement;
import com.ogc_prototype.ogc.model.enums.MovementType;
import com.ogc_prototype.ogc.repository.StockMovementRepository;
import com.ogc_prototype.ogc.service.almacen.impl.StockMovementServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceImplTest {

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private StockMovementServiceImpl service;

    private StockMovement buildMovement(int id, MovementType type) {
        Product product = Product.builder().id(1).name("Product A").build();
        Lot lot = Lot.builder().id(1).product(product).build();
        return StockMovement.builder().id(id).lot(lot).type(type).weightKg(5.0).build();
    }

    @Test
    void getAll_returnsList() {
        when(stockMovementRepository.findAll())
                .thenReturn(List.of(buildMovement(1, MovementType.ENTRADA)));

        List<StockMovementResponse> result = service.getAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void getAll_emptyRepo_returnsEmpty() {
        when(stockMovementRepository.findAll()).thenReturn(List.of());

        assertThat(service.getAll()).isEmpty();
    }

    @Test
    void getByLotId_returnsFilteredMovements() {
        when(stockMovementRepository.findAllByLotId(1))
                .thenReturn(List.of(buildMovement(1, MovementType.ENTRADA)));

        List<StockMovementResponse> result = service.getByLotId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void getByPurchaseId_returnsList() {
        when(stockMovementRepository.findAllByPurchaseId(10))
                .thenReturn(List.of(buildMovement(2, MovementType.ENTRADA)));

        assertThat(service.getByPurchaseId(10)).hasSize(1);
    }

    @Test
    void getBySaleId_returnsList() {
        when(stockMovementRepository.findAllBySaleId(5))
                .thenReturn(List.of(buildMovement(3, MovementType.SALIDA)));

        assertThat(service.getBySaleId(5)).hasSize(1);
    }

    @Test
    void getByType_returnsList() {
        when(stockMovementRepository.findAllByType(MovementType.SALIDA))
                .thenReturn(List.of(buildMovement(3, MovementType.SALIDA)));

        List<StockMovementResponse> result = service.getByType(MovementType.SALIDA);

        assertThat(result).hasSize(1);
    }
}
