package com.example.seurity.controller;

import com.example.seurity.dto.LoginResponse;
import com.example.seurity.dto.LoginUserDto;
import com.example.seurity.dto.RoleDTO;
import com.example.seurity.entity.Roles;
import com.example.seurity.entity.User;
import com.example.seurity.repo.UserRepository;
import com.example.seurity.service.AuthenticationService;
import com.example.seurity.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestMapping("/baital/auth")
@RestController
@CrossOrigin
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

//    @PostMapping("/signup")
//    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
//        User registeredUser = authenticationService.signup(registerUserDto);
//
//        return ResponseEntity.ok(registeredUser);
//    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setUserId(authenticatedUser.getUserId());
        loginResponse.setActive(authenticatedUser.isActive());
        loginResponse.setUsername(authenticatedUser.getUsername());
        loginResponse.setEmail(authenticatedUser.getEmail());
        loginResponse.setFirstName(authenticatedUser.getFirstName());
        loginResponse.setLastName(authenticatedUser.getLastName());
        loginResponse.setPhoneNumber(authenticatedUser.getPhoneNumber());
        loginResponse.setRoleName(authenticatedUser.getRoles().getRoleName());
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateUser(@RequestParam String token) {
        Optional<User> optionalUser = userRepository.findByActivationToken(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setActive(true); // Activate the user
            user.setActivationToken(null); // Clear the activation token
            userRepository.save(user); // Save the changes

            return ResponseEntity.ok("User successfully activated.");
        } else {
            return ResponseEntity.badRequest().body("Invalid activation token.");
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Roles> saveRole(@RequestBody RoleDTO roleDTO) {
        Roles savedRole = authenticationService.saveRole(roleDTO);
        return ResponseEntity.ok(savedRole);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = authenticationService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/updaterole/{roleId}")
    public ResponseEntity<Roles> updateRole(@PathVariable String roleId, @RequestBody RoleDTO roleDTO) {
        Roles updatedRole = authenticationService.updateRole(roleId, roleDTO);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/deleterole/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable String roleId) {
        authenticationService.deleteRole(roleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{roleId}/hide")
    public ResponseEntity<Roles> hideRole(@PathVariable String roleId) {
        Roles hiddenRole = authenticationService.hideRole(roleId);
        return ResponseEntity.ok(hiddenRole);
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<Roles> getRoleById(@PathVariable String roleId) {
        Roles role = authenticationService.getRoleById(roleId);
        return ResponseEntity.ok(role);
    }

    /**
     * Resend activation email
     * POST /auth/resend-activation
     * Request body: { "email": "user@example.com" }
     */
    @PostMapping("/resend-activation")
    public ResponseEntity<?> resendActivationEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email is required"));
            }

            logger.info("Resend activation email request for: {}", email);
            authenticationService.resendActivationEmail(email);

            return ResponseEntity.ok(Map.of(
                    "message", "Activation email has been sent. Please check your inbox.",
                    "email", email
            ));
        } catch (RuntimeException ex) {
            logger.error("Failed to resend activation email: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            logger.error("Unexpected error resending activation email: ", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send activation email. Please try again later."));
        }
    }

    /**
     * Forgot password - send reset email
     * POST /forgot-password
     * Request body: { "email": "user@example.com" }
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email is required"));
            }

            logger.info("Forgot password request for: {}", email);
            authenticationService.forgotPassword(email);

            // Always return success message for security (don't reveal if email exists)
            return ResponseEntity.ok(Map.of(
                    "message", "If an account exists with this email, a password reset link has been sent.",
                    "email", email
            ));
        } catch (RuntimeException ex) {
            logger.error("Failed to process forgot password: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            logger.error("Unexpected error processing forgot password: ", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process request. Please try again later."));
        }
    }

    /**
     * Reset password using token (auto-generates new password)
     * GET /auth/reset-password?token=xxx
     */
    @GetMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token) {
        try {
            logger.info("Password reset request with token");
            authenticationService.resetPassword(token);

            return ResponseEntity.ok(Map.of(
                    "message", "Password has been reset. Your new password has been sent to your email."
            ));
        } catch (RuntimeException ex) {
            logger.error("Failed to reset password: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            logger.error("Unexpected error resetting password: ", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reset password. Please try again later."));
        }
    }

    /**
     * Reset password with user-provided new password
     * POST /auth/reset-password
     * Request body: { "token": "xxx", "newPassword": "xxx" }
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPasswordWithNewPassword(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            String newPassword = request.get("newPassword");

            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Reset token is required"));
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "New password is required"));
            }
            if (newPassword.length() < 8) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Password must be at least 8 characters long"));
            }

            logger.info("Password reset with new password request");
            authenticationService.resetPasswordWithNewPassword(token, newPassword);

            return ResponseEntity.ok(Map.of(
                    "message", "Password has been reset successfully. You can now log in with your new password."
            ));
        } catch (RuntimeException ex) {
            logger.error("Failed to reset password: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            logger.error("Unexpected error resetting password: ", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reset password. Please try again later."));
        }
    }
}



