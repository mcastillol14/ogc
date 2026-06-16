package com.ogc_prototype.ogc.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PackResponse {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private List<PackProductItem> products;
    private boolean active;

    @Data
    @Builder
    public static class PackProductItem {
        private Integer id;
        private String name;
        private Double cbdPercentage;
        private Double thcPercentage;
    }
}
