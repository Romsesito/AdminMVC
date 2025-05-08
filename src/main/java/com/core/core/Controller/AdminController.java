package com.core.core.Controller;

import com.core.core.Entity.ERole;
import com.core.core.Entity.User;
import com.core.core.Service.UserService;
import com.core.core.Payload.Request.UserCreationRequest;
import com.core.core.Payload.Request.RoleAssignmentRequest;
import com.core.core.exceptions.CannotRemoveLastRoleException;
import com.core.core.exceptions.RoleAlreadyAssignedException;
import com.core.core.exceptions.RoleNotAssignedException;
import com.core.core.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public ResponseEntity<?> createUserByAdmin(@Valid @RequestBody UserCreationRequest request) {
        try {


            String username = request.getUsername();
            String password = request.getPassword();
            String roleNameString = request.getRole();

            ERole roleToAssign;
            try {
                roleToAssign = ERole.valueOf(roleNameString.toUpperCase());
                if (roleToAssign == ERole.ROLE_ADMIN) {
                    return ResponseEntity.badRequest().body("No se puede crear un usuario ADMIN a través de este endpoint.");
                }
                if (roleToAssign != ERole.ROLE_CLIENT && roleToAssign != ERole.ROLE_CREATIVE) {
                    return ResponseEntity.badRequest().body("Rol inválido para creación: " + roleNameString +
                            ". Los roles válidos para creación son ROLE_CLIENT, ROLE_CREATIVE.");
                }
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Nombre de rol inválido: " + roleNameString +
                        ". Los roles válidos para creación son ROLE_CLIENT, ROLE_CREATIVE.");
            }

            User newUser = userService.createUser(username, password, roleToAssign);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().body("ID de usuario inválido.");
        }
        try {

            userService.deleteUser(userId);
            return ResponseEntity.ok("Usuario con ID " + userId + " eliminado exitosamente.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> assignRoleToUser(@PathVariable Long userId, @Valid @RequestBody RoleAssignmentRequest request) { // Usar DTO y @Valid

        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().body("ID de usuario inválido.");
        }
        try {
            String roleNameString = request.getRole();

            ERole roleToAssign;
            try {
                roleToAssign = ERole.valueOf(roleNameString.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Nombre de rol inválido: " + roleNameString);
            }


            if (roleToAssign != ERole.ROLE_CLIENT && roleToAssign != ERole.ROLE_CREATIVE) {
                return ResponseEntity.badRequest()
                        .body("El Admin solo puede asignar ROLE_CLIENT o ROLE_CREATIVE.");
            }

            User updatedUser = userService.assignRoleToUser(userId, roleToAssign);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RoleAlreadyAssignedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
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

        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().body("ID de usuario inválido.");
        }
        try {

            User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}/role")
    public ResponseEntity<?> removeRoleFromUser(@PathVariable Long userId, @Valid @RequestBody RoleAssignmentRequest request) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().body("ID de usuario inválido.");
        }
        try {
            String roleNameString = request.getRole();

            ERole roleToRemove;
            try {
                roleToRemove = ERole.valueOf(roleNameString.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Nombre de rol inválido: " + roleNameString);
            }

            User updatedUser = userService.removeRoleFromUser(userId, roleToRemove);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RoleNotAssignedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (CannotRemoveLastRoleException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}