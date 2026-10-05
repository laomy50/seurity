package com.example.seurity.controller;
import com.example.seurity.dto.SimpleRegistrationRequest;
import com.example.seurity.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/registration")
@CrossOrigin
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private RegistrationService registrationService;



    @PostMapping("/register")
    public ResponseEntity<?> registerSimple(@RequestBody SimpleRegistrationRequest request) {
        try {
            logger.info("Simple registration request received for: {}", request.getEmail());

            // Validate required fields
            if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "First name is required"));
            }
            if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Last name is required"));
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email is required"));
            }
            if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Phone number is required"));
            }
            if (request.getRegistrationType() == null || request.getRegistrationType().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Registration type is required"));
            }

            // Validate registration type
            String regType = request.getRegistrationType().toUpperCase();
            if (!regType.equals("WORKFORCE") && !regType.equals("ORGANIZATION") && !regType.equals("ORDER") && !regType.equals("STORE")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid registration type. Must be WORKFORCE, ORDER, STORE or ORGANIZATION"));
            }

            Map<String, Object> result = registrationService.registerSimple(request);
            return ResponseEntity.ok(result);

        } catch (RuntimeException ex) {
            logger.error("Error during simple registration: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            logger.error("Unexpected error during simple registration: ", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registration failed. Please try again.", "details", ex.getMessage()));
        }
    }
}

