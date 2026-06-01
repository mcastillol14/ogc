package com.ogc_prototype.ogc.controller.catalogo;

import com.ogc_prototype.ogc.dto.request.CategoryRequest;
import com.ogc_prototype.ogc.dto.response.CategoryResponse;
import com.ogc_prototype.ogc.exception.CategoryException;
import com.ogc_prototype.ogc.exception.GlobalExceptionHandler;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.service.catalogo.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController controller;

    private CategoryResponse sampleResponse() {
        return CategoryResponse.builder().id(1).name("Indica").slug("indica").active(true).build();
    }

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()).setValidator(validator).build();
    }

    // ─── GET /api/categories ───

    @Test
    void getAll_returnsActiveCategoriesList() throws Exception {
        when(categoryService.getAllActive()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/categories")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Indica"));
    }

    @Test
    void getAll_empty_returns200WithEmptyArray() throws Exception {
        when(categoryService.getAllActive()).thenReturn(List.of());

        mockMvc.perform(get("/api/categories")).andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ─── GET /api/categories/{id} ───

    @Test
    void getById_found_returns200() throws Exception {
        when(categoryService.getById(1)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/categories/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(categoryService.getById(99)).thenThrow(CategoryException.notFound(99));

        mockMvc.perform(get("/api/categories/99")).andExpect(status().isNotFound());
    }

    // ─── POST /api/categories ───

    @Test
    void create_validRequest_returns201() throws Exception {
        when(categoryService.create(any())).thenReturn(sampleResponse());

        String body = """
                {"name":"Indica","slug":"indica","description":"desc","active":true}
                """;

        mockMvc.perform(
                post("/api/categories").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("Indica"));
    }

    @Test
    void create_missingRequiredField_returns400() throws Exception {
        String body = """
                {"slug":"indica"}
                """; // missing name

        mockMvc.perform(
                post("/api/categories").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_duplicateSlug_returns409() throws Exception {
        when(categoryService.create(any())).thenThrow(CategoryException.duplicateSlug("indica"));

        String body = """
                {"name":"Indica","slug":"indica","active":true}
                """;

        mockMvc.perform(
                post("/api/categories").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    // ─── PUT /api/categories/{id} ───

    @Test
    void update_validRequest_returns200() throws Exception {
        when(categoryService.update(eq(1), any())).thenReturn(sampleResponse());

        String body = """
                {"name":"Indica Updated","slug":"indica","active":true}
                """;

        mockMvc.perform(
                put("/api/categories/1").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }

    // ─── PATCH activate/deactivate ───

    @Test
    void activate_returns200() throws Exception {
        when(categoryService.activate(1)).thenReturn(sampleResponse());

        mockMvc.perform(patch("/api/categories/1/activate")).andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void deactivate_returns200() throws Exception {
        CategoryResponse inactive = CategoryResponse.builder().id(1).name("Indica").slug("indica")
                .active(false).build();
        when(categoryService.deactivate(1)).thenReturn(inactive);

        mockMvc.perform(patch("/api/categories/1/deactivate")).andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}
