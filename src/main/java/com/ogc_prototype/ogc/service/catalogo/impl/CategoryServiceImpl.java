package com.ogc_prototype.ogc.service.catalogo.impl;

import com.ogc_prototype.ogc.dto.request.CategoryRequest;
import com.ogc_prototype.ogc.dto.response.CategoryResponse;
import com.ogc_prototype.ogc.exception.CategoryException;
import com.ogc_prototype.ogc.mapper.CategoryMapper;
import com.ogc_prototype.ogc.model.Category;
import com.ogc_prototype.ogc.repository.CategoryRepository;
import com.ogc_prototype.ogc.service.catalogo.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(CategoryMapper::toResponse).toList();
    }

    @Override
    public List<CategoryResponse> getAllActive() {
        return categoryRepository.findAllByActiveTrue().stream().map(CategoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse getById(Integer id) {
        Category category =
                categoryRepository.findById(id).orElseThrow(() -> CategoryException.notFound(id));
        return CategoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw CategoryException.duplicateSlug(request.getSlug());
        }
        return CategoryMapper.toResponse(categoryRepository.save(CategoryMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public CategoryResponse update(Integer id, CategoryRequest request) {
        Category category =
                categoryRepository.findById(id).orElseThrow(() -> CategoryException.notFound(id));
        if (!category.getSlug().equals(request.getSlug())
                && categoryRepository.existsBySlug(request.getSlug())) {
            throw CategoryException.duplicateSlug(request.getSlug());
        }
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        category.setActive(request.isActive());
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse activate(Integer id) {
        Category category =
                categoryRepository.findById(id).orElseThrow(() -> CategoryException.notFound(id));
        category.setActive(true);
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse deactivate(Integer id) {
        Category category =
                categoryRepository.findById(id).orElseThrow(() -> CategoryException.notFound(id));
        category.setActive(false);
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }
}
