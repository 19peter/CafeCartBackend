package com.peters.cafecart.features.OrderManagement.controller;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.exceptions.CustomExceptions.UnauthorizedAccessException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.OrderManagement.dto.OrderDto;
import com.peters.cafecart.features.OrderManagement.dto.OrdersTotalPerMonthDto;
import com.peters.cafecart.features.OrderManagement.service.OrderServiceImpl;
import com.peters.cafecart.features.VendorManagement.service.VendorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(Constants.API_V1  + "/orders/vendor")
public class VendorOrderController {

    @Autowired private OrderServiceImpl orderService;
    @Autowired private VendorServiceImpl vendorService;

    @GetMapping("/month")
    public Page<OrderDto> getOrdersByMonthPerShop(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam int shopId,
            @AuthenticationPrincipal CustomUserPrincipal user
            ) {
        Set<Long> shopIds = vendorService.getShopIdsByVendorId(user.getId());
        if (!shopIds.contains((long) shopId)) throw new UnauthorizedAccessException("Unauthorized");
        return orderService.getAllOrdersByMonth(shopId, year, month, PageRequest.of(page, size));
    }

    @GetMapping("/month/total")
    public ResponseEntity<OrdersTotalPerMonthDto> getMonthSaleSummaryPerShop(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam Long shopId,
            @AuthenticationPrincipal CustomUserPrincipal user
            ) {
        Set<Long> shopIds = vendorService.getShopIdsByVendorId(user.getId());
        if (!shopIds.contains(shopId)) throw new UnauthorizedAccessException("Unauthorized");
        return ResponseEntity.ok(orderService.getSalesSummaryForMonthForShop(shopId, year, month));
    }
}
