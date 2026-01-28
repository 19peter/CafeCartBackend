package com.peters.cafecart.features.OrderManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.peters.cafecart.features.OrderManagement.entity.OrderItem;
import com.peters.cafecart.features.OrderManagement.projections.ItemDetail;

public interface OrderItemsRepository extends JpaRepository<OrderItem, Long> {
    
    @Query("SELECT i FROM OrderItem i LEFT JOIN FETCH i.additions WHERE i.order.id = :orderId")
    List<OrderItem> findByOrderId(Long orderId);
}
