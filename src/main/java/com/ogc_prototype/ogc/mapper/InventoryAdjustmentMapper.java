package com.ogc_prototype.ogc.mapper;

import com.ogc_prototype.ogc.dto.request.InventoryAdjustmentRequest;
import com.ogc_prototype.ogc.dto.response.InventoryAdjustmentResponse;
import com.ogc_prototype.ogc.model.InventoryAdjustment;

public class InventoryAdjustmentMapper {

    private InventoryAdjustmentMapper() {}

    public static InventoryAdjustmentResponse toResponse(InventoryAdjustment adjustment) {
        return InventoryAdjustmentResponse.builder().id(adjustment.getId())
                .reason(adjustment.getReason()).createdAt(adjustment.getCreatedAt()).build();
    }

    public static InventoryAdjustment toEntity(InventoryAdjustmentRequest request) {
        return InventoryAdjustment.builder().reason(request.getReason()).build();
    }
}
