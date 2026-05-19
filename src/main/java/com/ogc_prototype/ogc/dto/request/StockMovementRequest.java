package com.ogc_prototype.ogc.dto.request;

import com.ogc_prototype.ogc.model.enums.MovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementRequest {

    @NotNull
    private Integer lotId;

    @NotNull
    private MovementType type;

    @NotNull
    @Positive
    private Double weightKg;

    private Integer purchaseId;

    private Integer saleId;

    private String notes;
}

