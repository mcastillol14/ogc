package com.ogc_prototype.ogc.mapper;

import com.ogc_prototype.ogc.dto.response.StockMovementResponse;
import com.ogc_prototype.ogc.model.StockMovement;

public class StockMovementMapper {

    private StockMovementMapper() {}

    public static StockMovementResponse toResponse(StockMovement movement) {
        return StockMovementResponse.builder().id(movement.getId()).lotId(movement.getLot().getId())
                .productName(movement.getLot().getProduct().getName()).type(movement.getType())
                .weightKg(movement.getWeightKg())
                .purchaseId(movement.getPurchase() != null ? movement.getPurchase().getId() : null)
                .saleId(movement.getSale() != null ? movement.getSale().getId() : null)
                .notes(movement.getNotes()).createdAt(movement.getCreatedAt()).build();
    }
}
