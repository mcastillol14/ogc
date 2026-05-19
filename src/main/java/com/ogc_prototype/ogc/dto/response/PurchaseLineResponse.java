package com.ogc_prototype.ogc.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseLineResponse {
    private Integer id;
    private Integer purchaseId;
    private Integer productId;
    private String productName;
    private String productSku;
    private Double orderedWeightKg;
    private Double unitPricePerKg;
    private Integer lotId; // null until the purchase is received
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

