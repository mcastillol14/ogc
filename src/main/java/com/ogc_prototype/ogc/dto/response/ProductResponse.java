package com.ogc_prototype.ogc.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Double cbdPercentage;
    private Double thcPercentage;
    private Double ohTenPercentage;
    private Double msPercentage;
    private Double nano10Percentage;
    private Double deltaHcPercentage;
    private boolean active;
    private Double stock; 
}

