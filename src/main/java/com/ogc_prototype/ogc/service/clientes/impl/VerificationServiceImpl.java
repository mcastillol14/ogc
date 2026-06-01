package com.ogc_prototype.ogc.service.clientes.impl;

import com.ogc_prototype.ogc.exception.CustomerException;
import com.ogc_prototype.ogc.exception.VerificationException;
import com.ogc_prototype.ogc.model.Customer;
import com.ogc_prototype.ogc.model.VerificationCode;
import com.ogc_prototype.ogc.model.enums.VerificationCodePurpose;
import com.ogc_prototype.ogc.repository.CustomerRepository;
import com.ogc_prototype.ogc.repository.VerificationCodeRepository;
import com.ogc_prototype.ogc.service.clientes.EmailService;
import com.ogc_prototype.ogc.service.clientes.VerificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class VerificationServiceImpl implements VerificationService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final VerificationCodeRepository codeRepository;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;

    @Value("${verification.code.expiry-minutes:15}")
    private int expiryMinutes;

    public VerificationServiceImpl(VerificationCodeRepository codeRepository,
            CustomerRepository customerRepository, EmailService emailService) {
        this.codeRepository = codeRepository;
        this.customerRepository = customerRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void sendCode(String email) {
        sendCode(email, VerificationCodePurpose.EMAIL_VERIFICATION);
    }

    @Override
    @Transactional
    public void sendCode(String email, VerificationCodePurpose purpose) {
        codeRepository.deleteByEmailAndPurpose(email, purpose);

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));

        VerificationCode record = VerificationCode.builder().email(email).code(code)
                .purpose(purpose).expiresAt(LocalDateTime.now().plusMinutes(expiryMinutes)).build();
        codeRepository.save(record);

        emailService.sendVerificationCode(email, code);
    }

    @Override
    @Transactional
    public void verifyCode(String email, String code) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> CustomerException.notFound(email));

        if (customer.isEmailVerified()) {
            throw VerificationException.alreadyVerified();
        }

        validateCode(email, code, VerificationCodePurpose.EMAIL_VERIFICATION);

        customer.setEmailVerified(true);
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void validateCode(String email, String code, VerificationCodePurpose purpose) {
        VerificationCode record = codeRepository
                .findTopByEmailAndPurposeAndUsedFalseOrderByExpiresAtDesc(email, purpose)
                .orElseThrow(VerificationException::invalidOrExpired);

        if (LocalDateTime.now().isAfter(record.getExpiresAt())) {
            throw VerificationException.invalidOrExpired();
        }

        if (!record.getCode().equals(code)) {
            throw VerificationException.invalidOrExpired();
        }

        record.setUsed(true);
        codeRepository.save(record);
    }
}
