package com.peters.cafecart.features.CartManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peters.cafecart.features.CartManagement.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
    
}
