package com.ogc_prototype.ogc.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryAdjustmentResponse {
    private Integer id;
    private String reason;
    private LocalDateTime createdAt;
}

