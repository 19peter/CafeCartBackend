package com.peters.cafecart.features.OrderManagement.service;

import java.time.LocalDate;
import java.util.List;

import com.peters.cafecart.features.CartManagement.dto.OrderSummaryDto;
import com.peters.cafecart.features.OrderManagement.dto.*;
import com.peters.cafecart.features.OrderManagement.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.peters.cafecart.features.OrderManagement.enums.OrderStatusEnum;

@Service
public interface OrderService {

    List<ShopOrderDto> getAllOrdersForShop(Long shopId, LocalDate date);

    List<OrderItemDto> getOrderItems(Long shopId, Long orderId);

    List<OrderDto> getAllOrdersForCustomer(Long customerId);

    Page<OrderDto> getAllOrdersByMonth(int shopId, int year, int month, Pageable pageable);

    OrderDto getOrderById(Long id);

    void cancelOrder(Long shopId, OrderUpdateDto order);

    OrderStatusEnum updateOrderStatusToNextState(Long shopId, OrderUpdateDto order);

    OrdersTotalPerMonthDto getSalesSummaryForMonthForShop(Long shopId, int year, int month);

    PaymentStatusUpdate updateOrderPaymentStatus(PaymentStatusUpdate paymentStatusUpdate);
}
