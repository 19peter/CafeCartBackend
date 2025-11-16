package com.peters.cafecart.features.OrderManagement.service;

import java.math.BigDecimal;
import java.util.List;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.OrderManagement.dto.OrderDto;
import com.peters.cafecart.features.OrderManagement.dto.OrderItemDto;
import com.peters.cafecart.features.OrderManagement.dto.OrderUpdateDto;
import com.peters.cafecart.features.OrderManagement.entity.Order;
import com.peters.cafecart.features.OrderManagement.entity.OrderItem;
import com.peters.cafecart.features.OrderManagement.enums.OrderStatusEnum;
import com.peters.cafecart.features.OrderManagement.enums.PaymentStatus;
import com.peters.cafecart.features.OrderManagement.projections.OrderStatusSummary;
import com.peters.cafecart.features.OrderManagement.repository.OrderItemsRepository;
import com.peters.cafecart.features.OrderManagement.repository.OrderRepository;
import com.peters.cafecart.shared.enums.OrderTypeEnum;
import com.peters.cafecart.shared.enums.PaymentMethodEnum;
import com.peters.cafecart.features.CartManagement.dto.CartAndOrderSummaryDto;
import com.peters.cafecart.features.CartManagement.dto.CartItemDto;
import com.peters.cafecart.features.CartManagement.dto.CartOptionsDto;
import com.peters.cafecart.features.CartManagement.service.CartServiceImpl;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.InventoryManagement.service.InventoryService;
import com.peters.cafecart.features.OrderManagement.mapper.OrderMapper;
import com.peters.cafecart.features.PaymentManagement.Service.PaymentServiceImpl;
import com.peters.cafecart.features.VendorManagement.entity.VendorShop;

@Service
public class OrderServiceImpl implements OrderService {
    SecureRandom RANDOM = new SecureRandom();
    @Autowired OrderRepository orderRepository;
    @Autowired OrderItemsRepository orderItemsRepository;
    
    @Autowired CartServiceImpl cartService;
    @Autowired InventoryService inventoryService;
    @Autowired PaymentServiceImpl paymentService;
    @PersistenceContext EntityManager entityManager;
    @Autowired OrderMapper orderMapper;

    @Override
    public List<OrderDto> getAllOrdersForShop(Long shopId) {
        return orderMapper.toDtoList(orderRepository.findOrderDetailShopByShopId(shopId, PaymentStatus.PAID));
    }

    @Override
    public List<OrderItemDto> getOrderItems(Long shopId, Long orderId) {
        //First check that the order belongs to the shop
        Long orderShopId = orderRepository.findOrderShopIdById(orderId);
        if (orderShopId == null || !orderShopId.equals(shopId)) throw new ValidationException("Order belongs to a different shop");
        //then get the order items
        return orderMapper.toOrderItemDtoList(orderItemsRepository.findByOrderId(orderId));
    }

    @Override
    public List<OrderDto> getAllOrdersForCustomer(Long customerId) {
        return null;
    }

    @Override
    public OrderDto getOrderById(Long id) {
        return null;
    }

    @Override
    @Transactional
    public void createOrder(Long customerId, CartOptionsDto cartOptionsDto) {
       CartAndOrderSummaryDto cartAndOrderSummaryDto =  cartService.getCartAndOrderSummary(customerId, cartOptionsDto);
        
       try {
           inventoryService.reduceInventoryStockInBulk(customerId, cartAndOrderSummaryDto.getOrderSummary().getItems());
       } catch (Exception e) {
           throw new ValidationException("Failed to reduce inventory stock " + e.getMessage());
       }
       
       Order order = createOrderEntityFromCartAndOrderSummaryDto(cartAndOrderSummaryDto);

       if (cartAndOrderSummaryDto.getOrderSummary().getPaymentMethod() == PaymentMethodEnum.CREDIT_CARD) {
           order.setPaymentStatus(PaymentStatus.PENDING);
           try {
               paymentService.createIntention(cartAndOrderSummaryDto);
           } catch (Exception e) {
               throw new ValidationException("Failed to create payment intention " + e.getMessage());
           }
       }
       
       saveOrder(order);
    }

    @Override
    @Transactional
    public void updateOrderStatusToNextState(Long shopId, OrderUpdateDto order) {

        OrderStatusSummary orderProjection = orderRepository.findOrderStatusSummaryById(order.getOrderId());

        if (!orderProjection.getVendorShopId().equals(shopId)) throw new ValidationException("Order belongs to a different shop");
        OrderStatusEnum nextStatus = computeNextStatus(orderProjection.getStatus(), orderProjection.getOrderType());
        
        if (nextStatus == null) throw new ValidationException("Order status cannot be updated");
        updateOrderStatus(order.getOrderId(), shopId, nextStatus);
    }

    @Transactional
    private void saveOrder(Order order) {
        try {
            orderRepository.save(order);
        } catch (Exception e) {
            throw new ValidationException("Failed to save order " + e.getMessage());
        }
    }

    @Transactional
    private void updateOrderStatus(Long orderId, Long shopId, OrderStatusEnum status) {
        try {
            orderRepository.updateOrderStatus(orderId, shopId, status);
        } catch (Exception e) {
            throw new ValidationException("Failed to update order status" + e.getMessage());
        }
    }

    private Order createOrderEntityFromCartAndOrderSummaryDto(CartAndOrderSummaryDto cartAndOrderSummaryDto) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(entityManager.getReference(Customer.class, cartAndOrderSummaryDto.getCartSummary().getCustomerId()));
        order.setVendorShop(entityManager.getReference(VendorShop.class, cartAndOrderSummaryDto.getCartSummary().getShopId()));
        order.setOrderType(cartAndOrderSummaryDto.getOrderSummary().getOrderType());
        order.getItems().addAll(createOrderItemsFromCartItems(cartAndOrderSummaryDto.getOrderSummary().getItems()));
        order.setDeliveryAddress("abc");
        order.setPaymentMethod(cartAndOrderSummaryDto.getOrderSummary().getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setStatus(OrderStatusEnum.PENDING);
        order.setTotalAmount(new BigDecimal(cartAndOrderSummaryDto.getOrderSummary().getTotal()));
        order.setDeliveryFee(new BigDecimal(cartAndOrderSummaryDto.getOrderSummary().getDeliveryFee()));
        return order;
    }

    private List<OrderItem> createOrderItemsFromCartItems(List<CartItemDto> cartItems) {
        return cartItems.stream()
                .map(this::createOrderItemFromCartItem)
                .toList();
    }

    private OrderItem createOrderItemFromCartItem(CartItemDto cartItemDto) {        
        OrderItem orderItem = entityManager.getReference(OrderItem.class, cartItemDto.getId());
        return orderItem;
    }

    private OrderStatusEnum computeNextStatus(OrderStatusEnum current, OrderTypeEnum orderType) {
        if (current == null || orderType == null)
            return null;

        switch (current) {
            case PENDING:
                return OrderStatusEnum.PREPARING;
            case PREPARING:
                return (orderType == OrderTypeEnum.DELIVERY)
                        ? OrderStatusEnum.OUT_FOR_DELIVERY
                        : OrderStatusEnum.READY_FOR_PICKUP;
            case READY_FOR_PICKUP:
            case OUT_FOR_DELIVERY:
                return OrderStatusEnum.DELIVERED;
            case DELIVERED:
            case CANCELLED:
            default:
                return OrderStatusEnum.PREPARING;
        }
    }

    private String generateOrderNumber() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        
        // Random 4 chars for uniqueness
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            randomPart.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }

        return "ORD-" + datePart + "-" + randomPart;
    }
}
