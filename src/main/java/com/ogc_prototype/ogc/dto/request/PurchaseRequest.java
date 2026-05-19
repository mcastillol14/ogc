package com.ogc_prototype.ogc.dto.request;

import java.time.LocalDate;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequest {

    @NotNull
    private Integer providerId;

    @NotNull
    @FutureOrPresent
    private LocalDate expectedDate;

    private String notes;
}

