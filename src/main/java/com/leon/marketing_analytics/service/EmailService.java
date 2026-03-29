package com.leon.marketing_analytics.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendHtmlEmail(String sentTo,String subject, String htmlEmail) {
        try {
            MimeMessage email = mailSender.createMimeMessage();
            MimeMessageHelper emailContent = new MimeMessageHelper(
                    email, true, "UTF-8");
            emailContent.setTo(sentTo);
            emailContent.setSubject(subject);
            emailContent.setText(htmlEmail, true);
            mailSender.send(email);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", sentTo, e.getMessage());
            throw new RuntimeException("Email delivery failed", e);
        }
    }
}
