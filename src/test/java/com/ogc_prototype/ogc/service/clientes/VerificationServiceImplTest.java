package com.ogc_prototype.ogc.service.clientes;

import com.ogc_prototype.ogc.exception.CustomerException;
import com.ogc_prototype.ogc.exception.VerificationException;
import com.ogc_prototype.ogc.model.Customer;
import com.ogc_prototype.ogc.model.VerificationCode;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.model.enums.VerificationCodePurpose;
import com.ogc_prototype.ogc.repository.CustomerRepository;
import com.ogc_prototype.ogc.repository.VerificationCodeRepository;
import com.ogc_prototype.ogc.service.clientes.impl.VerificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceImplTest {

    @Mock
    private VerificationCodeRepository codeRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private VerificationServiceImpl verificationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(verificationService, "expiryMinutes", 15);
    }

    private Customer buildCustomer(boolean emailVerified) {
        return Customer.builder().id(1).name("Alice").lastName("Smith").email("alice@example.com")
                .userName("alice123").password("$2a$10$hash").role(Role.CUSTOMER)
                .phoneNumber("+34600000001").address("Calle 1").city("Madrid").zipCode("28001")
                .country("ES").emailVerified(emailVerified).build();
    }

    private VerificationCode buildCode(String code, VerificationCodePurpose purpose,
            LocalDateTime expiresAt, boolean used) {
        return VerificationCode.builder().id(1).email("alice@example.com").code(code)
                .purpose(purpose).expiresAt(expiresAt).used(used).build();
    }

    // ---- sendCode(email) ----

    @Test
    void sendCode_email_delegatesToEmailVerificationPurpose() {
        verificationService.sendCode("alice@example.com");

        verify(codeRepository).deleteByEmailAndPurpose("alice@example.com",
                VerificationCodePurpose.EMAIL_VERIFICATION);
        verify(codeRepository).save(any(VerificationCode.class));
        verify(emailService).sendVerificationCode(eq("alice@example.com"), any());
    }

    // ---- sendCode(email, purpose) ----

    @Test
    void sendCode_withPurpose_deletesOldAndSavesNewAndSendsEmail() {
        verificationService.sendCode("alice@example.com", VerificationCodePurpose.PASSWORD_RESET);

        verify(codeRepository).deleteByEmailAndPurpose("alice@example.com",
                VerificationCodePurpose.PASSWORD_RESET);

        ArgumentCaptor<VerificationCode> captor = ArgumentCaptor.forClass(VerificationCode.class);
        verify(codeRepository).save(captor.capture());
        VerificationCode saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("alice@example.com");
        assertThat(saved.getPurpose()).isEqualTo(VerificationCodePurpose.PASSWORD_RESET);
        assertThat(saved.getCode()).matches("\\d{6}");
        assertThat(saved.getExpiresAt()).isAfter(LocalDateTime.now());

        verify(emailService).sendVerificationCode(eq("alice@example.com"), any());
    }

    // ---- resendVerificationCode ----

    @Test
    void resendVerificationCode_customerNotFound_throwsCustomerException() {
        when(customerRepository.findByEmail("unknown@x.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> verificationService.resendVerificationCode("unknown@x.com"))
                .isInstanceOf(CustomerException.class);

        verify(codeRepository, never()).save(any());
        verify(emailService, never()).sendVerificationCode(any(), any());
    }

    @Test
    void resendVerificationCode_alreadyVerified_throwsVerificationException() {
        when(customerRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(buildCustomer(true)));

        assertThatThrownBy(() -> verificationService.resendVerificationCode("alice@example.com"))
                .isInstanceOf(VerificationException.class);

        verify(codeRepository, never()).save(any());
        verify(emailService, never()).sendVerificationCode(any(), any());
    }

    @Test
    void resendVerificationCode_notYetVerified_sendsNewCode() {
        when(customerRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(buildCustomer(false)));

        verificationService.resendVerificationCode("alice@example.com");

        verify(codeRepository).deleteByEmailAndPurpose("alice@example.com",
                VerificationCodePurpose.EMAIL_VERIFICATION);
        verify(codeRepository).save(any(VerificationCode.class));
        verify(emailService).sendVerificationCode(eq("alice@example.com"), any());
    }

    // ---- verifyCode ----

    @Test
    void verifyCode_customerNotFound_throwsCustomerException() {
        when(customerRepository.findByEmail("unknown@x.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> verificationService.verifyCode("unknown@x.com", "123456"))
                .isInstanceOf(CustomerException.class);
    }

    @Test
    void verifyCode_alreadyVerified_throwsVerificationException() {
        when(customerRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(buildCustomer(true)));

        assertThatThrownBy(() -> verificationService.verifyCode("alice@example.com", "123456"))
                .isInstanceOf(VerificationException.class);
    }

    @Test
    void verifyCode_valid_setsEmailVerifiedAndSaves() {
        Customer customer = buildCustomer(false);
        when(customerRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(customer));

        VerificationCode code = buildCode("123456", VerificationCodePurpose.EMAIL_VERIFICATION,
                LocalDateTime.now().plusMinutes(10), false);
        when(codeRepository.findTopByEmailAndPurposeAndUsedFalseOrderByExpiresAtDesc(
                "alice@example.com", VerificationCodePurpose.EMAIL_VERIFICATION))
                        .thenReturn(Optional.of(code));

        verificationService.verifyCode("alice@example.com", "123456");

        assertThat(customer.isEmailVerified()).isTrue();
        verify(customerRepository).save(customer);
        assertThat(code.isUsed()).isTrue();
    }

    // ---- validateCode ----

    @Test
    void validateCode_noCodeFound_throwsVerificationException() {
        when(codeRepository.findTopByEmailAndPurposeAndUsedFalseOrderByExpiresAtDesc(
                "alice@example.com", VerificationCodePurpose.PASSWORD_RESET))
                        .thenReturn(Optional.empty());

        assertThatThrownBy(() -> verificationService.validateCode("alice@example.com", "123456",
                VerificationCodePurpose.PASSWORD_RESET)).isInstanceOf(VerificationException.class);
    }

    @Test
    void validateCode_expiredCode_throwsVerificationException() {
        VerificationCode code = buildCode("123456", VerificationCodePurpose.PASSWORD_RESET,
                LocalDateTime.now().minusMinutes(1), false);
        when(codeRepository.findTopByEmailAndPurposeAndUsedFalseOrderByExpiresAtDesc(
                "alice@example.com", VerificationCodePurpose.PASSWORD_RESET))
                        .thenReturn(Optional.of(code));

        assertThatThrownBy(() -> verificationService.validateCode("alice@example.com", "123456",
                VerificationCodePurpose.PASSWORD_RESET)).isInstanceOf(VerificationException.class);
    }

    @Test
    void validateCode_wrongCode_throwsVerificationException() {
        VerificationCode code = buildCode("999999", VerificationCodePurpose.PASSWORD_RESET,
                LocalDateTime.now().plusMinutes(10), false);
        when(codeRepository.findTopByEmailAndPurposeAndUsedFalseOrderByExpiresAtDesc(
                "alice@example.com", VerificationCodePurpose.PASSWORD_RESET))
                        .thenReturn(Optional.of(code));

        assertThatThrownBy(() -> verificationService.validateCode("alice@example.com", "123456",
                VerificationCodePurpose.PASSWORD_RESET)).isInstanceOf(VerificationException.class);
    }

    @Test
    void validateCode_valid_marksCodeUsedAndSaves() {
        VerificationCode code = buildCode("123456", VerificationCodePurpose.PASSWORD_RESET,
                LocalDateTime.now().plusMinutes(10), false);
        when(codeRepository.findTopByEmailAndPurposeAndUsedFalseOrderByExpiresAtDesc(
                "alice@example.com", VerificationCodePurpose.PASSWORD_RESET))
                        .thenReturn(Optional.of(code));

        verificationService.validateCode("alice@example.com", "123456",
                VerificationCodePurpose.PASSWORD_RESET);

        assertThat(code.isUsed()).isTrue();
        verify(codeRepository).save(code);
    }
}
