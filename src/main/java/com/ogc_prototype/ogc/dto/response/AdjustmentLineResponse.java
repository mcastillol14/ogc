package com.ogc_prototype.ogc.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdjustmentLineResponse {
    private Integer id;
    private Integer adjustmentId;
    private Integer lotId;
    private String productName;
    private Double quantityKg;
    private String notes;
}

