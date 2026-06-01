package com.ogc_prototype.ogc.controller.clientes;

import com.ogc_prototype.ogc.dto.response.CustomerResponse;
import com.ogc_prototype.ogc.exception.GlobalExceptionHandler;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.service.clientes.CustomerService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private CustomerResponse sampleCustomer(int id) {
        return CustomerResponse.builder().id(id).name("Alice").lastName("Smith")
                .email("alice@example.com").userName("alice123").role(Role.CUSTOMER)
                .phoneNumber("+34611111111").address("Calle 1").city("Madrid").zipCode(28001)
                .country("ES").build();
    }

    // ─── GET /api/customers (ADMIN only via interceptor — standalone bypasses interceptor) ───

    @Test
    void getAll_returns200WithList() throws Exception {
        when(customerService.getAll()).thenReturn(List.of(sampleCustomer(1)));

        mockMvc.perform(get("/api/customers")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // ─── GET /api/customers/{id} with IDOR logic ───

    @Test
    void getById_adminRole_canAccessAnyCustomer() throws Exception {
        when(customerService.getById(2)).thenReturn(sampleCustomer(2));

        mockMvc.perform(
                get("/api/customers/2").requestAttr("role", Role.ADMIN).requestAttr("userId", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void getById_customerRole_canAccessOwnResource() throws Exception {
        when(customerService.getById(1)).thenReturn(sampleCustomer(1));

        mockMvc.perform(
                get("/api/customers/1").requestAttr("role", Role.CUSTOMER).requestAttr("userId", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_customerRole_cannotAccessOtherCustomer_returns403() throws Exception {
        // CUSTOMER with userId=1 trying to access customer 2 → IDOR prevented
        mockMvc.perform(
                get("/api/customers/2").requestAttr("role", Role.CUSTOMER).requestAttr("userId", 1))
                .andExpect(status().isForbidden());
    }
}
