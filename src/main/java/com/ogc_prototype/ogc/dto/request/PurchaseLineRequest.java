package com.ogc_prototype.ogc.dto.request;

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
public class PurchaseLineRequest {

    @NotNull
    private Integer purchaseId;

    @NotNull
    private Integer productId;

    @NotNull
    @Positive
    private Double orderedWeightKg;

    @NotNull
    @Positive
    private Double unitPricePerKg;
}

