package com.ogc_prototype.ogc.service.clientes;

import com.ogc_prototype.ogc.config.JwtUtils;
import com.ogc_prototype.ogc.config.PasswordManager;
import com.ogc_prototype.ogc.exception.AuthException;
import com.ogc_prototype.ogc.model.Customer;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.repository.CustomerRepository;
import com.ogc_prototype.ogc.service.clientes.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordManager passwordManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    private Customer buildCustomer() {
        return Customer.builder().id(1).name("Alice").lastName("Smith").email("alice@example.com")
                .userName("alice123").password("$2a$10$hashedPassword").role(Role.CUSTOMER)
                .phoneNumber("+34600000001").address("Calle Mayor 1").city("Madrid").zipCode(28001)
                .country("ES").emailVerified(true).build();
    }

    @Test
    void login_validCredentials_returnsToken() {
        Customer customer = buildCustomer();
        when(customerRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(customer));
        when(passwordManager.matches("rawPassword", customer.getPassword())).thenReturn(true);
        when(jwtUtils.generateToken(1, "alice123", Role.CUSTOMER)).thenReturn("jwt-token-abc");

        String token = authService.login("alice@example.com", "rawPassword");

        assertThat(token).isEqualTo("jwt-token-abc");
    }

    @Test
    void login_unknownEmail_throwsAuthException() {
        when(customerRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("unknown@example.com", "anyPassword"))
                .isInstanceOf(AuthException.class);
    }

    @Test
    void login_wrongPassword_throwsAuthException() {
        Customer customer = buildCustomer();
        when(customerRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(customer));
        when(passwordManager.matches("wrongPassword", customer.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login("alice@example.com", "wrongPassword"))
                .isInstanceOf(AuthException.class);
    }
}
