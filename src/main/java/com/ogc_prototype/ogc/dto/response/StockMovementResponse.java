package com.ogc_prototype.ogc.dto.response;

import java.time.LocalDateTime;
import com.ogc_prototype.ogc.model.enums.MovementType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockMovementResponse {
    private Integer id;
    private Integer lotId;
    private String productName;
    private MovementType type;
    private Double weightKg;
    private Integer purchaseId;
    private Integer saleId;
    private String notes;
    private LocalDateTime createdAt;
}

