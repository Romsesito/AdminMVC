package com.core.core.Config;

import com.core.core.Entity.ERole;
import com.core.core.Entity.Role;
import com.core.core.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        for (ERole role : ERole.values()) {
            Optional<Role> existingRole = roleRepository.findByName(role);
            if (existingRole.isEmpty()) {
                Role newRole = new Role();
                newRole.setName(role);
                roleRepository.save(newRole);
                System.out.println("Role " + role + " initialized.");
            }
        }
    }
}