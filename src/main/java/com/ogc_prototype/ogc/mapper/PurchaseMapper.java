package com.ogc_prototype.ogc.mapper;

import com.ogc_prototype.ogc.dto.request.PurchaseRequest;
import com.ogc_prototype.ogc.dto.response.PurchaseResponse;
import com.ogc_prototype.ogc.model.Provider;
import com.ogc_prototype.ogc.model.Purchase;

public class PurchaseMapper {

    private PurchaseMapper() {}

    public static PurchaseResponse toResponse(Purchase purchase) {
        return PurchaseResponse.builder().id(purchase.getId())
                .providerId(purchase.getProvider().getId())
                .providerName(purchase.getProvider().getName())
                .expectedDate(purchase.getExpectedDate()).receivedDate(purchase.getReceivedDate())
                .totalAmount(purchase.getTotalAmount()).notes(purchase.getNotes()).build();
    }

    public static Purchase toEntity(PurchaseRequest request, Provider provider) {
        return Purchase.builder().provider(provider).expectedDate(request.getExpectedDate())
                .notes(request.getNotes()).build();
    }
}
