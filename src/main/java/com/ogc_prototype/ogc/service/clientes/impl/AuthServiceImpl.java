package com.ogc_prototype.ogc.service.clientes.impl;

import com.ogc_prototype.ogc.config.JwtUtils;
import com.ogc_prototype.ogc.config.PasswordManager;
import com.ogc_prototype.ogc.exception.AuthException;
import com.ogc_prototype.ogc.exception.VerificationException;
import com.ogc_prototype.ogc.model.Customer;
import com.ogc_prototype.ogc.model.PasswordHistory;
import com.ogc_prototype.ogc.model.enums.VerificationCodePurpose;
import com.ogc_prototype.ogc.repository.CustomerRepository;
import com.ogc_prototype.ogc.repository.PasswordHistoryRepository;
import com.ogc_prototype.ogc.service.clientes.AuthService;
import com.ogc_prototype.ogc.service.clientes.VerificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordManager passwordManager;
    private final JwtUtils jwtUtils;
    private final VerificationService verificationService;
    private final PasswordHistoryRepository passwordHistoryRepository;

    public AuthServiceImpl(CustomerRepository customerRepository, PasswordManager passwordManager,
            JwtUtils jwtUtils, VerificationService verificationService,
            PasswordHistoryRepository passwordHistoryRepository) {
        this.customerRepository = customerRepository;
        this.passwordManager = passwordManager;
        this.jwtUtils = jwtUtils;
        this.verificationService = verificationService;
        this.passwordHistoryRepository = passwordHistoryRepository;
    }

    @Override
    public String login(String email, String password) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(AuthException::invalidCredentials);
        if (!passwordManager.matches(password, customer.getPassword())) {
            throw AuthException.invalidCredentials();
        }
        if (!customer.isEmailVerified()) {
            throw VerificationException.accountNotVerified();
        }
        return jwtUtils.generateToken(customer.getId(), customer.getUserName(), customer.getRole());
    }

    @Override
    @Transactional
    public void requestPasswordChange(Integer userId) {
        Customer customer =
                customerRepository.findById(userId).orElseThrow(AuthException::invalidCredentials);
        verificationService.sendCode(customer.getEmail(), VerificationCodePurpose.PASSWORD_CHANGE);
    }

    @Override
    @Transactional
    public void confirmPasswordChange(Integer userId, String currentPassword, String code,
            String newPassword) {
        Customer customer =
                customerRepository.findById(userId).orElseThrow(AuthException::invalidCredentials);

        if (!passwordManager.matches(currentPassword, customer.getPassword())) {
            throw AuthException.wrongCurrentPassword();
        }

        verificationService.validateCode(customer.getEmail(), code,
                VerificationCodePurpose.PASSWORD_CHANGE);

        checkPasswordHistory(customer, newPassword);
        applyNewPassword(customer, newPassword);
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        customerRepository.findByEmail(email).ifPresent(customer -> verificationService
                .sendCode(email, VerificationCodePurpose.PASSWORD_RESET));
    }

    @Override
    @Transactional
    public void confirmPasswordReset(String email, String code, String newPassword) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(AuthException::invalidCredentials);

        verificationService.validateCode(email, code, VerificationCodePurpose.PASSWORD_RESET);

        checkPasswordHistory(customer, newPassword);
        applyNewPassword(customer, newPassword);
    }


    private void checkPasswordHistory(Customer customer, String newPassword) {
        List<PasswordHistory> history = passwordHistoryRepository
                .findTop5ByCustomerIdOrderByCreatedAtDesc(customer.getId());
        boolean reused = history.stream()
                .anyMatch(h -> passwordManager.matches(newPassword, h.getHashedPassword()));
        if (reused) {
            throw AuthException.passwordRecentlyUsed();
        }
    }

    private void applyNewPassword(Customer customer, String newPassword) {
        String hashed = passwordManager.encode(newPassword);
        passwordHistoryRepository
                .save(PasswordHistory.builder().customer(customer).hashedPassword(hashed).build());
        customer.setPassword(hashed);
        customerRepository.save(customer);
    }
}

