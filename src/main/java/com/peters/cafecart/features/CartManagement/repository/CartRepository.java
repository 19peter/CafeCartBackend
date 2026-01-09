package com.peters.cafecart.features.CartManagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peters.cafecart.features.CartManagement.entity.Cart;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCustomerId(Long customerId);

    @Query("SELECT c from Cart c " +
            "LEFT JOIN FETCH c.items " +
            "LEFT JOIN FETCH c.shop  " +
            "WHERE c.customer.id = :customerId")
    Optional<Cart> findByCustomerIdWithItemsAndShop(@Param("customerId") Long customerId);
}
