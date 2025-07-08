package dev.ivy.orders.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class PingController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/api/test/orders")
    public Map<String, Object> testOrders() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Test endpoint working");
        response.put("orders", new Object[]{
            Map.of("id", 1, "productName", "Test Product", "clientName", "Test Client", "amount", 100.00)
        });
        return response;
    }

    @GetMapping("/api/test/clients")
    public Map<String, Object> testClients() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Test endpoint working");
        response.put("clients", new Object[]{
            Map.of("id", 1, "name", "Test Client", "code", "TEST001", "city", "Test City")
        });
        return response;
    }
} 