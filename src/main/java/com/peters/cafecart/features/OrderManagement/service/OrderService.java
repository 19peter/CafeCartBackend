package com.peters.cafecart.features.OrderManagement.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.peters.cafecart.features.CartManagement.dto.CartOptionsDto;
import com.peters.cafecart.features.OrderManagement.dto.OrderDto;
import com.peters.cafecart.features.OrderManagement.dto.OrderItemDto;
import com.peters.cafecart.features.OrderManagement.dto.OrderUpdateDto;
import com.peters.cafecart.features.OrderManagement.dto.ShopOrderDto;
import com.peters.cafecart.features.OrderManagement.enums.OrderStatusEnum;

@Service
public interface OrderService {

    List<ShopOrderDto> getAllOrdersForShop(Long shopId, LocalDate date);
    List<OrderItemDto> getOrderItems(Long shopId, Long orderId);

    List<OrderDto> getAllOrdersForCustomer(Long customerId);

    OrderDto getOrderById(Long id);

    void cancelOrder(Long shopId, OrderUpdateDto order);

    OrderStatusEnum updateOrderStatusToNextState(Long shopId, OrderUpdateDto order);
}
