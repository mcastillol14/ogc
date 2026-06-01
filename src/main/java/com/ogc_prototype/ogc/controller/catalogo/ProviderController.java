package com.ogc_prototype.ogc.controller.catalogo;

import com.ogc_prototype.ogc.config.RequiresRole;
import com.ogc_prototype.ogc.dto.request.ProviderRequest;
import com.ogc_prototype.ogc.dto.response.ProviderResponse;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.service.catalogo.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
@RequiresRole(Role.ADMIN)
public class ProviderController {

    private final ProviderService providerService;

    @GetMapping
    public ResponseEntity<List<ProviderResponse>> getAll() {
        return ResponseEntity.ok(providerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(providerService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProviderResponse> create(@Valid @RequestBody ProviderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(providerService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProviderResponse> update(@PathVariable Integer id,
            @Valid @RequestBody ProviderRequest request) {
        return ResponseEntity.ok(providerService.update(id, request));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ProviderResponse> activate(@PathVariable Integer id) {
        return ResponseEntity.ok(providerService.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ProviderResponse> deactivate(@PathVariable Integer id) {
        return ResponseEntity.ok(providerService.deactivate(id));
    }
}
