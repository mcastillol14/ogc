package com.ogc_prototype.ogc.controller.almacen;

import com.ogc_prototype.ogc.dto.response.AdjustmentLineResponse;
import com.ogc_prototype.ogc.dto.response.InventoryAdjustmentResponse;
import com.ogc_prototype.ogc.exception.GlobalExceptionHandler;
import com.ogc_prototype.ogc.exception.InventoryAdjustmentException;
import com.ogc_prototype.ogc.service.almacen.InventoryAdjustmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InventoryAdjustmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InventoryAdjustmentService adjustmentService;

    @InjectMocks
    private InventoryAdjustmentController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private InventoryAdjustmentResponse sampleAdjustment(int id) {
        return InventoryAdjustmentResponse.builder().id(id)
                .reason("Corrección de inventario tras conteo físico").build();
    }

    private AdjustmentLineResponse sampleLine(int id, int adjustmentId) {
        return AdjustmentLineResponse.builder().id(id).adjustmentId(adjustmentId).lotId(2)
                .productName("OG Kush CBD").quantityKg(-1.5).notes("Merma detectada").build();
    }

    // ─── GET /api/inventory-adjustments ───

    @Test
    void getAll_returns200WithList() throws Exception {
        when(adjustmentService.getAll())
                .thenReturn(List.of(sampleAdjustment(1), sampleAdjustment(2)));

        mockMvc.perform(get("/api/inventory-adjustments")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].id").value(1));
    }

    // ─── GET /api/inventory-adjustments/{id} ───

    @Test
    void getById_found_returns200() throws Exception {
        when(adjustmentService.getById(1)).thenReturn(sampleAdjustment(1));

        mockMvc.perform(get("/api/inventory-adjustments/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1)).andExpect(
                        jsonPath("$.reason").value("Corrección de inventario tras conteo físico"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(adjustmentService.getById(99)).thenThrow(InventoryAdjustmentException.notFound(99));

        mockMvc.perform(get("/api/inventory-adjustments/99")).andExpect(status().isNotFound());
    }

    // ─── POST /api/inventory-adjustments ───

    @Test
    void create_validRequest_returns201() throws Exception {
        when(adjustmentService.create(any())).thenReturn(sampleAdjustment(1));

        String body = """
                {"reason":"Corrección de inventario tras conteo físico"}
                """;

        mockMvc.perform(post("/api/inventory-adjustments").contentType(MediaType.APPLICATION_JSON)
                .content(body)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_missingReason_returns400() throws Exception {
        String body = """
                {}
                """;

        mockMvc.perform(post("/api/inventory-adjustments").contentType(MediaType.APPLICATION_JSON)
                .content(body)).andExpect(status().isBadRequest());
    }

    @Test
    void create_blankReason_returns400() throws Exception {
        String body = """
                {"reason":"   "}
                """;

        mockMvc.perform(post("/api/inventory-adjustments").contentType(MediaType.APPLICATION_JSON)
                .content(body)).andExpect(status().isBadRequest());
    }

    // ─── GET /api/inventory-adjustments/{id}/lines ───

    @Test
    void getLines_returns200WithList() throws Exception {
        when(adjustmentService.getLines(1)).thenReturn(List.of(sampleLine(1, 1), sampleLine(2, 1)));

        mockMvc.perform(get("/api/inventory-adjustments/1/lines")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].adjustmentId").value(1));
    }

    // ─── GET /api/inventory-adjustments/{id}/lines/{lineId} ───

    @Test
    void getLine_found_returns200() throws Exception {
        when(adjustmentService.getLine(1, 1)).thenReturn(sampleLine(1, 1));

        mockMvc.perform(get("/api/inventory-adjustments/1/lines/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.lotId").value(2));
    }

    @Test
    void getLine_notFound_returns404() throws Exception {
        when(adjustmentService.getLine(1, 99))
                .thenThrow(InventoryAdjustmentException.lineNotFound(99));

        mockMvc.perform(get("/api/inventory-adjustments/1/lines/99"))
                .andExpect(status().isNotFound());
    }

    // ─── POST /api/inventory-adjustments/{id}/lines ───

    @Test
    void addLine_validRequest_returns201() throws Exception {
        when(adjustmentService.addLine(any())).thenReturn(sampleLine(1, 1));

        String body = """
                {"adjustmentId":1,"lotId":2,"quantityKg":-1.5,"notes":"Merma detectada"}
                """;

        mockMvc.perform(post("/api/inventory-adjustments/1/lines")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.adjustmentId").value(1))
                .andExpect(jsonPath("$.quantityKg").value(-1.5));
    }

    @Test
    void addLine_missingFields_returns400() throws Exception {
        String body = """
                {"adjustmentId":1}
                """;

        mockMvc.perform(post("/api/inventory-adjustments/1/lines")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
