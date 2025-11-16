package com.peters.cafecart.features.CartManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.features.CartManagement.dto.AddToCartDto;
import com.peters.cafecart.features.CartManagement.dto.CartAndOrderSummaryDto;
import com.peters.cafecart.features.CartManagement.dto.CartOptionsDto;
import com.peters.cafecart.features.CartManagement.dto.RemoveFromCart;
import com.peters.cafecart.features.CartManagement.service.CartServiceImpl;

@RestController
@RequestMapping(Constants.API_V1 + "/carts")
public class CartController {
    @Autowired
    private CartServiceImpl cartService;

    @GetMapping("/get-cart")
    public ResponseEntity<CartAndOrderSummaryDto> getCart(@AuthenticationPrincipal CustomUserPrincipal user, @RequestBody CartOptionsDto cartOptionsDto) {
        return ResponseEntity.ok(cartService.getCartAndOrderSummary(user.getId(), cartOptionsDto));
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<HttpStatus> addToCart(@AuthenticationPrincipal CustomUserPrincipal user, @RequestBody AddToCartDto addToCartDto) {
        cartService.addOneToCart(user.getId(), addToCartDto);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @PostMapping("/remove-from-cart")
    public ResponseEntity<HttpStatus> removeFromCart(@AuthenticationPrincipal CustomUserPrincipal user, @RequestBody RemoveFromCart removeFromCart) {
        cartService.removeOneFromCart(user.getId(), removeFromCart);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
