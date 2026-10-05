package com.example.seurity.controller;
import com.example.seurity.dto.PasswordChangeRequest;
import com.example.seurity.entity.User;
import com.example.seurity.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/baital/users")
@RestController
@CrossOrigin
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/user/{userId}") // Endpoint for updating user
    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody User updatedUser) {
        try {
            Optional<User> existingUser = userService.getUserById(userId);
            if (existingUser.isPresent()) {
                User userToUpdate = existingUser.get();

                // Update user properties
                userToUpdate.setEmail(updatedUser.getEmail());
                userToUpdate.setFirstName(updatedUser.getFirstName());
                userToUpdate.setLastName(updatedUser.getLastName());


                // Save updated user
                userService.save(userToUpdate);
                return ResponseEntity.ok(userToUpdate);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/changepassword/{userId}")
    public ResponseEntity<String> changePassword(@PathVariable String userId,
                                                 @RequestBody PasswordChangeRequest passwordChangeRequest) {
        try {
            if (userService.changePassword(userId, passwordChangeRequest.getCurrentPassword(), passwordChangeRequest.getNewPassword())) {
                return ResponseEntity.ok("Password changed successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is incorrect.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error changing password.");
        }
    }
}


