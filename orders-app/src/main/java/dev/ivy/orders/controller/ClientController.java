package dev.ivy.orders.controller;

import dev.ivy.orders.entity.Client;
import dev.ivy.orders.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "*")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    // GET /api/clients → 200 + JSON list
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllClients() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Client> clients = clientRepository.findAll();
            response.put("success", true);
            response.put("message", "Clients retrieved successfully");
            response.put("clients", clients);
            response.put("count", clients.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to retrieve clients: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // GET /api/clients/{id} → 200 + JSON or 404
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getClientById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Client> clientOpt = clientRepository.findById(id);
            if (clientOpt.isPresent()) {
                response.put("success", true);
                response.put("message", "Client retrieved successfully");
                response.put("client", clientOpt.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Client not found");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to retrieve client: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // POST /api/clients → 201 + created record
    @PostMapping
    public ResponseEntity<Map<String, Object>> createClient(@RequestBody Client client) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validation
            if (client.getName() == null || client.getName().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Client name is required");
                return ResponseEntity.status(400).body(response);
            }
            
            if (client.getCode() == null || client.getCode().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Client code is required");
                return ResponseEntity.status(400).body(response);
            }
            
            if (client.getCity() == null || client.getCity().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Client city is required");
                return ResponseEntity.status(400).body(response);
            }
            
            // Check if code already exists
            if (clientRepository.existsByCode(client.getCode())) {
                response.put("success", false);
                response.put("message", "Client code already exists");
                return ResponseEntity.status(400).body(response);
            }
            
            Client savedClient = clientRepository.save(client);
            response.put("success", true);
            response.put("message", "Client created successfully");
            response.put("client", savedClient);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create client: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // PUT /api/clients/{id} → 200 + updated record
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateClient(@PathVariable Long id, @RequestBody Client client) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Client> existingClientOpt = clientRepository.findById(id);
            if (!existingClientOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Client not found");
                return ResponseEntity.status(404).body(response);
            }
            
            Client existingClient = existingClientOpt.get();
            
            // Validation
            if (client.getName() == null || client.getName().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Client name is required");
                return ResponseEntity.status(400).body(response);
            }
            
            if (client.getCode() == null || client.getCode().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Client code is required");
                return ResponseEntity.status(400).body(response);
            }
            
            if (client.getCity() == null || client.getCity().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Client city is required");
                return ResponseEntity.status(400).body(response);
            }
            
            // Check if code already exists for different client
            if (!client.getCode().equals(existingClient.getCode()) && 
                clientRepository.existsByCode(client.getCode())) {
                response.put("success", false);
                response.put("message", "Client code already exists");
                return ResponseEntity.status(400).body(response);
            }
            
            // Update fields
            existingClient.setName(client.getName());
            existingClient.setCode(client.getCode());
            existingClient.setCity(client.getCity());
            
            Client updatedClient = clientRepository.save(existingClient);
            response.put("success", true);
            response.put("message", "Client updated successfully");
            response.put("client", updatedClient);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update client: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // DELETE /api/clients/{id} → 204
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteClient(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Client> clientOpt = clientRepository.findById(id);
            if (!clientOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Client not found");
                return ResponseEntity.status(404).body(response);
            }
            
            clientRepository.deleteById(id);
            response.put("success", true);
            response.put("message", "Client deleted successfully");
            return ResponseEntity.status(204).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete client: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
} 