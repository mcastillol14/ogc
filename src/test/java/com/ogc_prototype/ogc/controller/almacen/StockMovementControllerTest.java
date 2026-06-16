package com.ogc_prototype.ogc.controller.almacen;

import com.ogc_prototype.ogc.dto.response.StockMovementResponse;
import com.ogc_prototype.ogc.exception.GlobalExceptionHandler;
import com.ogc_prototype.ogc.model.enums.MovementType;
import com.ogc_prototype.ogc.service.almacen.StockMovementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StockMovementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StockMovementService stockMovementService;

    @InjectMocks
    private StockMovementController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private StockMovementResponse sampleMovement(int id, MovementType type) {
        return StockMovementResponse.builder().id(id).lotId(1).productName("OG Kush CBD").type(type)
                .weightKg(2.5).notes("Movimiento de prueba").createdAt(LocalDateTime.now()).build();
    }

    // ─── GET /api/stock-movements ───

    @Test
    void getAll_returns200WithList() throws Exception {
        when(stockMovementService.getAll()).thenReturn(List.of(
                sampleMovement(1, MovementType.ENTRADA), sampleMovement(2, MovementType.SALIDA)));

        mockMvc.perform(get("/api/stock-movements")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].id").value(1));
    }

    // ─── GET /api/stock-movements/lot/{lotId} ───

    @Test
    void getByLot_returns200() throws Exception {
        when(stockMovementService.getByLotId(3))
                .thenReturn(List.of(sampleMovement(1, MovementType.ENTRADA)));

        mockMvc.perform(get("/api/stock-movements/lot/3")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lotId").value(1));
    }

    @Test
    void getByLot_noResults_returnsEmptyList() throws Exception {
        when(stockMovementService.getByLotId(99)).thenReturn(List.of());

        mockMvc.perform(get("/api/stock-movements/lot/99")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ─── GET /api/stock-movements/purchase/{purchaseId} ───

    @Test
    void getByPurchase_returns200() throws Exception {
        when(stockMovementService.getByPurchaseId(5))
                .thenReturn(List.of(sampleMovement(1, MovementType.ENTRADA)));

        mockMvc.perform(get("/api/stock-movements/purchase/5")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // ─── GET /api/stock-movements/sale/{saleId} ───

    @Test
    void getBySale_returns200() throws Exception {
        when(stockMovementService.getBySaleId(7))
                .thenReturn(List.of(sampleMovement(2, MovementType.SALIDA)));

        mockMvc.perform(get("/api/stock-movements/sale/7")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("SALIDA"));
    }

    // ─── GET /api/stock-movements/type/{type} ───

    @Test
    void getByType_ENTRADA_returns200() throws Exception {
        when(stockMovementService.getByType(MovementType.ENTRADA))
                .thenReturn(List.of(sampleMovement(1, MovementType.ENTRADA)));

        mockMvc.perform(get("/api/stock-movements/type/ENTRADA")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("ENTRADA"));
    }

    @Test
    void getByType_SALIDA_returns200() throws Exception {
        when(stockMovementService.getByType(MovementType.SALIDA))
                .thenReturn(List.of(sampleMovement(2, MovementType.SALIDA)));

        mockMvc.perform(get("/api/stock-movements/type/SALIDA")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("SALIDA"));
    }
}
