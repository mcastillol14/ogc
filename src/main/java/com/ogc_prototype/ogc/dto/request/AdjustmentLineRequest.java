package com.ogc_prototype.ogc.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size(max = 500)
    private String notes;
}

