package com.ogc_prototype.ogc.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryAdjustmentResponse {
    private Integer id;
    private String reason;
}

