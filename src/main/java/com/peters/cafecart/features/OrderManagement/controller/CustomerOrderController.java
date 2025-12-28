package com.peters.cafecart.features.OrderManagement.controller;

import com.peters.cafecart.workflows.CreateOrderUseCase;
import org.springframework.web.bind.annotation.*;

import com.peters.cafecart.features.CartManagement.dto.request.CartOptionsDto;
import com.peters.cafecart.features.OrderManagement.dto.OrderDto;
import com.peters.cafecart.features.OrderManagement.dto.OrderItemDto;
import com.peters.cafecart.features.OrderManagement.service.OrderServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.Constants.Constants;

@RestController
@RequestMapping(Constants.API_V1 + "/orders/customer")
public class CustomerOrderController {

    @Autowired OrderServiceImpl orderService;
    @Autowired CreateOrderUseCase createOrderUseCase;
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrdersForCustomer(@AuthenticationPrincipal CustomUserPrincipal user) {
        return ResponseEntity.ok(orderService.getAllOrdersForCustomer(user.getId()));
    }

    @GetMapping("/items")
    public ResponseEntity<List<OrderItemDto>> getOrderItems(@AuthenticationPrincipal CustomUserPrincipal user,
            @RequestParam Long orderId) {
        return ResponseEntity.ok(orderService.getOrderItems(user.getId(), orderId));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createOrder(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @RequestBody CartOptionsDto cartOptionsDto,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        createOrderUseCase.createOrder(user.getId(), cartOptionsDto, idempotencyKey);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
