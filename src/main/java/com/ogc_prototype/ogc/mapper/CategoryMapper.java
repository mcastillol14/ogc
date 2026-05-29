package com.ogc_prototype.ogc.mapper;

import com.ogc_prototype.ogc.dto.request.CategoryRequest;
import com.ogc_prototype.ogc.dto.response.CategoryResponse;
import com.ogc_prototype.ogc.model.Category;

public class CategoryMapper {

    private CategoryMapper() {}

    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder().id(category.getId()).name(category.getName())
                .slug(category.getSlug()).description(category.getDescription())
                .active(category.isActive()).createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt()).build();
    }

    public static Category toEntity(CategoryRequest request) {
        return Category.builder().name(request.getName()).slug(request.getSlug())
                .description(request.getDescription()).active(request.isActive()).build();
    }
}
