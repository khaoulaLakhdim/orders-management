package dev.ivy.orders.controller;

import dev.ivy.orders.config.DataSeeder;
import dev.ivy.orders.repository.UserRepository;
import dev.ivy.orders.repository.ClientRepository;
import dev.ivy.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/seeder")
public class DataSeederController {

    @Autowired
    private DataSeeder dataSeeder;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSeedingStatus() {
        Map<String, Object> response = new HashMap<>();
        
        long userCount = userRepository.count();
        long clientCount = clientRepository.count();
        long orderCount = orderRepository.count();
        
        response.put("users", userCount);
        response.put("clients", clientCount);
        response.put("orders", orderCount);
        response.put("isSeeded", userCount > 0 && clientCount > 0);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/seed")
    public ResponseEntity<Map<String, Object>> manuallySeedData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Clear existing data
            orderRepository.deleteAll();
            clientRepository.deleteAll();
            userRepository.deleteAll();
            
            // Run seeder
            dataSeeder.run();
            
            response.put("status", "SUCCESS");
            response.put("message", "Database seeded successfully!");
            response.put("users", userRepository.count());
            response.put("clients", clientRepository.count());
            response.put("orders", orderRepository.count());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Failed to seed database: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearAllData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            orderRepository.deleteAll();
            clientRepository.deleteAll();
            userRepository.deleteAll();
            
            response.put("status", "SUCCESS");
            response.put("message", "All data cleared successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Failed to clear data: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
} 