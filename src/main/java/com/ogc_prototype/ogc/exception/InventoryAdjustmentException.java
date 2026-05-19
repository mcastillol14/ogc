package com.ogc_prototype.ogc.exception;

import org.springframework.http.HttpStatus;

public class InventoryAdjustmentException extends AppException {

    private InventoryAdjustmentException(HttpStatus status, String message) {
        super(status, message);
    }

    public static InventoryAdjustmentException notFound(Integer id) {
        return new InventoryAdjustmentException(HttpStatus.NOT_FOUND,
                "Ajuste de inventario no encontrado con id: " + id);
    }

    public static InventoryAdjustmentException lineNotFound(Integer id) {
        return new InventoryAdjustmentException(HttpStatus.NOT_FOUND,
                "Línea de ajuste no encontrada con id: " + id);
    }
}

