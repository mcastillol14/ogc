package com.ogc_prototype.ogc.service.clientes;

public interface AuthService {

    String login(String email, String password);

    void requestPasswordChange(Integer userId);

    void confirmPasswordChange(Integer userId, String currentPassword, String code,
            String newPassword);

    void requestPasswordReset(String email);

    void confirmPasswordReset(String email, String code, String newPassword);
}
