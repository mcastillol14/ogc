package com.ogc_prototype.ogc.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    @PositiveOrZero
    private Double price;

    @NotNull
    @PositiveOrZero
    @DecimalMax("100.0")
    private Double cbdPercentage;

    @NotNull
    @PositiveOrZero
    @DecimalMax("100.0")
    private Double thcPercentage;

    @NotNull
    @PositiveOrZero
    @DecimalMax("100.0")
    private Double ohTenPercentage;

    @NotNull
    @PositiveOrZero
    @DecimalMax("100.0")
    private Double msPercentage;

    @NotNull
    @PositiveOrZero
    @DecimalMax("100.0")
    private Double nano10Percentage;

    @NotNull
    @PositiveOrZero
    @DecimalMax("100.0")
    private Double deltaHcPercentage;

    @Builder.Default
    private boolean active = true;
}

