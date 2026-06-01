package com.ogc_prototype.ogc.controller.catalogo;

import com.ogc_prototype.ogc.config.RequiresRole;
import com.ogc_prototype.ogc.dto.request.ProductRequest;
import com.ogc_prototype.ogc.dto.response.ProductResponse;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.service.catalogo.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /** Público: devuelve solo productos activos */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @PutMapping("/{id}")
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<ProductResponse> update(@PathVariable Integer id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @PatchMapping("/{id}/activate")
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<ProductResponse> activate(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<ProductResponse> deactivate(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.deactivate(id));
    }
}
