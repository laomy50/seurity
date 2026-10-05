package com.example.seurity.service;
import com.example.seurity.dto.LoginUserDto;
import com.example.seurity.dto.RegisterUserDto;
import com.example.seurity.dto.RoleDTO;
import com.example.seurity.entity.Roles;
import com.example.seurity.entity.User;
import com.example.seurity.repo.RoleRepository;
import com.example.seurity.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Value("${app.frontend-url:https://zanzimart.com }")
    private String frontendUrl;

    @Value("${app.api-url:https://api_up_zanzimart.com}")
    private String apiUrl;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;


    @Autowired
    private RoleRepository roleRepository;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Roles saveRole(RoleDTO dto){
        Roles role = new Roles();
        role.setRoleName(dto.getRoleName());
        role.setRoleStatus(1);
        return roleRepository.save(role);
    }

    public List<RoleDTO> getAllRoles() {
        List<Roles> roles = roleRepository.findAll();

        return roles.stream().map(role -> {
            RoleDTO dto = new RoleDTO();
            dto.setRoleId(role.getRoleId());
            dto.setRoleName(role.getRoleName());
            dto.setRoleStatus(role.getRoleStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    public User signup(RegisterUserDto input) {
        Optional<Roles> roles = roleRepository.findById(input.getRoleId());
        User user = new User();

        // Always use email as username
        user.setUsername(input.getEmail());
        user.setEmail(input.getEmail());
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setSecondName(input.getSecondName());
        user.setPhoneNumber(input.getPhoneNumber());

        // Auto-generate password if not provided
        String plainPassword;
        if (input.getPassword() == null || input.getPassword().trim().isEmpty()) {
            plainPassword = generateRandomPassword();
        } else {
            plainPassword = input.getPassword();
        }

        // Encode and set password
        user.setPassword(passwordEncoder.encode(plainPassword));

        if (roles.isPresent()) {
            user.setRoles(roles.get());
        } else {
            user.setRoles(new Roles());
        }

        // Generate activation token
        String token = UUID.randomUUID().toString();
        user.setActivationToken(token);

        // Save the user
        User savedUser = userRepository.save(user);

        // Try to send welcome email (don't fail registration if email fails)
        try {
            String activationLink = frontendUrl + "/activate?token=" + token;
            logger.info("Attempting to send welcome email to: {}", user.getEmail());
            emailService.sendWelcomeEmailWithCredentials(
                    user.getEmail(),
                    user.getFirstName(),
                    plainPassword,
                    activationLink
            );
            logger.info("Welcome email sent successfully to: {}", user.getEmail());
        } catch (Exception e) {
            // Log the error but don't fail the registration
            logger.error("Failed to send welcome email to {}: {}", user.getEmail(), e.getMessage(), e);
        }

        return savedUser;
    }

    /**
     * Generate a secure random password
     * Format: 3 uppercase + 3 lowercase + 3 digits + 1 special = 10 characters
     */
    private String generateRandomPassword() {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "@#$%&*";

        StringBuilder password = new StringBuilder();

        // Add 3 uppercase letters
        for (int i = 0; i < 3; i++) {
            int index = (int) (Math.random() * uppercase.length());
            password.append(uppercase.charAt(index));
        }

        // Add 3 lowercase letters
        for (int i = 0; i < 3; i++) {
            int index = (int) (Math.random() * lowercase.length());
            password.append(lowercase.charAt(index));
        }

        // Add 3 digits
        for (int i = 0; i < 3; i++) {
            int index = (int) (Math.random() * digits.length());
            password.append(digits.charAt(index));
        }

        // Add 1 special character
        int index = (int) (Math.random() * special.length());
        password.append(special.charAt(index));

        // Shuffle the password for better randomness
        return shuffleString(password.toString());
    }

    /**
     * Shuffle string characters
     */
    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );

        return userRepository.findByUsername(input.getUsername())
                .orElseThrow();
    }

    public Roles updateRole(String roleId, RoleDTO dto) {
        Roles role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        role.setRoleName(dto.getRoleName());
        role.setRoleStatus(dto.getRoleStatus());
        return roleRepository.save(role);
    }

    public void deleteRole(String roleId) {
        roleRepository.deleteById(roleId);
    }

    public Roles hideRole(String roleId) {
        Roles role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        role.setRoleStatus(0); // Assuming 0 means hidden
        return roleRepository.save(role);
    }

//    public Optional<Roles> hideRole(String roleId) {
//        Optional<Roles> role = roleRepository.findById(roleId);
//        role.ifPresent(roleName -> {
//            roleName.setHidden(true);
//            roleRepository.save(roleName);
//        });
//        return role;
//    }

    public Roles getRoleById(String roleId) {
        return roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
    }

    /**
     * Resend activation email to user
     */
    public void resendActivationEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        if (user.isActive()) {
            throw new RuntimeException("Account is already activated");
        }

        // Generate new activation token if needed
        if (user.getActivationToken() == null) {
            String token = UUID.randomUUID().toString();
            user.setActivationToken(token);
            userRepository.save(user);
        }

        String activationLink = frontendUrl + "/activate?token=" + user.getActivationToken();

        try {
            logger.info("Resending activation email to: {}", email);
            emailService.resendActivationEmail(email, user.getFirstName(), activationLink);
            logger.info("Activation email resent successfully to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to resend activation email to {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to send activation email. Please try again later.");
        }
    }

    /**
     * Initiate forgot password process - send reset email
     */
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            // Don't reveal if email exists for security
            logger.warn("Password reset requested for non-existent email: {}", email);
            return;
        }

        // Generate password reset token
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);

        // Set expiry to 1 hour from now
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.HOUR, 1);
        user.setPasswordResetTokenExpiry(cal.getTime());

        userRepository.save(user);

        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        try {
            logger.info("Sending password reset email to: {}", email);
            emailService.sendPasswordResetEmail(email, user.getFirstName(), resetLink);
            logger.info("Password reset email sent successfully to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send password reset email to {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to send password reset email. Please try again later.");
        }
    }

    /**
     * Reset password using token - generates new password and sends it via email
     */
    public void resetPassword(String token) {
        java.util.Optional<User> optionalUser = userRepository.findByPasswordResetToken(token);

        if (!optionalUser.isPresent()) {
            throw new RuntimeException("Invalid password reset token");
        }

        User user = optionalUser.get();

        // Check if token is expired
        if (user.getPasswordResetTokenExpiry() == null ||
                user.getPasswordResetTokenExpiry().before(new java.util.Date())) {
            throw new RuntimeException("Password reset token has expired. Please request a new one.");
        }

        // Generate new password
        String newPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));

        // Clear reset token
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);

        userRepository.save(user);

        try {
            logger.info("Sending new password to: {}", user.getEmail());
            emailService.sendNewPasswordEmail(user.getEmail(), user.getFirstName(), newPassword);
            logger.info("New password sent successfully to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send new password email to {}: {}", user.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Password reset successful but failed to send email. Please contact support.");
        }
    }

    /**
     * Reset password with custom password provided by user
     */
    public void resetPasswordWithNewPassword(String token, String newPassword) {
        java.util.Optional<User> optionalUser = userRepository.findByPasswordResetToken(token);

        if (!optionalUser.isPresent()) {
            throw new RuntimeException("Invalid password reset token");
        }

        User user = optionalUser.get();

        // Check if token is expired
        if (user.getPasswordResetTokenExpiry() == null ||
                user.getPasswordResetTokenExpiry().before(new java.util.Date())) {
            throw new RuntimeException("Password reset token has expired. Please request a new one.");
        }

        // Set new password
        user.setPassword(passwordEncoder.encode(newPassword));

        // Clear reset token
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);

        userRepository.save(user);

        logger.info("Password reset successfully for user: {}", user.getEmail());
    }
}
