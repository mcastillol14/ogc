package com.ogc_prototype.ogc.controller.almacen;

import com.ogc_prototype.ogc.config.RequiresRole;
import com.ogc_prototype.ogc.dto.response.StockMovementResponse;
import com.ogc_prototype.ogc.model.enums.MovementType;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.service.almacen.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
@RequiresRole({Role.ADMIN, Role.VENDOR})
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @GetMapping
    public ResponseEntity<List<StockMovementResponse>> getAll() {
        return ResponseEntity.ok(stockMovementService.getAll());
    }

    @GetMapping("/lot/{lotId}")
    public ResponseEntity<List<StockMovementResponse>> getByLot(@PathVariable Integer lotId) {
        return ResponseEntity.ok(stockMovementService.getByLotId(lotId));
    }

    @GetMapping("/purchase/{purchaseId}")
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<List<StockMovementResponse>> getByPurchase(
            @PathVariable Integer purchaseId) {
        return ResponseEntity.ok(stockMovementService.getByPurchaseId(purchaseId));
    }

    @GetMapping("/sale/{saleId}")
    public ResponseEntity<List<StockMovementResponse>> getBySale(@PathVariable Integer saleId) {
        return ResponseEntity.ok(stockMovementService.getBySaleId(saleId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<StockMovementResponse>> getByType(@PathVariable MovementType type) {
        return ResponseEntity.ok(stockMovementService.getByType(type));
    }
}
