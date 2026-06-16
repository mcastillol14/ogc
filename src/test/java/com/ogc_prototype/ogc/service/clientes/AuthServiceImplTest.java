package com.ogc_prototype.ogc.service.clientes;

import com.ogc_prototype.ogc.config.JwtUtils;
import com.ogc_prototype.ogc.config.PasswordManager;
import com.ogc_prototype.ogc.exception.AuthException;
import com.ogc_prototype.ogc.model.Customer;
import com.ogc_prototype.ogc.model.PasswordHistory;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.model.enums.VerificationCodePurpose;
import com.ogc_prototype.ogc.repository.CustomerRepository;
import com.ogc_prototype.ogc.repository.PasswordHistoryRepository;
import com.ogc_prototype.ogc.service.clientes.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordManager passwordManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private VerificationService verificationService;

    @Mock
    private PasswordHistoryRepository passwordHistoryRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private Customer buildCustomer() {
        return Customer.builder().id(1).name("Alice").lastName("Smith").email("alice@example.com")
                .userName("alice123").password("$2a$10$hashedPassword").role(Role.CUSTOMER)
                .phoneNumber("+34600000001").address("Calle Mayor 1").city("Madrid")
                .zipCode("28001").country("ES").emailVerified(true).build();
    }

    // ---- login ----

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

    // ---- requestPasswordChange ----

    @Test
    void requestPasswordChange_customerFound_sendsCode() {
        Customer customer = buildCustomer();
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        authService.requestPasswordChange(1);

        verify(verificationService).sendCode("alice@example.com",
                VerificationCodePurpose.PASSWORD_CHANGE);
    }

    @Test
    void requestPasswordChange_customerNotFound_throwsAuthException() {
        when(customerRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.requestPasswordChange(99))
                .isInstanceOf(AuthException.class);
    }

    // ---- confirmPasswordChange ----

    @Test
    void confirmPasswordChange_wrongCurrentPassword_throwsAuthException() {
        Customer customer = buildCustomer();
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(passwordManager.matches("wrongPass", customer.getPassword())).thenReturn(false);

        assertThatThrownBy(
                () -> authService.confirmPasswordChange(1, "wrongPass", "123456", "NewPass1"))
                        .isInstanceOf(AuthException.class);
    }

    @Test
    void confirmPasswordChange_passwordRecentlyUsed_throwsAuthException() {
        Customer customer = buildCustomer();
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(passwordManager.matches("currentRaw", customer.getPassword())).thenReturn(true);

        PasswordHistory old =
                PasswordHistory.builder().customer(customer).hashedPassword("$2a$oldHash").build();
        when(passwordHistoryRepository.findTop5ByCustomerIdOrderByCreatedAtDesc(1))
                .thenReturn(List.of(old));
        when(passwordManager.matches("NewPass1", "$2a$oldHash")).thenReturn(true);

        assertThatThrownBy(
                () -> authService.confirmPasswordChange(1, "currentRaw", "123456", "NewPass1"))
                        .isInstanceOf(AuthException.class);
    }

    @Test
    void confirmPasswordChange_valid_updatesPasswordAndSavesHistory() {
        Customer customer = buildCustomer();
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(passwordManager.matches("currentRaw", customer.getPassword())).thenReturn(true);
        when(passwordHistoryRepository.findTop5ByCustomerIdOrderByCreatedAtDesc(1))
                .thenReturn(List.of());
        when(passwordManager.encode("NewPass1!")).thenReturn("$2a$newHash");

        authService.confirmPasswordChange(1, "currentRaw", "123456", "NewPass1!");

        verify(verificationService).validateCode("alice@example.com", "123456",
                VerificationCodePurpose.PASSWORD_CHANGE);
        verify(passwordHistoryRepository).save(any(PasswordHistory.class));
        assertThat(customer.getPassword()).isEqualTo("$2a$newHash");
        verify(customerRepository).save(customer);
    }

    // ---- requestPasswordReset ----

    @Test
    void requestPasswordReset_emailNotFound_doesNothing() {
        when(customerRepository.findByEmail("unknown@x.com")).thenReturn(Optional.empty());

        authService.requestPasswordReset("unknown@x.com");

        verify(verificationService, never()).sendCode(any(), any());
    }

    @Test
    void requestPasswordReset_emailFound_sendsCode() {
        Customer customer = buildCustomer();
        when(customerRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(customer));

        authService.requestPasswordReset("alice@example.com");

        verify(verificationService).sendCode("alice@example.com",
                VerificationCodePurpose.PASSWORD_RESET);
    }

    // ---- confirmPasswordReset ----

    @Test
    void confirmPasswordReset_emailNotFound_throwsAuthException() {
        when(customerRepository.findByEmail("unknown@x.com")).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> authService.confirmPasswordReset("unknown@x.com", "123456", "NewPass1"))
                        .isInstanceOf(AuthException.class);
    }

    @Test
    void confirmPasswordReset_valid_updatesPasswordAndSavesHistory() {
        Customer customer = buildCustomer();
        when(customerRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(customer));
        when(passwordHistoryRepository.findTop5ByCustomerIdOrderByCreatedAtDesc(1))
                .thenReturn(List.of());
        when(passwordManager.encode("NewPass1!")).thenReturn("$2a$newHash");

        authService.confirmPasswordReset("alice@example.com", "123456", "NewPass1!");

        verify(verificationService).validateCode("alice@example.com", "123456",
                VerificationCodePurpose.PASSWORD_RESET);
        verify(passwordHistoryRepository).save(any(PasswordHistory.class));
        assertThat(customer.getPassword()).isEqualTo("$2a$newHash");
        verify(customerRepository).save(customer);
    }
}

