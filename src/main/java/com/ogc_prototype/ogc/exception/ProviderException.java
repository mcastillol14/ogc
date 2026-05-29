package com.ogc_prototype.ogc.exception;

import org.springframework.http.HttpStatus;

public class ProviderException extends AppException {

    private ProviderException(HttpStatus status, String message) {
        super(status, message);
    }

    public static ProviderException notFound(Integer id) {
        return new ProviderException(HttpStatus.NOT_FOUND, "Proveedor no encontrado con id: " + id);
    }

    public static ProviderException inactive(Integer id) {
        return new ProviderException(HttpStatus.UNPROCESSABLE_CONTENT,
                "El proveedor con id " + id + " está inactivo");
    }

    public static ProviderException duplicateName(String name) {
        return new ProviderException(HttpStatus.CONFLICT,
                "Ya existe un proveedor con el nombre '" + name + "'");
    }

    public static ProviderException duplicateEmail(String email) {
        return new ProviderException(HttpStatus.CONFLICT,
                "Ya existe un proveedor con el email '" + email + "'");
    }

    public static ProviderException duplicatePhone(String phone) {
        return new ProviderException(HttpStatus.CONFLICT,
                "Ya existe un proveedor con el teléfono '" + phone + "'");
    }
}

