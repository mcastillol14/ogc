package com.ogc_prototype.ogc.controller.clientes;

import com.ogc_prototype.ogc.config.RequiresRole;
import com.ogc_prototype.ogc.dto.request.ChangePasswordRequest;
import com.ogc_prototype.ogc.dto.request.CustomerRequest;
import com.ogc_prototype.ogc.dto.request.ForgotPasswordRequest;
import com.ogc_prototype.ogc.dto.request.LoginRequest;
import com.ogc_prototype.ogc.dto.request.ResetPasswordRequest;
import com.ogc_prototype.ogc.dto.request.VerifyCodeRequest;
import com.ogc_prototype.ogc.dto.response.CustomerResponse;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.service.clientes.AuthService;
import com.ogc_prototype.ogc.service.clientes.CustomerService;
import com.ogc_prototype.ogc.service.clientes.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CustomerService customerService;
    private final VerificationService verificationService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> register(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@Valid @RequestBody VerifyCodeRequest request) {
        verificationService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/change/request")
    @RequiresRole({Role.CUSTOMER, Role.ADMIN})
    public ResponseEntity<Map<String, String>> requestPasswordChange(HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        authService.requestPasswordChange(userId);
        return ResponseEntity.ok(Map.of("message", "Se ha enviado un código de verificación a tu correo"));
    }

    @PostMapping("/password/change/confirm")
    @RequiresRole({Role.CUSTOMER, Role.ADMIN})
    public ResponseEntity<Void> confirmPasswordChange(@Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        authService.confirmPasswordChange(userId, request.getCurrentPassword(),
                request.getCode(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset/request")
    public ResponseEntity<Map<String, String>> requestPasswordReset(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(Map.of("message",
                "Si el correo existe en el sistema recibirás un código de recuperación"));
    }

    @PostMapping("/password/reset/confirm")
    public ResponseEntity<Void> confirmPasswordReset(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.confirmPasswordReset(request.getEmail(), request.getCode(),
                request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}

