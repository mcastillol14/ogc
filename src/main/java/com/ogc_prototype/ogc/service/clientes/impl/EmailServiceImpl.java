package com.ogc_prototype.ogc.service.clientes.impl;

import com.ogc_prototype.ogc.service.clientes.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Código de verificación OGC");
        message.setText("""
                Bienvenido a OGC.

                Tu código de verificación es: %s

                Este código caduca en 15 minutos.

                Si no has creado una cuenta, ignora este mensaje.
                """.formatted(code));
        mailSender.send(message);
    }
}
