package com.ogc_prototype.ogc.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaleLineResponse {
    private Integer id;
    private Integer saleId;
    private Integer lotId;
    private Integer productId;
    private String productName;
    private Double weightKg;
    private Double unitPricePerKg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

