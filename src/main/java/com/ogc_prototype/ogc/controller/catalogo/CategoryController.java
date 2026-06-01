package com.ogc_prototype.ogc.controller.catalogo;

import com.ogc_prototype.ogc.config.RequiresRole;
import com.ogc_prototype.ogc.dto.request.CategoryRequest;
import com.ogc_prototype.ogc.dto.response.CategoryResponse;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.service.catalogo.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /** Público: devuelve solo categorías activas */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PostMapping
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @PutMapping("/{id}")
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<CategoryResponse> update(@PathVariable Integer id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @PatchMapping("/{id}/activate")
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<CategoryResponse> activate(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<CategoryResponse> deactivate(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.deactivate(id));
    }
}
