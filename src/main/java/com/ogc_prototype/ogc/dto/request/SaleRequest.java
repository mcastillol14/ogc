package com.ogc_prototype.ogc.dto.request;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleRequest {

    @NotNull
    private Integer customerId;

    @NotNull
    @PastOrPresent
    private LocalDate saleDate;

    @Size(max = 500)
    private String notes;
}

