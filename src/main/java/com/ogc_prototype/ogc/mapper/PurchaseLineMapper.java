package com.ogc_prototype.ogc.mapper;

import com.ogc_prototype.ogc.dto.request.PurchaseLineRequest;
import com.ogc_prototype.ogc.dto.response.PurchaseLineResponse;
import com.ogc_prototype.ogc.model.Product;
import com.ogc_prototype.ogc.model.Purchase;
import com.ogc_prototype.ogc.model.PurchaseLine;

public class PurchaseLineMapper {

    private PurchaseLineMapper() {}

    public static PurchaseLineResponse toResponse(PurchaseLine line) {
        return PurchaseLineResponse.builder().id(line.getId())
                .purchaseId(line.getPurchase().getId()).productId(line.getProduct().getId())
                .productName(line.getProduct().getName()).orderedWeightKg(line.getOrderedWeightKg())
                .unitPricePerKg(line.getUnitPricePerKg())
                .lotId(line.getLot() != null ? line.getLot().getId() : null)
                .createdAt(line.getCreatedAt()).updatedAt(line.getUpdatedAt()).build();
    }

    public static PurchaseLine toEntity(PurchaseLineRequest request, Purchase purchase,
            Product product) {
        return PurchaseLine.builder().purchase(purchase).product(product)
                .orderedWeightKg(request.getOrderedWeightKg())
                .unitPricePerKg(request.getUnitPricePerKg()).build();
    }
}
