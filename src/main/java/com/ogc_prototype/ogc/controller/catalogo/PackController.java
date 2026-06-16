package com.ogc_prototype.ogc.controller.catalogo;

import com.ogc_prototype.ogc.config.RequiresRole;
import com.ogc_prototype.ogc.dto.request.PackRequest;
import com.ogc_prototype.ogc.dto.response.PackResponse;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.service.catalogo.PackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packs")
@RequiredArgsConstructor
public class PackController {

    private final PackService packService;

    @GetMapping
    public ResponseEntity<List<PackResponse>> getAll() {
        return ResponseEntity.ok(packService.getAllActive());
    }

    @GetMapping("/all")
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<List<PackResponse>> getAllAdmin() {
        return ResponseEntity.ok(packService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(packService.getById(id));
    }

    @PostMapping
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<PackResponse> create(@Valid @RequestBody PackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(packService.create(request));
    }

    @PutMapping("/{id}")
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<PackResponse> update(@PathVariable Integer id,
            @Valid @RequestBody PackRequest request) {
        return ResponseEntity.ok(packService.update(id, request));
    }

    @PatchMapping("/{id}/activate")
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<PackResponse> activate(@PathVariable Integer id) {
        return ResponseEntity.ok(packService.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<PackResponse> deactivate(@PathVariable Integer id) {
        return ResponseEntity.ok(packService.deactivate(id));
    }
}
