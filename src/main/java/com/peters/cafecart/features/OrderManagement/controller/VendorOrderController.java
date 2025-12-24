package com.peters.cafecart.features.OrderManagement.controller;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.features.OrderManagement.dto.OrderDto;
import com.peters.cafecart.features.OrderManagement.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_V1  + "/orders/vendor")
public class VendorOrderController {

    @Autowired private OrderServiceImpl orderService;

    @GetMapping
    public Page<OrderDto> getOrdersByMonth(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam int shopId,
            @AuthenticationPrincipal CustomUserPrincipal user
            ) {
        return orderService.getAllOrdersByMonth(shopId, year, month, PageRequest.of(page, size));
    }
}
