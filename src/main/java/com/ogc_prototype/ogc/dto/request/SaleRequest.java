package com.ogc_prototype.ogc.dto.request;

import java.time.LocalDate;
import com.ogc_prototype.ogc.model.enums.DiscountMode;
import jakarta.validation.constraints.DecimalMin;
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

    private Integer customerId;

    @NotNull
    @PastOrPresent
    private LocalDate saleDate;

    @Size(max = 500)
    private String notes;

    private Boolean discounted;

    private DiscountMode discountMode;

    @DecimalMin(value = "0.01")
    private Double discountValue;
}

