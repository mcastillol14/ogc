package com.ogc_prototype.ogc.service.catalogo;

import com.ogc_prototype.ogc.dto.request.CategoryRequest;
import com.ogc_prototype.ogc.dto.response.CategoryResponse;
import com.ogc_prototype.ogc.exception.CategoryException;
import com.ogc_prototype.ogc.model.Category;
import com.ogc_prototype.ogc.repository.CategoryRepository;
import com.ogc_prototype.ogc.service.catalogo.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl service;

    private Category buildCategory(int id, String name, String slug, boolean active) {
        return Category.builder().id(id).name(name).slug(slug).description("desc").active(active)
                .build();
    }

    // ─────────── getAll ───────────

    @Test
    void getAll_returnsAllCategories() {
        when(categoryRepository.findAll())
                .thenReturn(List.of(buildCategory(1, "Indica", "indica", true)));

        List<CategoryResponse> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Indica");
    }

    @Test
    void getAll_emptyRepo_returnsEmptyList() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        assertThat(service.getAll()).isEmpty();
    }

    // ─────────── getAllActive ───────────

    @Test
    void getAllActive_returnsOnlyActiveCategories() {
        when(categoryRepository.findAllByActiveTrue())
                .thenReturn(List.of(buildCategory(2, "Sativa", "sativa", true)));

        List<CategoryResponse> result = service.getAllActive();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
    }

    // ─────────── getById ───────────

    @Test
    void getById_existingId_returnsCategoryResponse() {
        when(categoryRepository.findById(1))
                .thenReturn(Optional.of(buildCategory(1, "Indica", "indica", true)));

        CategoryResponse response = service.getById(1);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getName()).isEqualTo("Indica");
    }

    @Test
    void getById_nonExistingId_throwsCategoryException() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99)).isInstanceOf(CategoryException.class);
    }

    // ─────────── create ───────────

    @Test
    void create_newSlug_savesCategoryAndReturnsResponse() {
        CategoryRequest request = CategoryRequest.builder().name("Hybrid").slug("hybrid")
                .description("desc").active(true).build();
        when(categoryRepository.existsBySlug("hybrid")).thenReturn(false);
        Category saved = buildCategory(3, "Hybrid", "hybrid", true);
        when(categoryRepository.save(any())).thenReturn(saved);

        CategoryResponse response = service.create(request);

        assertThat(response.getName()).isEqualTo("Hybrid");
        assertThat(response.getSlug()).isEqualTo("hybrid");
        verify(categoryRepository).save(any());
    }

    @Test
    void create_duplicateSlug_throwsCategoryException() {
        CategoryRequest request = CategoryRequest.builder().name("Indica").slug("indica").build();
        when(categoryRepository.existsBySlug("indica")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(CategoryException.class);
    }

    // ─────────── update ───────────

    @Test
    void update_existingId_updatesAndReturns() {
        Category existing = buildCategory(1, "Indica", "indica", true);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsBySlug("indica-v2")).thenReturn(false);
        when(categoryRepository.save(any())).thenReturn(existing);

        CategoryRequest request =
                CategoryRequest.builder().name("Indica V2").slug("indica-v2").active(true).build();
        CategoryResponse response = service.update(1, request);

        assertThat(response).isNotNull();
        verify(categoryRepository).save(any());
    }

    @Test
    void update_nonExistingId_throwsCategoryException() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.update(99, CategoryRequest.builder().name("X").slug("x").build()))
                        .isInstanceOf(CategoryException.class);
    }

    @Test
    void update_changeToExistingSlug_throwsConflict() {
        Category existing = buildCategory(1, "Indica", "indica", true);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsBySlug("sativa")).thenReturn(true);

        assertThatThrownBy(() -> service.update(1,
                CategoryRequest.builder().name("Indica").slug("sativa").build()))
                        .isInstanceOf(CategoryException.class);
    }

    // ─────────── activate / deactivate ───────────

    @Test
    void activate_setsActiveTrueAndSaves() {
        Category cat = buildCategory(1, "Indica", "indica", false);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(cat));
        when(categoryRepository.save(cat)).thenReturn(cat);

        CategoryResponse response = service.activate(1);

        assertThat(response.isActive()).isTrue();
        verify(categoryRepository).save(cat);
    }

    @Test
    void deactivate_setsActiveFalseAndSaves() {
        Category cat = buildCategory(1, "Indica", "indica", true);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(cat));
        when(categoryRepository.save(cat)).thenReturn(cat);

        CategoryResponse response = service.deactivate(1);

        assertThat(response.isActive()).isFalse();
        verify(categoryRepository).save(cat);
    }
}
