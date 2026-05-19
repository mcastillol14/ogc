package com.ogc_prototype.ogc.dto.request;

import java.util.Date;
import jakarta.validation.constraints.Future;
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
public class LotRequest {

    @NotNull
    private Integer productId;

    @NotNull
    private Integer categoryId;

    @NotNull
    private Integer providerId;

    @NotNull
    @Positive
    private Double weight;

    @NotNull
    @Future
    private Date expirationDate;

    private String description;
}

