package com.ogc_prototype.ogc.dto.response;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseResponse {
    private Integer id;
    private Integer providerId;
    private String providerName;
    private LocalDate expectedDate;
    private LocalDate receivedDate;
    private Double totalAmount;
    private String notes;
}

