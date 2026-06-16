package com.ogc_prototype.ogc.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private Integer id;
    private String name;
    private String slug;
    private String description;
    private boolean active;
}

