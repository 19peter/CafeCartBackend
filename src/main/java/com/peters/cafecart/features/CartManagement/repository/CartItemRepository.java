package com.peters.cafecart.features.CartManagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peters.cafecart.features.CartManagement.entity.Cart;
import com.peters.cafecart.features.CartManagement.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("select ci.cart.customer.id from CartItem ci where ci.id = :cartItemId")
    Long findCustomerIdByCartItemId(@Param("cartItemId") Long cartItemId);

    @Query("select ci.cart from CartItem ci where ci.id = :cartItemId")
    Optional<Cart> findCartByCartItemId(@Param("cartItemId") Long cartItemId);
}
