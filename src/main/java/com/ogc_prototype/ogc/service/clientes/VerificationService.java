package com.ogc_prototype.ogc.service.clientes;

import com.ogc_prototype.ogc.model.enums.VerificationCodePurpose;

public interface VerificationService {

    void sendCode(String email);

    void sendCode(String email, VerificationCodePurpose purpose);

    /** Valida el código de verificación de cuenta y marca la cuenta como verificada. */
    void verifyCode(String email, String code);

    void validateCode(String email, String code, VerificationCodePurpose purpose);
}
