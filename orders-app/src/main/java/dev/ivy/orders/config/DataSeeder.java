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
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final int TARGET_ORDER_COUNT = 1000;

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
        seedUsersIfEmpty();
        seedClientsIfEmpty();
        seedOrdersUpToTarget();
    }

    private void seedUsersIfEmpty() {
        if (userRepository.count() > 0) return;

        String pw = passwordEncoder.encode("password");
        List<User> users = List.of(
            new User("admin",   pw, "ADMIN"),
            new User("manager", pw, "MANAGER"),
            new User("user1",   pw, "USER"),
            new User("user2",   pw, "USER")
        );
        userRepository.saveAll(users);
        System.out.println("👥 Seeded " + users.size() + " users (password = 'password')");
    }

    private void seedClientsIfEmpty() {
        if (clientRepository.count() > 0) return;

        List<Client> clients = List.of(
            new Client("Acme Corporation",   "ACME001", "New York"),
            new Client("Tech Solutions Ltd", "TECH002","San Francisco"),
            new Client("Global Industries",  "GLOB003", "Chicago"),
            new Client("Innovation Systems","INNO004", "Boston"),
            new Client("Digital Dynamics",   "DIGI005", "Seattle"),
            new Client("Future Technologies","FUTU006", "Austin"),
            new Client("Smart Solutions",    "SMAR007", "Denver"),
            new Client("Elite Enterprises",  "ELIT008", "Miami")
        );
        clientRepository.saveAll(clients);
        System.out.println("🏢 Seeded " + clients.size() + " clients");
    }

    private void seedOrdersUpToTarget() {
        long existing = orderRepository.count();
        if (existing >= TARGET_ORDER_COUNT) {
            System.out.println("📦 Already have " + existing + " orders—skipping order seeding.");
            return;
        }

        List<Client> clients = clientRepository.findAll();
        if (clients.isEmpty()) {
            System.out.println("⚠️  No clients found—cannot seed orders.");
            return;
        }

        String[] productNames    = {"Laptop Pro","Office Software","Cloud Services",
                                    "Server Hardware","Network Equipment","Industrial Software",
                                    "Safety Equipment","AI Development Kit","Testing Tools",
                                    "Digital Marketing Suite","Analytics Platform","IoT Sensors",
                                    "Machine Learning API","Smart Home Devices","Automation Software",
                                    "Enterprise Security","Business Intelligence","CRM System"};
        String[] paymentMethods  = {"CARD","BANK_TRANSFER","CASH"};
        String[] expeditions     = {"EXPRESS","STANDARD","PRIORITY"};
        String[] statuses        = {"COMPLETED","PENDING","IN_PROGRESS"};

        Random rnd = new Random();
        List<Order> toSave = new ArrayList<>();
        int needed = TARGET_ORDER_COUNT - (int)existing;

        for (int i = 0; i < needed; i++) {
            Client client = clients.get(rnd.nextInt(clients.size()));
            String product  = productNames[rnd.nextInt(productNames.length)];
            int quantity    = rnd.nextInt(5) + 1;
            BigDecimal price = BigDecimal.valueOf(50 + rnd.nextDouble() * 9950)
                                       .setScale(2, BigDecimal.ROUND_HALF_UP);
            LocalDate date   = LocalDate.now().minusDays(rnd.nextInt(365));
            int type         = rnd.nextInt(3) + 1;
            String payment   = paymentMethods[rnd.nextInt(paymentMethods.length)];
            String expedition= expeditions[rnd.nextInt(expeditions.length)];
            String status    = statuses[rnd.nextInt(statuses.length)];

            Order order = new Order(product, client, quantity, price, date);
            order.setType(type);
            order.setPaymentMethod(payment);
            order.setExpedition(expedition);
            order.setStatus(status);
            toSave.add(order);
        }

        orderRepository.saveAll(toSave);
        System.out.println("📦 Seeded " + toSave.size() + " orders (total now "
                           + orderRepository.count() + ")");
    }
}
