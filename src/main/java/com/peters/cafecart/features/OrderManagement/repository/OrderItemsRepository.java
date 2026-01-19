package com.peters.cafecart.features.OrderManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.peters.cafecart.features.OrderManagement.entity.OrderItem;
import com.peters.cafecart.features.OrderManagement.projections.ItemDetail;

public interface OrderItemsRepository extends JpaRepository<OrderItem, Long> {
    
    @Query("SELECT i.id as id, i.productOption.product.name as name, i.quantity as quantity FROM OrderItem i WHERE i.order.id = :orderId")
    List<ItemDetail> findByOrderId(Long orderId);
}
