package com.peters.cafecart.features.CartManagement.controller;

import com.peters.cafecart.workflows.AddToCartUseCase;
import com.peters.cafecart.workflows.GetCartAndOrderSummaryUseCase;
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
import com.peters.cafecart.features.CartManagement.dto.request.AddToCartDto;
import com.peters.cafecart.features.CartManagement.dto.response.CartAndOrderSummaryDto;
import com.peters.cafecart.features.CartManagement.dto.request.CartOptionsDto;
import com.peters.cafecart.features.CartManagement.dto.request.RemoveFromCart;
import com.peters.cafecart.features.CartManagement.service.CartServiceImpl;

@RestController
@RequestMapping(Constants.API_V1 + "/cart")
public class CartController {
    public record CartShopResponse(String shop) {}

    @Autowired private CartServiceImpl cartService;
    @Autowired private AddToCartUseCase addToCartUseCase;
    @Autowired private GetCartAndOrderSummaryUseCase getCartAndOrderSummaryUseCase;

    @PostMapping("/get-cart")
    public ResponseEntity<CartAndOrderSummaryDto> getCart(@AuthenticationPrincipal CustomUserPrincipal user,
                                                          @RequestBody CartOptionsDto cartOptionsDto) {
        return ResponseEntity.ok(getCartAndOrderSummaryUseCase.execute(user.getId(), cartOptionsDto));
    }

    @GetMapping("/get-cart-shop")
    public ResponseEntity<CartShopResponse> getCartShop(@AuthenticationPrincipal CustomUserPrincipal user) {
        String shopName = cartService.getCartShop(user.getId());
        return ResponseEntity.ok(new CartShopResponse(shopName));
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<HttpStatus> addToCart(@AuthenticationPrincipal CustomUserPrincipal user,
                                                @RequestBody AddToCartDto addToCartDto) {


        addToCartUseCase.execute(user.getId(), addToCartDto);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @PostMapping("/add-one-to-cart")
    public ResponseEntity<HttpStatus> addOneToCart(@AuthenticationPrincipal CustomUserPrincipal user,
                                                   @RequestBody AddToCartDto addToCartDto) {
        addToCartUseCase.execute(user.getId(), addToCartDto);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @PostMapping("/remove-one-from-cart")
    public ResponseEntity<HttpStatus> removeOneFromCart(@AuthenticationPrincipal CustomUserPrincipal user, @RequestBody RemoveFromCart removeFromCart) {
        cartService.removeOneFromCart(user.getId(), removeFromCart);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @PostMapping("/remove-item-from-cart")
    public ResponseEntity<HttpStatus> removeItemFromCart(@AuthenticationPrincipal CustomUserPrincipal user, @RequestBody RemoveFromCart removeFromCart) {
        cartService.clearItem(removeFromCart.getCartItemId());
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
