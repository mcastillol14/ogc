package com.ogc_prototype.ogc.dto.request;

import java.time.LocalDate;
import jakarta.validation.constraints.FutureOrPresent;
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
public class PurchaseRequest {

    @NotNull
    private Integer providerId;

    @NotNull
    @FutureOrPresent
    private LocalDate expectedDate;

    @Size(max = 500)
    private String notes;
}

