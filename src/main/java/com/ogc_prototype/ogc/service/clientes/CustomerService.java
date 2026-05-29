package com.ogc_prototype.ogc.service.clientes;

import com.ogc_prototype.ogc.dto.request.CustomerRequest;
import com.ogc_prototype.ogc.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {
    List<CustomerResponse> getAll();

    CustomerResponse getById(Integer id);

    CustomerResponse create(CustomerRequest request);

    CustomerResponse update(Integer id, CustomerRequest request);
}
