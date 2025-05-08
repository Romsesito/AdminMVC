package com.core.core.Controller;

import com.core.core.Entity.ERole;
import com.core.core.Entity.User;
import com.core.core.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public ResponseEntity<?> createUserByAdmin(@RequestBody Map<String, String> payload) {
        try {
            String username = payload.get("username");
            String password = payload.get("password");
            String roleNameString = payload.get("role");

            if (username == null || password == null || roleNameString == null) {
                return ResponseEntity.badRequest().body("Username, password, and role are required.");
            }

            ERole roleToAssign;
            try {
                roleToAssign = ERole.valueOf(roleNameString.toUpperCase());
                if (roleToAssign == ERole.ROLE_ADMIN) {
                    return ResponseEntity.badRequest().body("Cannot create ADMIN user via this endpoint.");
                }
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid role name: " + roleNameString +
                        ". Valid roles for creation are ROLE_CLIENT, ROLE_CREATIVE.");
            }

            User newUser = userService.createUser(username, password, roleToAssign);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User with ID " + userId + " deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> assignRoleToUser(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        try {
            String roleNameString = payload.get("role");
            if (roleNameString == null) {
                return ResponseEntity.badRequest().body("Role name is required in the payload.");
            }

            ERole roleToAssign;
            try {
                roleToAssign = ERole.valueOf(roleNameString.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid role name: " + roleNameString);
            }

            if (roleToAssign != ERole.ROLE_CLIENT && roleToAssign != ERole.ROLE_CREATIVE) {
                return ResponseEntity.badRequest()
                        .body("Admin can only assign ROLE_CLIENT or ROLE_CREATIVE to normal users.");
            }

            User updatedUser = userService.assignRoleToUser(userId, roleToAssign);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}