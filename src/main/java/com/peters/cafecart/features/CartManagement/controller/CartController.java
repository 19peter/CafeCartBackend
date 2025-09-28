package com.peters.cafecart.features.CartManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peters.cafecart.features.CartManagement.dto.AddToCartDto;
import com.peters.cafecart.features.CartManagement.dto.RemoveFromCart;
import com.peters.cafecart.features.CartManagement.service.CartService;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/add-to-cart")
    public ResponseEntity<HttpStatus> addToCart(@RequestBody AddToCartDto addToCartDto) {
        cartService.addOneToCart(addToCartDto);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @PostMapping("/remove-from-cart")
    public ResponseEntity<HttpStatus> removeFromCart(@RequestBody RemoveFromCart removeFromCart) {
        cartService.removeOneFromCart(removeFromCart);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
