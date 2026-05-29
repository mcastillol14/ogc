package com.ogc_prototype.ogc.service.compras;

import com.ogc_prototype.ogc.dto.request.PurchaseLineRequest;
import com.ogc_prototype.ogc.dto.response.PurchaseLineResponse;

import java.util.List;

public interface PurchaseLineService {
    List<PurchaseLineResponse> getByPurchaseId(Integer purchaseId);

    PurchaseLineResponse getById(Integer purchaseId, Integer lineId);

    PurchaseLineResponse create(PurchaseLineRequest request);

    PurchaseLineResponse assignLot(Integer purchaseId, Integer lineId, Integer lotId);

    void delete(Integer purchaseId, Integer lineId);
}
