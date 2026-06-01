package com.ogc_prototype.ogc.service.clientes.impl;

import com.ogc_prototype.ogc.config.PasswordManager;
import com.ogc_prototype.ogc.dto.request.CustomerRequest;
import com.ogc_prototype.ogc.dto.response.CustomerResponse;
import com.ogc_prototype.ogc.exception.CustomerException;
import com.ogc_prototype.ogc.mapper.CustomerMapper;
import com.ogc_prototype.ogc.model.Customer;
import com.ogc_prototype.ogc.repository.CustomerRepository;
import com.ogc_prototype.ogc.service.clientes.CustomerService;
import com.ogc_prototype.ogc.service.clientes.VerificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordManager passwordManager;
    private final VerificationService verificationService;

    public CustomerServiceImpl(CustomerRepository customerRepository,
            PasswordManager passwordManager, VerificationService verificationService) {
        this.customerRepository = customerRepository;
        this.passwordManager = passwordManager;
        this.verificationService = verificationService;
    }

    @Override
    public List<CustomerResponse> getAll() {
        return customerRepository.findAll().stream().map(CustomerMapper::toResponse).toList();
    }

    @Override
    public CustomerResponse getById(Integer id) {
        Customer customer =
                customerRepository.findById(id).orElseThrow(() -> CustomerException.notFound(id));
        return CustomerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw CustomerException.duplicateEmail(request.getEmail());
        }
        if (customerRepository.existsByUserName(request.getUserName())) {
            throw CustomerException.duplicateUsername(request.getUserName());
        }
        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw CustomerException.duplicatePhone(request.getPhoneNumber());
        }
        Customer customer = CustomerMapper.toEntity(request);
        customer.setPassword(passwordManager.encode(request.getPassword()));
        CustomerResponse response = CustomerMapper.toResponse(customerRepository.save(customer));
        verificationService.sendCode(customer.getEmail());
        return response;
    }

    @Override
    @Transactional
    public CustomerResponse update(Integer id, CustomerRequest request) {
        Customer customer =
                customerRepository.findById(id).orElseThrow(() -> CustomerException.notFound(id));
        if (!customer.getEmail().equals(request.getEmail())
                && customerRepository.existsByEmail(request.getEmail())) {
            throw CustomerException.duplicateEmail(request.getEmail());
        }
        if (!customer.getUserName().equals(request.getUserName())
                && customerRepository.existsByUserName(request.getUserName())) {
            throw CustomerException.duplicateUsername(request.getUserName());
        }
        if (!customer.getPhoneNumber().equals(request.getPhoneNumber())
                && customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw CustomerException.duplicatePhone(request.getPhoneNumber());
        }
        customer.setName(request.getName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setUserName(request.getUserName());
        customer.setPassword(passwordManager.encode(request.getPassword()));
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setZipCode(request.getZipCode());
        customer.setCountry(request.getCountry());
        customer.setNewsletterSubscribed(Boolean.TRUE.equals(request.getNewsletterSubscribed()));
        return CustomerMapper.toResponse(customerRepository.save(customer));
    }
}
