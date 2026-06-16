package com.ogc_prototype.ogc.controller.compras;

import com.ogc_prototype.ogc.dto.response.PurchaseLineResponse;
import com.ogc_prototype.ogc.dto.response.PurchaseResponse;
import com.ogc_prototype.ogc.exception.GlobalExceptionHandler;
import com.ogc_prototype.ogc.exception.PurchaseException;
import com.ogc_prototype.ogc.service.compras.PurchaseLineService;
import com.ogc_prototype.ogc.service.compras.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PurchaseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PurchaseService purchaseService;

    @Mock
    private PurchaseLineService purchaseLineService;

    @InjectMocks
    private PurchaseController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private PurchaseResponse samplePurchase(int id) {
        return PurchaseResponse.builder().id(id).providerId(1).providerName("Proveedor CBD")
                .expectedDate(LocalDate.now().plusDays(7)).totalAmount(500.0).build();
    }

    private PurchaseLineResponse sampleLine(int id, int purchaseId) {
        return PurchaseLineResponse.builder().id(id).purchaseId(purchaseId).productId(1)
                .productName("OG Kush CBD").orderedWeightKg(10.0).unitPricePerKg(50.0).build();
    }

    // ─── GET /api/purchases ───

    @Test
    void getAll_returns200WithList() throws Exception {
        when(purchaseService.getAll()).thenReturn(List.of(samplePurchase(1)));

        mockMvc.perform(get("/api/purchases")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // ─── GET /api/purchases/recent ───

    @Test
    void getAllOrderedByDate_returns200WithList() throws Exception {
        when(purchaseService.getAllOrderedByDate())
                .thenReturn(List.of(samplePurchase(1), samplePurchase(2)));

        mockMvc.perform(get("/api/purchases/recent")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ─── GET /api/purchases/{id} ───

    @Test
    void getById_found_returns200() throws Exception {
        when(purchaseService.getById(1)).thenReturn(samplePurchase(1));

        mockMvc.perform(get("/api/purchases/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.providerId").value(1));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(purchaseService.getById(99)).thenThrow(PurchaseException.notFound(99));

        mockMvc.perform(get("/api/purchases/99")).andExpect(status().isNotFound());
    }

    // ─── POST /api/purchases ───

    @Test
    void create_validRequest_returns201() throws Exception {
        when(purchaseService.create(any())).thenReturn(samplePurchase(1));

        String body = """
                {"providerId":1,"expectedDate":"2030-06-01","notes":"Pedido de prueba"}
                """;

        mockMvc.perform(
                post("/api/purchases").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.providerId").value(1));
    }

    @Test
    void create_missingFields_returns400() throws Exception {
        String body = """
                {}
                """;

        mockMvc.perform(
                post("/api/purchases").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    // ─── PUT /api/purchases/{id} ───

    @Test
    void update_validRequest_returns200() throws Exception {
        when(purchaseService.update(eq(1), any())).thenReturn(samplePurchase(1));

        String body = """
                {"providerId":1,"expectedDate":"2030-06-01"}
                """;

        mockMvc.perform(
                put("/api/purchases/1").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }

    @Test
    void update_notFound_returns404() throws Exception {
        when(purchaseService.update(eq(99), any())).thenThrow(PurchaseException.notFound(99));

        String body = """
                {"providerId":1,"expectedDate":"2030-06-01"}
                """;

        mockMvc.perform(
                put("/api/purchases/99").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    // ─── GET /api/purchases/{id}/lines ───

    @Test
    void getLines_returns200WithList() throws Exception {
        when(purchaseLineService.getByPurchaseId(1)).thenReturn(List.of(sampleLine(1, 1)));

        mockMvc.perform(get("/api/purchases/1/lines")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].purchaseId").value(1));
    }

    // ─── GET /api/purchases/{id}/lines/{lineId} ───

    @Test
    void getLine_found_returns200() throws Exception {
        when(purchaseLineService.getById(1, 1)).thenReturn(sampleLine(1, 1));

        mockMvc.perform(get("/api/purchases/1/lines/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getLine_notFound_returns404() throws Exception {
        when(purchaseLineService.getById(1, 99)).thenThrow(PurchaseException.lineNotFound(99));

        mockMvc.perform(get("/api/purchases/1/lines/99")).andExpect(status().isNotFound());
    }

    // ─── POST /api/purchases/{id}/lines ───

    @Test
    void addLine_validRequest_returns201() throws Exception {
        when(purchaseLineService.create(any())).thenReturn(sampleLine(1, 1));

        String body = """
                {"purchaseId":1,"productId":2,"orderedWeightKg":5.0,"unitPricePerKg":40.0}
                """;

        mockMvc.perform(post("/api/purchases/1/lines").contentType(MediaType.APPLICATION_JSON)
                .content(body)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.purchaseId").value(1));
    }

    @Test
    void addLine_missingFields_returns400() throws Exception {
        String body = """
                {"purchaseId":1}
                """;

        mockMvc.perform(post("/api/purchases/1/lines").contentType(MediaType.APPLICATION_JSON)
                .content(body)).andExpect(status().isBadRequest());
    }

    // ─── PATCH /api/purchases/{id}/lines/{lineId}/assign-lot/{lotId} ───

    @Test
    void assignLot_returns200() throws Exception {
        when(purchaseLineService.assignLot(1, 1, 5)).thenReturn(sampleLine(1, 1));

        mockMvc.perform(patch("/api/purchases/1/lines/1/assign-lot/5")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // ─── DELETE /api/purchases/{id}/lines/{lineId} ───

    @Test
    void deleteLine_returns204() throws Exception {
        doNothing().when(purchaseLineService).delete(1, 1);

        mockMvc.perform(delete("/api/purchases/1/lines/1")).andExpect(status().isNoContent());
    }
}
