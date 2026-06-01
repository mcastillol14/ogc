package com.ogc_prototype.ogc.service.compras;

import com.ogc_prototype.ogc.dto.request.PurchaseRequest;
import com.ogc_prototype.ogc.dto.response.PurchaseResponse;

import java.util.List;

public interface PurchaseService {
    List<PurchaseResponse> getAll();

    List<PurchaseResponse> getAllOrderedByDate();

    PurchaseResponse getById(Integer id);

    PurchaseResponse create(PurchaseRequest request);

    PurchaseResponse update(Integer id, PurchaseRequest request);
}
