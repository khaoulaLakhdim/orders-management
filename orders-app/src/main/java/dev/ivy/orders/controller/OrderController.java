package dev.ivy.orders.controller;

import dev.ivy.orders.entity.Order;
import dev.ivy.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    // GET /api/orders?page={page}&size={size} → 200 + paged JSON
    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long clientId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> ordersPage;
            
            if (clientId != null) {
                // Filtered by clientId
                ordersPage = orderRepository.findByClientId(clientId, pageable);
            } else {
                // All orders
                ordersPage = orderRepository.findAll(pageable);
            }
            
            response.put("success", true);
            response.put("message", "Orders retrieved successfully");
            response.put("orders", ordersPage.getContent());
            response.put("totalElements", ordersPage.getTotalElements());
            response.put("totalPages", ordersPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);
            response.put("hasNext", ordersPage.hasNext());
            response.put("hasPrevious", ordersPage.hasPrevious());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to retrieve orders: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // GET /api/orders/{id} → 200 + JSON or 404
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Order> orderOpt = orderRepository.findById(id);
            if (orderOpt.isPresent()) {
                response.put("success", true);
                response.put("message", "Order retrieved successfully");
                response.put("order", orderOpt.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Order not found");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to retrieve order: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // POST /api/orders → 201 + created record
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Order order) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validation
            if (order.getClient() == null || order.getClient().getId() == null) {
                response.put("success", false);
                response.put("message", "Client is required");
                return ResponseEntity.status(400).body(response);
            }
            
            if (order.getProductName() == null || order.getProductName().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Product name is required");
                return ResponseEntity.status(400).body(response);
            }
            
            if (order.getQuantity() == null || order.getQuantity() <= 0) {
                response.put("success", false);
                response.put("message", "Valid quantity is required");
                return ResponseEntity.status(400).body(response);
            }
            
            if (order.getPrice() == null || order.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                response.put("success", false);
                response.put("message", "Valid price is required");
                return ResponseEntity.status(400).body(response);
            }
            
            Order savedOrder = orderRepository.save(order);
            response.put("success", true);
            response.put("message", "Order created successfully");
            response.put("order", savedOrder);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create order: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // PUT /api/orders/{id} → 200 + updated record
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Order> existingOrderOpt = orderRepository.findById(id);
            if (!existingOrderOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Order not found");
                return ResponseEntity.status(404).body(response);
            }
            
            Order existingOrder = existingOrderOpt.get();
            
            // Validation
            if (order.getClient() == null || order.getClient().getId() == null) {
                response.put("success", false);
                response.put("message", "Client is required");
                return ResponseEntity.status(400).body(response);
            }
            
            if (order.getProductName() == null || order.getProductName().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Product name is required");
                return ResponseEntity.status(400).body(response);
            }
            
            if (order.getQuantity() == null || order.getQuantity() <= 0) {
                response.put("success", false);
                response.put("message", "Valid quantity is required");
                return ResponseEntity.status(400).body(response);
            }
            
            if (order.getPrice() == null || order.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                response.put("success", false);
                response.put("message", "Valid price is required");
                return ResponseEntity.status(400).body(response);
            }
            
            // Update fields
            existingOrder.setClient(order.getClient());
            existingOrder.setProductName(order.getProductName());
            existingOrder.setQuantity(order.getQuantity());
            existingOrder.setPrice(order.getPrice());
            existingOrder.setOrderDate(order.getOrderDate());
            
            Order updatedOrder = orderRepository.save(existingOrder);
            response.put("success", true);
            response.put("message", "Order updated successfully");
            response.put("order", updatedOrder);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update order: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // DELETE /api/orders/{id} → 204
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Order> orderOpt = orderRepository.findById(id);
            if (!orderOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Order not found");
                return ResponseEntity.status(404).body(response);
            }
            
            orderRepository.deleteById(id);
            response.put("success", true);
            response.put("message", "Order deleted successfully");
            return ResponseEntity.status(204).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete order: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
} 