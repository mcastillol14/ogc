package com.ogc_prototype.ogc.exception;

import org.springframework.http.HttpStatus;

public class ProductException extends AppException {

    private ProductException(HttpStatus status, String message) {
        super(status, message);
    }

    public static ProductException notFound(Integer id) {
        return new ProductException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + id);
    }

    public static ProductException notFound(String sku) {
        return new ProductException(HttpStatus.NOT_FOUND, "Producto no encontrado con SKU: " + sku);
    }

    public static ProductException duplicateSku(String sku) {
        return new ProductException(HttpStatus.CONFLICT,
                "Ya existe un producto con el SKU '" + sku + "'");
    }

    public static ProductException inactive(Integer id) {
        return new ProductException(HttpStatus.UNPROCESSABLE_CONTENT,
                "El producto con id " + id + " está inactivo");
    }
}

