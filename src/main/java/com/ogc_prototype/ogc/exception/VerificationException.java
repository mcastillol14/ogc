package com.ogc_prototype.ogc.exception;

import org.springframework.http.HttpStatus;

public class VerificationException extends AppException {

    private VerificationException(HttpStatus status, String message) {
        super(status, message);
    }

    public static VerificationException invalidOrExpired() {
        return new VerificationException(HttpStatus.BAD_REQUEST,
                "Código de verificación inválido o caducado");
    }

    public static VerificationException alreadyVerified() {
        return new VerificationException(HttpStatus.CONFLICT, "Esta cuenta ya está verificada");
    }

    public static VerificationException accountNotVerified() {
        return new VerificationException(HttpStatus.FORBIDDEN,
                "La cuenta no ha sido verificada. Revisa tu correo electrónico");
    }
}
