package com.ogc_prototype.ogc.mapper;

import com.ogc_prototype.ogc.dto.request.CustomerRequest;
import com.ogc_prototype.ogc.dto.response.CustomerResponse;
import com.ogc_prototype.ogc.model.Customer;
import com.ogc_prototype.ogc.model.enums.Role;

public class CustomerMapper {

    private CustomerMapper() {}

    public static CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder().id(customer.getId()).name(customer.getName())
                .lastName(customer.getLastName()).email(customer.getEmail())
                .userName(customer.getUserName()).role(customer.getRole())
                .phoneNumber(customer.getPhoneNumber()).address(customer.getAddress())
                .city(customer.getCity()).zipCode(customer.getZipCode())
                .country(customer.getCountry()).newsletterSubscribed(customer.isNewsletterSubscribed())
                .createdAt(customer.getCreatedAt()).updatedAt(customer.getUpdatedAt()).build();
    }

    // La contraseña NO se asigna aqui
    public static Customer toEntity(CustomerRequest request) {
        return Customer.builder().name(request.getName()).lastName(request.getLastName())
                .email(request.getEmail()).userName(request.getUserName()).role(Role.CUSTOMER)
                .phoneNumber(request.getPhoneNumber()).address(request.getAddress())
                .city(request.getCity()).zipCode(request.getZipCode()).country(request.getCountry())
                .newsletterSubscribed(Boolean.TRUE.equals(request.getNewsletterSubscribed()))
                .build();
    }
}
