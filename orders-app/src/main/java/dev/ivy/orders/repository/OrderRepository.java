package dev.ivy.orders.repository;

import dev.ivy.orders.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Basic CRUD operations are automatically provided by JpaRepository
    
    // Find orders by client ID with pagination
    Page<Order> findByClientId(Long clientId, Pageable pageable);
} 