package com.ogc_prototype.ogc.exception;

import org.springframework.http.HttpStatus;

public class PackException extends AppException {

    private PackException(HttpStatus status, String message) {
        super(status, message);
    }

    public static PackException notFound(Integer id) {
        return new PackException(HttpStatus.NOT_FOUND, "Pack no encontrado con id: " + id);
    }

    public static PackException productNotFound(Integer productId) {
        return new PackException(HttpStatus.UNPROCESSABLE_CONTENT,
                "Producto no encontrado con id: " + productId);
    }
}
