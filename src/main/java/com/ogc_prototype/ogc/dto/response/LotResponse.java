package com.ogc_prototype.ogc.dto.response;

import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LotResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer categoryId;
    private String categoryName;
    private Integer providerId;
    private String providerName;
    private Double weight;
    private Date expirationDate;
    private String description;
    private Double remainingWeight;
}

