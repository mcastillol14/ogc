package com.ogc_prototype.ogc.service.clientes;

public interface EmailService {

    void sendVerificationCode(String to, String code);
}
