package com.ogc_prototype.ogc.service.clientes;

import com.ogc_prototype.ogc.dto.request.CustomerRequest;
import com.ogc_prototype.ogc.dto.response.CustomerResponse;
import com.ogc_prototype.ogc.model.enums.Role;

import java.util.List;

public interface CustomerService {
    List<CustomerResponse> getAll();

    CustomerResponse getById(Integer id);

    CustomerResponse create(CustomerRequest request);

    CustomerResponse update(Integer id, CustomerRequest request);

    CustomerResponse changeRole(Integer id, Role newRole);
}
