package com.ogc_prototype.ogc.exception;

import org.springframework.http.HttpStatus;

public class SaleException extends AppException {

    private SaleException(HttpStatus status, String message) {
        super(status, message);
    }

    public static SaleException notFound(Integer id) {
        return new SaleException(HttpStatus.NOT_FOUND, "Venta no encontrada con id: " + id);
    }

    public static SaleException lineNotFound(Integer id) {
        return new SaleException(HttpStatus.NOT_FOUND,
                "Línea de venta no encontrada con id: " + id);
    }
}

