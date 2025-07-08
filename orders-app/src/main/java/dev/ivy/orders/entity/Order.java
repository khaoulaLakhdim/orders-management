package dev.ivy.orders.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private Client client;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;
    
    @Column(name = "type")
    private Integer type;
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Column(name = "expedition", length = 100)
    private String expedition;
    
    @Column(name = "status", length = 50)
    private String status;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Default constructor
    public Order() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with fields
    public Order(String productName, Client client, Integer quantity, BigDecimal price, LocalDate orderDate) {
        this.productName = productName;
        this.client = client;
        this.quantity = quantity;
        this.price = price;
        this.orderDate = orderDate;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    // Add a method to get client name for JSON serialization
    public String getClientName() {
        return client != null ? client.getName() : null;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public LocalDate getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
    
    public Integer getType() {
        return type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getExpedition() {
        return expedition;
    }
    
    public void setExpedition(String expedition) {
        this.expedition = expedition;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", client=" + (client != null ? client.getName() : "null") +
                ", quantity=" + quantity +
                ", price=" + price +
                ", orderDate=" + orderDate +
                ", type=" + type +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", expedition='" + expedition + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
} 