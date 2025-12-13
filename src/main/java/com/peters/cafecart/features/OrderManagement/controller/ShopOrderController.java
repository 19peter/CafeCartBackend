package com.peters.cafecart.features.OrderManagement.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.features.OrderManagement.dto.OrderUpdateDto;
import com.peters.cafecart.features.OrderManagement.dto.ShopOrderDto;
import com.peters.cafecart.features.OrderManagement.enums.OrderStatusEnum;
import com.peters.cafecart.features.OrderManagement.service.OrderServiceImpl;

@RestController
@RequestMapping(Constants.API_V1 + "/orders/shop")
public class ShopOrderController {
    @Autowired
    OrderServiceImpl orderService;

    @GetMapping("/")
    public ResponseEntity<List<ShopOrderDto>> getAllOrdersForShop(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDate date) {
        return ResponseEntity.ok(orderService.getAllOrdersForShop(user.getId(), date));
    }

    @PostMapping("/update-order")
    public ResponseEntity<OrderStatusEnum> updateOrder(@AuthenticationPrincipal CustomUserPrincipal user,
            @RequestBody OrderUpdateDto orderUpdateDto) {
        OrderStatusEnum orderStatusEnum = orderService.updateOrderStatusToNextState(user.getId(), orderUpdateDto);
        return ResponseEntity.ok(orderStatusEnum);
    }

    @PostMapping("/cancel-order")
    public ResponseEntity<HttpStatus> cancelOrder(@AuthenticationPrincipal CustomUserPrincipal user,
            @RequestBody OrderUpdateDto orderUpdateDto) {
        orderService.cancelOrder(user.getId(), orderUpdateDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
