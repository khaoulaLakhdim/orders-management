package dev.ivy.orders.controller;

import dev.ivy.orders.entity.User;
import dev.ivy.orders.entity.Client;
import dev.ivy.orders.entity.Order;
import dev.ivy.orders.repository.UserRepository;
import dev.ivy.orders.repository.ClientRepository;
import dev.ivy.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/db-test")
public class DatabaseTestController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkDatabaseHealth() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test database connection by counting records
            long userCount = userRepository.count();
            long clientCount = clientRepository.count();
            long orderCount = orderRepository.count();
            
            response.put("status", "SUCCESS");
            response.put("message", "Database connection is working");
            response.put("totalUsers", userCount);
            response.put("totalClients", clientCount);
            response.put("totalOrders", orderCount);
            response.put("timestamp", java.time.LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Database connection failed: " + e.getMessage());
            response.put("timestamp", java.time.LocalDateTime.now());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/create-test-data")
    public ResponseEntity<Map<String, Object>> createTestData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Create a test user
            User testUser = new User("testuser", "hashedpassword123", "USER");
            User savedUser = userRepository.save(testUser);
            
            // Create a test client
            Client testClient = new Client("Test Company", "CLI001", "Test City");
            Client savedClient = clientRepository.save(testClient);
            
            // Create a test order
            Order testOrder = new Order("Test Product", savedClient, 1, new BigDecimal("999.99"), LocalDate.now());
            testOrder.setStatus("PENDING");
            testOrder.setPaymentMethod("CARD");
            Order savedOrder = orderRepository.save(testOrder);
            
            response.put("status", "SUCCESS");
            response.put("message", "Test data created successfully");
            response.put("user", savedUser);
            response.put("client", savedClient);
            response.put("order", savedOrder);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Failed to create test data: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<User> users = userRepository.findAll();
            response.put("status", "SUCCESS");
            response.put("message", "Retrieved all users");
            response.put("users", users);
            response.put("count", users.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Failed to retrieve users: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/clients")
    public ResponseEntity<Map<String, Object>> getAllClients() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Client> clients = clientRepository.findAll();
            response.put("status", "SUCCESS");
            response.put("message", "Retrieved all clients");
            response.put("clients", clients);
            response.put("count", clients.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Failed to retrieve clients: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Order> orders = orderRepository.findAll();
            response.put("status", "SUCCESS");
            response.put("message", "Retrieved all orders");
            response.put("orders", orders);
            response.put("count", orders.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Failed to retrieve orders: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/clear-all")
    public ResponseEntity<Map<String, Object>> clearAllData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            orderRepository.deleteAll();
            clientRepository.deleteAll();
            userRepository.deleteAll();
            response.put("status", "SUCCESS");
            response.put("message", "All data cleared successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Failed to clear data: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
} 