package com.peters.cafecart.features.CartManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peters.cafecart.features.CartManagement.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
}
