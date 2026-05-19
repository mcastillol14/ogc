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
public class SaleLineRequest {

    @NotNull
    private Integer saleId;

    @NotNull
    private Integer lotId;

    @NotNull
    @Positive
    private Double weightKg;

    @NotNull
    @Positive
    private Double unitPricePerKg;
}

