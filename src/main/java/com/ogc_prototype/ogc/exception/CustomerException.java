package com.ogc_prototype.ogc.exception;

import com.ogc_prototype.ogc.model.enums.Role;
import org.springframework.http.HttpStatus;

public class CustomerException extends AppException {

    private CustomerException(HttpStatus status, String message) {
        super(status, message);
    }

    public static CustomerException notFound(Integer id) {
        return new CustomerException(HttpStatus.NOT_FOUND, "Cliente no encontrado con id: " + id);
    }

    public static CustomerException notFound(String email) {
        return new CustomerException(HttpStatus.NOT_FOUND,
                "Cliente no encontrado con email: " + email);
    }

    public static CustomerException duplicateEmail(String email) {
        return new CustomerException(HttpStatus.CONFLICT,
                "El correo electrónico '" + email + "' ya está registrado");
    }

    public static CustomerException duplicateUsername(String userName) {
        return new CustomerException(HttpStatus.CONFLICT,
                "El nombre de usuario '" + userName + "' ya está en uso");
    }

    public static CustomerException sameRole(Role role) {
        return new CustomerException(HttpStatus.CONFLICT,
                "El cliente ya tiene el rol '" + role.name() + "'");
    }

    public static CustomerException duplicatePhone(String phone) {
        return new CustomerException(HttpStatus.CONFLICT,
                "El teléfono '" + phone + "' ya está registrado");
    }
}

