package com.ogc_prototype.ogc.controller.clientes;

import com.ogc_prototype.ogc.config.RequiresRole;
import com.ogc_prototype.ogc.dto.request.ChangeRoleRequest;
import com.ogc_prototype.ogc.dto.request.CustomerRequest;
import com.ogc_prototype.ogc.dto.response.CustomerResponse;
import com.ogc_prototype.ogc.exception.AppException;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.service.clientes.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<List<CustomerResponse>> getAll() {
        return ResponseEntity.ok(customerService.getAll());
    }

    @GetMapping("/{id}")
    @RequiresRole({Role.ADMIN, Role.CUSTOMER})
    public ResponseEntity<CustomerResponse> getById(@PathVariable Integer id,
            HttpServletRequest request) {
        verifyAccess(id, request);
        return ResponseEntity.ok(customerService.getById(id));
    }

    @PutMapping("/{id}")
    @RequiresRole({Role.ADMIN, Role.CUSTOMER})
    public ResponseEntity<CustomerResponse> update(@PathVariable Integer id,
            @Valid @RequestBody CustomerRequest body, HttpServletRequest request) {
        verifyAccess(id, request);
        return ResponseEntity.ok(customerService.update(id, body));
    }

    @PatchMapping("/{id}/role")
    @RequiresRole(Role.ADMIN)
    public ResponseEntity<CustomerResponse> changeRole(@PathVariable Integer id,
            @Valid @RequestBody ChangeRoleRequest body) {
        return ResponseEntity.ok(customerService.changeRole(id, body.getRole()));
    }

    private void verifyAccess(Integer resourceId, HttpServletRequest request) {
        Role role = (Role) request.getAttribute("role");
        Integer requesterId = (Integer) request.getAttribute("userId");
        if (role == Role.CUSTOMER && !resourceId.equals(requesterId)) {
            throw new AppException(HttpStatus.FORBIDDEN,
                    "No tienes permisos para acceder a este recurso");
        }
    }
}
