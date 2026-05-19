package com.ogc_prototype.ogc.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaleResponse {
    private Integer id;
    private Integer customerId;
    private String customerName;
    private LocalDate saleDate;
    private Double totalAmount;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

