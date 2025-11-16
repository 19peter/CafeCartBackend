package com.peters.cafecart.features.CartManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peters.cafecart.features.CartManagement.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("select ci.cart.customer.id from CartItem ci where ci.id = :cartItemId")
    Long findCustomerIdByCartItemId(@Param("cartItemId") Long cartItemId);

    @Modifying
    @Query(value = "delete from cart_items where cart_id = :cartId", nativeQuery = true)
    void deleteCartItemsByCartId(@Param("cartId") Long cartId);
}
