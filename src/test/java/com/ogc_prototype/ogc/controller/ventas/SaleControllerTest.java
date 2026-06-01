package com.ogc_prototype.ogc.controller.ventas;

import com.ogc_prototype.ogc.dto.response.SaleResponse;
import com.ogc_prototype.ogc.exception.GlobalExceptionHandler;
import com.ogc_prototype.ogc.exception.SaleException;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.service.ventas.SaleLineService;
import com.ogc_prototype.ogc.service.ventas.SaleService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SaleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SaleService saleService;

    @Mock
    private SaleLineService saleLineService;

    @InjectMocks
    private SaleController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private SaleResponse sampleSale(int id, int customerId) {
        return SaleResponse.builder().id(id).customerId(customerId).customerName("Alice Smith")
                .saleDate(LocalDate.now()).totalAmount(0.0).build();
    }

    // ─── GET /api/sales ───

    @Test
    void getAll_returns200WithList() throws Exception {
        when(saleService.getAll()).thenReturn(List.of(sampleSale(1, 10)));

        mockMvc.perform(get("/api/sales")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // ─── GET /api/sales/{id} ───

    @Test
    void getById_adminRole_found_returns200() throws Exception {
        when(saleService.getById(1)).thenReturn(sampleSale(1, 10));

        mockMvc.perform(
                get("/api/sales/1").requestAttr("role", Role.ADMIN).requestAttr("userId", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(saleService.getById(99)).thenThrow(SaleException.notFound(99));

        mockMvc.perform(
                get("/api/sales/99").requestAttr("role", Role.ADMIN).requestAttr("userId", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_customerIDOR_ownSale_returns200() throws Exception {
        when(saleService.getById(1)).thenReturn(sampleSale(1, 5)); // customerId = 5

        mockMvc.perform(
                get("/api/sales/1").requestAttr("role", Role.CUSTOMER).requestAttr("userId", 5)) // same
                                                                                                 // as
                                                                                                 // sale.customerId
                .andExpect(status().isOk());
    }

    @Test
    void getById_customerIDOR_otherCustomerSale_returns403() throws Exception {
        when(saleService.getById(1)).thenReturn(sampleSale(1, 5)); // customerId = 5

        mockMvc.perform(
                get("/api/sales/1").requestAttr("role", Role.CUSTOMER).requestAttr("userId", 9)) // different
                                                                                                 // userId
                                                                                                 // →
                                                                                                 // IDOR
                                                                                                 // blocked
                .andExpect(status().isForbidden());
    }

    // ─── POST /api/sales ───

    @Test
    void create_validRequest_returns201() throws Exception {
        when(saleService.create(any())).thenReturn(sampleSale(1, 10));

        String body = """
                {"customerId":10,"saleDate":"2025-01-01"}
                """;

        mockMvc.perform(post("/api/sales").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.customerId").value(10));
    }

    // ─── PUT /api/sales/{id} ───

    @Test
    void update_validRequest_returns200() throws Exception {
        when(saleService.update(eq(1), any())).thenReturn(sampleSale(1, 10));

        String body = """
                {"customerId":10,"saleDate":"2025-01-01"}
                """;

        mockMvc.perform(put("/api/sales/1").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }
}
