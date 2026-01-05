package com.peters.cafecart.shared.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.backend.url}")
    private String backendUrl;

    public void sendVerificationEmail(String to, String token) {
        String subject = "Verify your Email - CafeCart";
        String confirmationUrl = backendUrl + "/verify-email?token=" + token;
        String message = "Please click the link below to verify your email address:\n" + confirmationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }

    public void sendResetPasswordEmail(String to, String token) {
        String subject = "Reset your Password - CafeCart";
        // Redirecting to frontend as requested
        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        String message = "Please click the link below to reset your password. This link will expire in 1 hour:\n" + resetUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }
}
