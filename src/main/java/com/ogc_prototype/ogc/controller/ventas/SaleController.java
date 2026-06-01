package com.ogc_prototype.ogc.controller.ventas;

import com.ogc_prototype.ogc.config.RequiresRole;
import com.ogc_prototype.ogc.dto.request.SaleLineRequest;
import com.ogc_prototype.ogc.dto.request.SaleRequest;
import com.ogc_prototype.ogc.dto.response.SaleLineResponse;
import com.ogc_prototype.ogc.dto.response.SaleResponse;
import com.ogc_prototype.ogc.exception.AppException;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.service.ventas.SaleLineService;
import com.ogc_prototype.ogc.service.ventas.SaleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;
    private final SaleLineService saleLineService;

    @GetMapping
    @RequiresRole({Role.ADMIN, Role.VENDOR})
    public ResponseEntity<List<SaleResponse>> getAll() {
        return ResponseEntity.ok(saleService.getAll());
    }

    /**
     * ADMIN y VENDOR ven cualquier venta. CUSTOMER solo puede ver sus propias ventas.
     */
    @GetMapping("/{id}")
    @RequiresRole({Role.ADMIN, Role.VENDOR, Role.CUSTOMER})
    public ResponseEntity<SaleResponse> getById(@PathVariable Integer id,
            HttpServletRequest request) {
        SaleResponse sale = saleService.getById(id);
        verifyCustomerAccess(sale.getCustomerId(), request);
        return ResponseEntity.ok(sale);
    }

    @PostMapping
    @RequiresRole({Role.ADMIN, Role.VENDOR})
    public ResponseEntity<SaleResponse> create(@Valid @RequestBody SaleRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleService.create(body));
    }

    @PutMapping("/{id}")
    @RequiresRole({Role.ADMIN, Role.VENDOR})
    public ResponseEntity<SaleResponse> update(@PathVariable Integer id,
            @Valid @RequestBody SaleRequest body) {
        return ResponseEntity.ok(saleService.update(id, body));
    }

    // --- Líneas de venta ---

    @GetMapping("/{id}/lines")
    @RequiresRole({Role.ADMIN, Role.VENDOR, Role.CUSTOMER})
    public ResponseEntity<List<SaleLineResponse>> getLines(@PathVariable Integer id,
            HttpServletRequest request) {
        SaleResponse sale = saleService.getById(id);
        verifyCustomerAccess(sale.getCustomerId(), request);
        return ResponseEntity.ok(saleLineService.getBySaleId(id));
    }

    @GetMapping("/{id}/lines/{lineId}")
    @RequiresRole({Role.ADMIN, Role.VENDOR})
    public ResponseEntity<SaleLineResponse> getLine(@PathVariable Integer id,
            @PathVariable Integer lineId) {
        return ResponseEntity.ok(saleLineService.getById(id, lineId));
    }

    @PostMapping("/{id}/lines")
    @RequiresRole({Role.ADMIN, Role.VENDOR})
    public ResponseEntity<SaleLineResponse> addLine(@PathVariable Integer id,
            @Valid @RequestBody SaleLineRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleLineService.create(body));
    }

    @DeleteMapping("/{id}/lines/{lineId}")
    @RequiresRole({Role.ADMIN, Role.VENDOR})
    public ResponseEntity<Void> deleteLine(@PathVariable Integer id, @PathVariable Integer lineId) {
        saleLineService.delete(id, lineId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Verifica que un CUSTOMER solo pueda acceder a sus propias ventas. ADMIN y VENDOR no tienen
     * restricción.
     */
    private void verifyCustomerAccess(Integer saleCustomerId, HttpServletRequest request) {
        Role role = (Role) request.getAttribute("role");
        Integer requesterId = (Integer) request.getAttribute("userId");
        if (role == Role.CUSTOMER && !saleCustomerId.equals(requesterId)) {
            throw new AppException(HttpStatus.FORBIDDEN,
                    "No tienes permisos para acceder a esta venta");
        }
    }
}
