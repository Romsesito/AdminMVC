package com.core.core.Payload.Request; // Ejemplo de paquete

import jakarta.validation.constraints.NotBlank;

public class RoleAssignmentRequest {

    @NotBlank(message = "El nombre del rol es obligatorio.")
    private String role;

    // Getters y Setters
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}