package com.ogc_prototype.ogc.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private Integer id;
    private String skuCode;
    private String name;
    private String description;
    private Double price;
    private Double cbdPercentage;
    private Double thcPercentage;
    private Double cbgPercentage;
    private Double cbnPercentage;
    private boolean active;
    private Double stock; // computed: sum of LotStock.remainingWeight for this product
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

