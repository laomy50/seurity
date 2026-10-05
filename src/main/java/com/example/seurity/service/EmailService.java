package com.example.seurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender; // Spring Boot's JavaMailSender


    @Value("${ADMIN.email}")
    private String adminEmail;

    public void sendActivationEmail(String to, String activationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Account Activation");
        message.setText("Please click the link to activate your account: " + activationLink);
        mailSender.send(message);
    }

    public void sendResetLink(String toEmail, String token) {
        String resetUrl = "https://apizanzimart.com/api/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset Request");
        message.setText("Click the link to reset your password: " + resetUrl);

        mailSender.send(message);
    }

    /**
     * Send welcome email with credentials and activation link
     */
    public void sendWelcomeEmailWithCredentials(String to, String firstName, String password, String activationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to Zanzimart - Account Credentials");

        String emailBody = String.format(
                "Dear %s,\n\n" +
                        "Welcome to Zanzimart !\n\n" +
                        "Your account has been created successfully. Here are your login credentials:\n\n" +
                        "Username: %s\n" +
                        "Password: %s\n\n" +
                        "IMPORTANT: Please keep this password secure and change it after your first login.\n\n" +
                        "To activate your account, please click the link below:\n" +
                        "%s\n\n" +
                        "If you did not request this account, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "Zanzimart  Team",
                firstName, to, password, activationLink
        );

        message.setText(emailBody);
        mailSender.send(message);
    }

    /**
     * Resend activation email
     */
    public void resendActivationEmail(String to, String firstName, String activationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Zanzimart  - Resend Activation Link");

        String emailBody = String.format(
                "Dear %s,\n\n" +
                        "You requested a new activation link for your Zanzimart  account.\n\n" +
                        "Please click the link below to activate your account:\n" +
                        "%s\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "Zanzimart  Team",
                firstName, activationLink
        );

        message.setText(emailBody);
        mailSender.send(message);
    }

    /**
     * Send password reset email
     */
    public void sendPasswordResetEmail(String to, String firstName, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Zanzimart  - Password Reset Request");

        String emailBody = String.format(
                "Dear %s,\n\n" +
                        "We received a request to reset your password for your Zanzimart  account.\n\n" +
                        "Please click the link below to reset your password:\n" +
                        "%s\n\n" +
                        "This link will expire in 1 hour.\n\n" +
                        "If you did not request a password reset, please ignore this email. Your password will remain unchanged.\n\n" +
                        "Best regards,\n" +
                        "Zanzimart  Team",
                firstName, resetLink
        );

        message.setText(emailBody);
        mailSender.send(message);
    }

    /**
     * Send new password email after reset
     */
    public void sendNewPasswordEmail(String to, String firstName, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Zanzimart  - Your New Password");

        String emailBody = String.format(
                "Dear %s,\n\n" +
                        "Your password has been successfully reset.\n\n" +
                        "Your new login credentials:\n" +
                        "Username: %s\n" +
                        "Password: %s\n\n" +
                        "IMPORTANT: Please change your password immediately after logging in.\n\n" +
                        "Best regards,\n" +
                        "Zanzimart  Team",
                firstName, to, newPassword
        );

        message.setText(emailBody);
        mailSender.send(message);
    }


}


