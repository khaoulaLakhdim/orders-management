package dev.ivy.orders.controller;

import dev.ivy.orders.entity.User;
import dev.ivy.orders.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            if (username == null || password == null) {
                response.put("success", false);
                response.put("message", "Username and password are required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Get user details
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole(),
                    "createdAt", user.getCreatedAt()
                ));
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(401).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            SecurityContextHolder.clearContext();
            response.put("success", true);
            response.put("message", "Logout successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Logout failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getName().equals("anonymousUser")) {
                
                Optional<User> userOpt = userRepository.findByUsername(authentication.getName());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    
                    response.put("success", true);
                    response.put("user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole(),
                        "createdAt", user.getCreatedAt()
                    ));
                    
                    return ResponseEntity.ok(response);
                }
            }
            
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(401).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error getting user info: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> registerRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String username = registerRequest.get("username");
            String password = registerRequest.get("password");
            String role = registerRequest.getOrDefault("role", "USER");
            
            if (username == null || password == null) {
                response.put("success", false);
                response.put("message", "Username and password are required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check if user already exists
            if (userRepository.existsByUsername(username)) {
                response.put("success", false);
                response.put("message", "Username already exists");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create new user
            User newUser = new User(username, passwordEncoder.encode(password), role);
            User savedUser = userRepository.save(newUser);
            
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("user", Map.of(
                "id", savedUser.getId(),
                "username", savedUser.getUsername(),
                "role", savedUser.getRole(),
                "createdAt", savedUser.getCreatedAt()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            // Check if user is admin
            if (authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                
                var users = userRepository.findAll().stream()
                    .map(user -> Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole(),
                        "createdAt", user.getCreatedAt()
                    ))
                    .toList();
                
                response.put("success", true);
                response.put("users", users);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Access denied. Admin role required.");
                return ResponseEntity.status(403).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error getting users: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
} 