package com.ogc_prototype.ogc.service.clientes;

public interface VerificationService {

    /** Genera y envía un código de verificación al email indicado. */
    void sendCode(String email);

    /** Valida el código y marca la cuenta como verificada. */
    void verifyCode(String email, String code);
}
