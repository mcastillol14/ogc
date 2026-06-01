package com.ogc_prototype.ogc.controller.almacen;

import com.ogc_prototype.ogc.config.RequiresRole;
import com.ogc_prototype.ogc.dto.request.AdjustmentLineRequest;
import com.ogc_prototype.ogc.dto.request.InventoryAdjustmentRequest;
import com.ogc_prototype.ogc.dto.response.AdjustmentLineResponse;
import com.ogc_prototype.ogc.dto.response.InventoryAdjustmentResponse;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.service.almacen.InventoryAdjustmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-adjustments")
@RequiredArgsConstructor
@RequiresRole(Role.ADMIN)
public class InventoryAdjustmentController {

    private final InventoryAdjustmentService adjustmentService;

    @GetMapping
    public ResponseEntity<List<InventoryAdjustmentResponse>> getAll() {
        return ResponseEntity.ok(adjustmentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryAdjustmentResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(adjustmentService.getById(id));
    }

    @PostMapping
    public ResponseEntity<InventoryAdjustmentResponse> create(
            @Valid @RequestBody InventoryAdjustmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adjustmentService.create(request));
    }

    @GetMapping("/{id}/lines")
    public ResponseEntity<List<AdjustmentLineResponse>> getLines(@PathVariable Integer id) {
        return ResponseEntity.ok(adjustmentService.getLines(id));
    }

    @GetMapping("/{id}/lines/{lineId}")
    public ResponseEntity<AdjustmentLineResponse> getLine(@PathVariable Integer id,
            @PathVariable Integer lineId) {
        return ResponseEntity.ok(adjustmentService.getLine(id, lineId));
    }

    @PostMapping("/{id}/lines")
    public ResponseEntity<AdjustmentLineResponse> addLine(@PathVariable Integer id,
            @Valid @RequestBody AdjustmentLineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adjustmentService.addLine(request));
    }
}
