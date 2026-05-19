package com.ogc_prototype.ogc.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentLineRequest {

    @NotNull
    private Integer adjustmentId;

    @NotNull
    private Integer lotId;

    @NotNull
    private Double quantityKg;
    
    private String notes;
}

