package com.ogc_prototype.ogc.service.ventas;

import com.ogc_prototype.ogc.dto.request.SaleRequest;
import com.ogc_prototype.ogc.dto.response.SaleResponse;

import java.util.List;

public interface SaleService {
    List<SaleResponse> getAll();

    SaleResponse getById(Integer id);

    SaleResponse create(SaleRequest request);

    SaleResponse update(Integer id, SaleRequest request);
}
