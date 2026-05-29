package com.ogc_prototype.ogc.service.clientes.impl;

import com.ogc_prototype.ogc.config.JwtUtils;
import com.ogc_prototype.ogc.config.PasswordManager;
import com.ogc_prototype.ogc.exception.AuthException;
import com.ogc_prototype.ogc.model.Customer;
import com.ogc_prototype.ogc.repository.CustomerRepository;
import com.ogc_prototype.ogc.service.clientes.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordManager passwordManager;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(CustomerRepository customerRepository, PasswordManager passwordManager,
            JwtUtils jwtUtils) {
        this.customerRepository = customerRepository;
        this.passwordManager = passwordManager;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public String login(String email, String password) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(AuthException::invalidCredentials);
        if (!passwordManager.matches(password, customer.getPassword())) {
            throw AuthException.invalidCredentials();
        }
        return jwtUtils.generateToken(customer.getId(), customer.getUserName(), customer.getRole());
    }
}
