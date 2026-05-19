package com.ogc_prototype.ogc.exception;

import org.springframework.http.HttpStatus;

public class PurchaseException extends AppException {

    private PurchaseException(HttpStatus status, String message) {
        super(status, message);
    }

    public static PurchaseException notFound(Integer id) {
        return new PurchaseException(HttpStatus.NOT_FOUND, "Compra no encontrada con id: " + id);
    }

    public static PurchaseException lineNotFound(Integer id) {
        return new PurchaseException(HttpStatus.NOT_FOUND,
                "Línea de compra no encontrada con id: " + id);
    }
}

