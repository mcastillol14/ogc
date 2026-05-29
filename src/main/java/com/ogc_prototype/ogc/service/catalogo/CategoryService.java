package com.ogc_prototype.ogc.service.catalogo;

import com.ogc_prototype.ogc.dto.request.CategoryRequest;
import com.ogc_prototype.ogc.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAll();

    List<CategoryResponse> getAllActive();

    CategoryResponse getById(Integer id);

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Integer id, CategoryRequest request);

    CategoryResponse activate(Integer id);

    CategoryResponse deactivate(Integer id);
}
