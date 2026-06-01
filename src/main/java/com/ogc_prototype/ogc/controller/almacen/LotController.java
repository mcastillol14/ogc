package com.ogc_prototype.ogc.controller.almacen;

import com.ogc_prototype.ogc.config.RequiresRole;
import com.ogc_prototype.ogc.dto.request.LotRequest;
import com.ogc_prototype.ogc.dto.response.LotResponse;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.service.almacen.LotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lots")
@RequiredArgsConstructor
@RequiresRole({Role.ADMIN, Role.VENDOR})
public class LotController {

    private final LotService lotService;

    @GetMapping
    public ResponseEntity<List<LotResponse>> getAll() {
        return ResponseEntity.ok(lotService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LotResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(lotService.getById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<LotResponse>> getByProduct(@PathVariable Integer productId) {
        return ResponseEntity.ok(lotService.getByProductId(productId));
    }

    @PostMapping
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<LotResponse> create(@Valid @RequestBody LotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lotService.create(request));
    }
}
