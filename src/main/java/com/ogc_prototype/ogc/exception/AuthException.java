package com.ogc_prototype.ogc.exception;

import org.springframework.http.HttpStatus;

public class AuthException extends AppException {

    private AuthException(HttpStatus status, String message) {
        super(status, message);
    }

    public static AuthException invalidCredentials() {
        return new AuthException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
    }

    public static AuthException invalidToken() {
        return new AuthException(HttpStatus.UNAUTHORIZED, "El token es inválido o ha expirado");
    }

    public static AuthException wrongCurrentPassword() {
        return new AuthException(HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta");
    }

    public static AuthException passwordRecentlyUsed() {
        return new AuthException(HttpStatus.BAD_REQUEST,
                "La nueva contraseña no puede coincidir con ninguna de las 5 últimas contraseñas usadas");
    }
}

