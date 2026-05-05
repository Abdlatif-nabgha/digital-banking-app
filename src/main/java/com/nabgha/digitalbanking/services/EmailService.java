package com.nabgha.digitalbanking.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Async
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, String token){
        String verificationLink = baseUrl + "/auth/verify?token=" + token;

        SimpleMailMessage message = new  SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Verify your Digital Banking account");
        message.setText(
                "Hello,\n\n" +
                        "Please verify your email by clicking the link below:\n\n" +
                        verificationLink + "\n\n" +
                        "This link expires in 15 minutes.\n\n" +
                        "Digital Banking Team"
        );
        mailSender.send(message);
    }
}
