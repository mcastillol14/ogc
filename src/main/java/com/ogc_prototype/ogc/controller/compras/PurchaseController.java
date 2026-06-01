package com.ogc_prototype.ogc.controller.compras;

import com.ogc_prototype.ogc.config.RequiresRole;
import com.ogc_prototype.ogc.dto.request.PurchaseLineRequest;
import com.ogc_prototype.ogc.dto.request.PurchaseRequest;
import com.ogc_prototype.ogc.dto.response.PurchaseLineResponse;
import com.ogc_prototype.ogc.dto.response.PurchaseResponse;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.service.compras.PurchaseLineService;
import com.ogc_prototype.ogc.service.compras.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@RequiresRole(Role.ADMIN)
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final PurchaseLineService purchaseLineService;

    @GetMapping
    public ResponseEntity<List<PurchaseResponse>> getAll() {
        return ResponseEntity.ok(purchaseService.getAll());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<PurchaseResponse>> getAllOrderedByDate() {
        return ResponseEntity.ok(purchaseService.getAllOrderedByDate());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(purchaseService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PurchaseResponse> create(@Valid @RequestBody PurchaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseResponse> update(@PathVariable Integer id,
            @Valid @RequestBody PurchaseRequest request) {
        return ResponseEntity.ok(purchaseService.update(id, request));
    }

    // --- Líneas de compra ---

    @GetMapping("/{id}/lines")
    public ResponseEntity<List<PurchaseLineResponse>> getLines(@PathVariable Integer id) {
        return ResponseEntity.ok(purchaseLineService.getByPurchaseId(id));
    }

    @GetMapping("/{id}/lines/{lineId}")
    public ResponseEntity<PurchaseLineResponse> getLine(@PathVariable Integer id,
            @PathVariable Integer lineId) {
        return ResponseEntity.ok(purchaseLineService.getById(id, lineId));
    }

    @PostMapping("/{id}/lines")
    public ResponseEntity<PurchaseLineResponse> addLine(@PathVariable Integer id,
            @Valid @RequestBody PurchaseLineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseLineService.create(request));
    }

    @PatchMapping("/{id}/lines/{lineId}/assign-lot/{lotId}")
    public ResponseEntity<PurchaseLineResponse> assignLot(@PathVariable Integer id,
            @PathVariable Integer lineId, @PathVariable Integer lotId) {
        return ResponseEntity.ok(purchaseLineService.assignLot(id, lineId, lotId));
    }

    @DeleteMapping("/{id}/lines/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable Integer id, @PathVariable Integer lineId) {
        purchaseLineService.delete(id, lineId);
        return ResponseEntity.noContent().build();
    }
}
