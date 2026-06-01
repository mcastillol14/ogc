package com.ogc_prototype.ogc.controller.clientes;

import com.ogc_prototype.ogc.exception.AuthException;
import com.ogc_prototype.ogc.exception.GlobalExceptionHandler;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.service.clientes.AuthService;
import com.ogc_prototype.ogc.service.clientes.CustomerService;
import com.ogc_prototype.ogc.dto.response.CustomerResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AuthController controller;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()).setValidator(validator).build();
    }

    // ─── POST /api/auth/login ───

    @Test
    void login_validCredentials_returns200WithToken() throws Exception {
        when(authService.login("alice@example.com", "somePassword")).thenReturn("jwt-token-abc");

        String body = """
                {"email":"alice@example.com","password":"somePassword"}
                """;

        mockMvc.perform(
                post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").value("jwt-token-abc"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        when(authService.login(anyString(), anyString()))
                .thenThrow(AuthException.invalidCredentials());

        String body = """
                {"email":"wrong@example.com","password":"badPassword"}
                """;

        mockMvc.perform(
                post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_missingEmail_returns400() throws Exception {
        String body = """
                {"password":"somePassword"}
                """;

        mockMvc.perform(
                post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_invalidEmailFormat_returns400() throws Exception {
        String body = """
                {"email":"not-an-email","password":"somePassword"}
                """;

        mockMvc.perform(
                post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_missingPassword_returns400() throws Exception {
        String body = """
                {"email":"alice@example.com"}
                """;

        mockMvc.perform(
                post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    // ─── POST /api/auth/register ───

    @Test
    void register_validRequest_returns201() throws Exception {
        CustomerResponse created = CustomerResponse.builder().id(1).name("Alice").lastName("Smith")
                .email("alice@example.com").userName("alice123").role(Role.CUSTOMER)
                .phoneNumber("+34611111111").address("Calle 1").city("Madrid").zipCode(28001)
                .country("ES").build();
        when(customerService.create(any())).thenReturn(created);

        String body = """
                {
                  "name":"Alice",
                  "lastName":"Smith",
                  "email":"alice@example.com",
                  "userName":"alice123",
                  "password":"passwordMustBe24CharsLong!!",
                  "phoneNumber":"+34611111111",
                  "address":"Calle 1",
                  "city":"Madrid",
                  "zipCode":28001,
                  "country":"ES"
                }
                """;

        mockMvc.perform(
                post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void register_missingRequiredField_returns400() throws Exception {
        String body = """
                {"email":"alice@example.com"}
                """; // many required fields missing

        mockMvc.perform(
                post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
