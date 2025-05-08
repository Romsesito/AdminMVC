package com.core.core.Service;

import com.core.core.Entity.ERole;
import com.core.core.Entity.Role;
import com.core.core.Entity.User;
import com.core.core.Repository.RoleRepository;
import com.core.core.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public User createUser(String username, String password, ERole role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists.");
        }

        Role userRole = roleRepository.findByName(role)
                .orElseThrow(() -> new RuntimeException("Role not found: " + role));

        User user = new User(username, password);
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    public User assignRoleToUser(Long userId, ERole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Role userRole = roleRepository.findByName(role)
                .orElseThrow(() -> new RuntimeException("Role not found: " + role));

        user.getRoles().add(userRole);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }
}