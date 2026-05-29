package com.ogc_prototype.ogc.mapper;

import com.ogc_prototype.ogc.dto.request.AdjustmentLineRequest;
import com.ogc_prototype.ogc.dto.response.AdjustmentLineResponse;
import com.ogc_prototype.ogc.model.AdjustmentLine;
import com.ogc_prototype.ogc.model.InventoryAdjustment;
import com.ogc_prototype.ogc.model.Lot;

public class AdjustmentLineMapper {

    private AdjustmentLineMapper() {}

    public static AdjustmentLineResponse toResponse(AdjustmentLine line) {
        return AdjustmentLineResponse.builder().id(line.getId())
                .adjustmentId(line.getAdjustment().getId()).lotId(line.getLot().getId())
                .productName(line.getLot().getProduct().getName()).quantityKg(line.getQuantityKg())
                .notes(line.getNotes()).createdAt(line.getCreatedAt()).build();
    }

    public static AdjustmentLine toEntity(AdjustmentLineRequest request,
            InventoryAdjustment adjustment, Lot lot) {
        return AdjustmentLine.builder().adjustment(adjustment).lot(lot)
                .quantityKg(request.getQuantityKg()).notes(request.getNotes()).build();
    }
}
