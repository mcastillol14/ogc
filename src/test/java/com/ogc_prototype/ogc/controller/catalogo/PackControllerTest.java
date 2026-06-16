package com.ogc_prototype.ogc.controller.catalogo;

import com.ogc_prototype.ogc.dto.response.PackResponse;
import com.ogc_prototype.ogc.exception.GlobalExceptionHandler;
import com.ogc_prototype.ogc.exception.PackException;
import com.ogc_prototype.ogc.service.catalogo.PackService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PackControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PackService packService;

    @InjectMocks
    private PackController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private PackResponse samplePack(int id, boolean active) {
        return PackResponse.builder().id(id).name("Pack Relajante")
                .description("Combinación de productos CBD").price(29.99).products(List.of())
                .active(active).build();
    }

    // ─── GET /api/packs ───

    @Test
    void getAll_returnsActiveList_200() throws Exception {
        when(packService.getAllActive()).thenReturn(List.of(samplePack(1, true)));

        mockMvc.perform(get("/api/packs")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    // ─── GET /api/packs/all ───

    @Test
    void getAllAdmin_returnsFullList_200() throws Exception {
        when(packService.getAll()).thenReturn(List.of(samplePack(1, true), samplePack(2, false)));

        mockMvc.perform(get("/api/packs/all")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ─── GET /api/packs/{id} ───

    @Test
    void getById_found_returns200() throws Exception {
        when(packService.getById(1)).thenReturn(samplePack(1, true));

        mockMvc.perform(get("/api/packs/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pack Relajante"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(packService.getById(99)).thenThrow(PackException.notFound(99));

        mockMvc.perform(get("/api/packs/99")).andExpect(status().isNotFound());
    }

    // ─── POST /api/packs ───

    @Test
    void create_validRequest_returns201() throws Exception {
        when(packService.create(any())).thenReturn(samplePack(1, true));

        String body = """
                {"name":"Pack Relajante","price":29.99,"productIds":[1,2],"active":true}
                """;

        mockMvc.perform(post("/api/packs").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_missingName_returns400() throws Exception {
        String body = """
                {"price":29.99,"productIds":[1],"active":true}
                """;

        mockMvc.perform(post("/api/packs").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_emptyProductIds_returns400() throws Exception {
        String body = """
                {"name":"Pack Vacío","price":10.0,"productIds":[],"active":true}
                """;

        mockMvc.perform(post("/api/packs").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    // ─── PUT /api/packs/{id} ───

    @Test
    void update_validRequest_returns200() throws Exception {
        when(packService.update(eq(1), any())).thenReturn(samplePack(1, true));

        String body = """
                {"name":"Pack Actualizado","price":35.0,"productIds":[1,3],"active":true}
                """;

        mockMvc.perform(put("/api/packs/1").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }

    @Test
    void update_notFound_returns404() throws Exception {
        when(packService.update(eq(99), any())).thenThrow(PackException.notFound(99));

        String body = """
                {"name":"Pack Actualizado","price":35.0,"productIds":[1],"active":true}
                """;

        mockMvc.perform(put("/api/packs/99").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    // ─── PATCH /api/packs/{id}/activate ───

    @Test
    void activate_returns200() throws Exception {
        when(packService.activate(1)).thenReturn(samplePack(1, true));

        mockMvc.perform(patch("/api/packs/1/activate")).andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void activate_notFound_returns404() throws Exception {
        when(packService.activate(99)).thenThrow(PackException.notFound(99));

        mockMvc.perform(patch("/api/packs/99/activate")).andExpect(status().isNotFound());
    }

    // ─── PATCH /api/packs/{id}/deactivate ───

    @Test
    void deactivate_returns200() throws Exception {
        when(packService.deactivate(1)).thenReturn(samplePack(1, false));

        mockMvc.perform(patch("/api/packs/1/deactivate")).andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void deactivate_notFound_returns404() throws Exception {
        when(packService.deactivate(99)).thenThrow(PackException.notFound(99));

        mockMvc.perform(patch("/api/packs/99/deactivate")).andExpect(status().isNotFound());
    }
}
