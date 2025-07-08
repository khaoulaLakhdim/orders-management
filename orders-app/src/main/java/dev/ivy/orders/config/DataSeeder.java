package dev.ivy.orders.config;

import dev.ivy.orders.entity.User;
import dev.ivy.orders.entity.Client;
import dev.ivy.orders.entity.Order;
import dev.ivy.orders.repository.UserRepository;
import dev.ivy.orders.repository.ClientRepository;
import dev.ivy.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only seed if database is empty
        if (userRepository.count() == 0 && clientRepository.count() == 0) {
            seedUsers();
            seedClients();
            seedOrders();
            System.out.println("✅ Database seeded successfully!");
        } else {
            System.out.println("ℹ️  Database already contains data, skipping seeding.");
        }
    }

    private void seedUsers() {
        String encodedPassword = passwordEncoder.encode("password");
        
        List<User> users = Arrays.asList(
            new User("admin", encodedPassword, "ADMIN"),
            new User("manager", encodedPassword, "MANAGER"),
            new User("user1", encodedPassword, "USER"),
            new User("user2", encodedPassword, "USER")
        );
        
        userRepository.saveAll(users);
        System.out.println("👥 Seeded " + users.size() + " users");
        System.out.println("🔑 All users have password: 'password'");
    }

    private void seedClients() {
        List<Client> clients = Arrays.asList(
            new Client("Acme Corporation", "ACME001", "New York"),
            new Client("Tech Solutions Ltd", "TECH002", "San Francisco"),
            new Client("Global Industries", "GLOB003", "Chicago"),
            new Client("Innovation Systems", "INNO004", "Boston"),
            new Client("Digital Dynamics", "DIGI005", "Seattle"),
            new Client("Future Technologies", "FUTU006", "Austin"),
            new Client("Smart Solutions", "SMAR007", "Denver"),
            new Client("Elite Enterprises", "ELIT008", "Miami")
        );
        
        clientRepository.saveAll(clients);
        System.out.println("🏢 Seeded " + clients.size() + " clients");
    }

    private void seedOrders() {
        List<Client> clients = clientRepository.findAll();
        
        List<Order> orders = Arrays.asList(
            // Orders for Acme Corporation
            createOrder("Laptop Pro", clients.get(0), 2, new BigDecimal("750.00"), LocalDate.now().minusDays(30), 1, "CARD", "EXPRESS", "COMPLETED"),
            createOrder("Office Software", clients.get(0), 1, new BigDecimal("2300.50"), LocalDate.now().minusDays(15), 2, "BANK_TRANSFER", "STANDARD", "PENDING"),
            
            // Orders for Tech Solutions Ltd
            createOrder("Cloud Services", clients.get(1), 3, new BigDecimal("296.92"), LocalDate.now().minusDays(25), 1, "CARD", "EXPRESS", "COMPLETED"),
            createOrder("Server Hardware", clients.get(1), 1, new BigDecimal("3200.00"), LocalDate.now().minusDays(10), 3, "CASH", "PRIORITY", "IN_PROGRESS"),
            createOrder("Network Equipment", clients.get(1), 2, new BigDecimal("875.13"), LocalDate.now().minusDays(5), 2, "CARD", "STANDARD", "PENDING"),
            
            // Orders for Global Industries
            createOrder("Industrial Software", clients.get(2), 1, new BigDecimal("4500.00"), LocalDate.now().minusDays(20), 1, "BANK_TRANSFER", "EXPRESS", "COMPLETED"),
            createOrder("Safety Equipment", clients.get(2), 4, new BigDecimal("300.00"), LocalDate.now().minusDays(8), 2, "CARD", "STANDARD", "PENDING"),
            
            // Orders for Innovation Systems
            createOrder("AI Development Kit", clients.get(3), 1, new BigDecimal("2800.50"), LocalDate.now().minusDays(12), 1, "CASH", "PRIORITY", "COMPLETED"),
            createOrder("Testing Tools", clients.get(3), 5, new BigDecimal("190.15"), LocalDate.now().minusDays(3), 2, "CARD", "STANDARD", "IN_PROGRESS"),
            
            // Orders for Digital Dynamics
            createOrder("Digital Marketing Suite", clients.get(4), 1, new BigDecimal("3600.00"), LocalDate.now().minusDays(18), 3, "BANK_TRANSFER", "EXPRESS", "COMPLETED"),
            createOrder("Analytics Platform", clients.get(4), 2, new BigDecimal("1050.13"), LocalDate.now().minusDays(7), 1, "CARD", "STANDARD", "PENDING"),
            
            // Orders for Future Technologies
            createOrder("IoT Sensors", clients.get(5), 10, new BigDecimal("180.00"), LocalDate.now().minusDays(22), 2, "CASH", "PRIORITY", "COMPLETED"),
            createOrder("Machine Learning API", clients.get(5), 1, new BigDecimal("2900.50"), LocalDate.now().minusDays(6), 1, "CARD", "EXPRESS", "IN_PROGRESS"),
            
            // Orders for Smart Solutions
            createOrder("Smart Home Devices", clients.get(6), 3, new BigDecimal("416.92"), LocalDate.now().minusDays(14), 2, "BANK_TRANSFER", "STANDARD", "COMPLETED"),
            createOrder("Automation Software", clients.get(6), 1, new BigDecimal("3400.00"), LocalDate.now().minusDays(2), 3, "CARD", "PRIORITY", "PENDING"),
            
            // Orders for Elite Enterprises
            createOrder("Enterprise Security", clients.get(7), 1, new BigDecimal("4200.25"), LocalDate.now().minusDays(16), 1, "CASH", "EXPRESS", "COMPLETED"),
            createOrder("Business Intelligence", clients.get(7), 2, new BigDecimal("800.00"), LocalDate.now().minusDays(4), 2, "CARD", "STANDARD", "IN_PROGRESS"),
            createOrder("CRM System", clients.get(7), 1, new BigDecimal("3800.50"), LocalDate.now().minusDays(1), 1, "BANK_TRANSFER", "PRIORITY", "PENDING")
        );
        
        orderRepository.saveAll(orders);
        System.out.println("📦 Seeded " + orders.size() + " orders");
    }

    private Order createOrder(String productName, Client client, Integer quantity, BigDecimal price, LocalDate orderDate,
                            Integer type, String paymentMethod, String expedition, String status) {
        Order order = new Order(productName, client, quantity, price, orderDate);
        order.setType(type);
        order.setPaymentMethod(paymentMethod);
        order.setExpedition(expedition);
        order.setStatus(status);
        return order;
    }
} 