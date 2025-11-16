package com.peters.cafecart.features.OrderManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peters.cafecart.features.OrderManagement.entity.Order;
import com.peters.cafecart.features.OrderManagement.enums.OrderStatusEnum;
import com.peters.cafecart.features.OrderManagement.enums.PaymentStatus;
import com.peters.cafecart.features.OrderManagement.projections.OrderDetailShop;
import com.peters.cafecart.features.OrderManagement.projections.OrderStatusSummary;

public interface OrderRepository extends JpaRepository<Order, Long> {
        @Query("SELECT o.vendorShop.id as vendorShopId, o.status as status, o.orderType as orderType " +
                        "FROM Order o WHERE o.id = :id")
        OrderStatusSummary findOrderStatusSummaryById(Long id);


        @Query("SELECT o.vendorShop.id as vendorShopId FROM Order o WHERE o.id = :id")
        Long findOrderShopIdById(Long id);

        @Modifying
        @Query("UPDATE Order o SET o.status = :status WHERE o.id = :id AND o.vendorShop.id = :shopId")
        int updateOrderStatus(@Param("id") Long id,
                        @Param("shopId") Long shopId,
                        @Param("status") OrderStatusEnum status);

        @Query("""
                SELECT o.id as id,
                       o.orderNumber as orderNumber,
                       o.deliveryAddress as deliveryAddress,
                       o.orderType as orderType,
                       o.status as status,
                       o.createdAt as createdAt,
                       COUNT(i) as itemCount
                FROM Order o
                LEFT JOIN o.items i
                WHERE o.vendorShop.id = :shopId
                  AND o.paymentStatus = :paymentStatus
                  AND o.status <> DELIVERED
                  AND o.status <> CANCELLED
                GROUP BY o.id, o.orderNumber, o.deliveryAddress,
                         o.orderType, o.status, o.createdAt
            """)
        List<OrderDetailShop> findOrderDetailShopByShopId(
                @Param("shopId") Long shopId,
                @Param("paymentStatus") PaymentStatus paymentStatus);


        
}
