package com.ogc_prototype.ogc.service.ventas;

import com.ogc_prototype.ogc.dto.request.SaleLineRequest;
import com.ogc_prototype.ogc.dto.response.SaleLineResponse;

import java.util.List;

public interface SaleLineService {
    List<SaleLineResponse> getBySaleId(Integer saleId);

    SaleLineResponse getById(Integer saleId, Integer lineId);

    SaleLineResponse create(SaleLineRequest request);

    void delete(Integer saleId, Integer lineId);
}
