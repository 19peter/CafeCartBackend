package com.peters.cafecart.features.OrderManagement.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.peters.cafecart.features.OrderManagement.projections.SalesSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peters.cafecart.features.OrderManagement.entity.Order;
import com.peters.cafecart.features.OrderManagement.enums.OrderStatusEnum;
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
          SELECT DISTINCT o
          FROM Order o
          LEFT JOIN FETCH o.items i
          JOIN FETCH o.customer c
          WHERE o.vendorShop.id = :shopId
            AND o.createdAt BETWEEN :startDate AND :endDate
      """)
  List<Order> findShopOrdersByDate(
      @Param("shopId") Long shopId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("""
          SELECT DISTINCT o
          FROM Order o
          LEFT JOIN FETCH o.items i
          JOIN FETCH o.customer c
          WHERE o.vendorShop.id = :shopId
      """)
  List<Order> findShopOrders(@Param("shopId") Long shopId);

  List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);


  @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate AND o.createdAt < :endDate AND o.vendorShop.id = :shopId ORDER BY o.createdAt DESC")
  Page<Order> findOrdersByMonth(
          @Param("startDate") LocalDateTime startDate,
          @Param("endDate") LocalDateTime endDate,
          Pageable pageable,
          @Param("shopId") int shopId

  );

  @Query("SELECT COUNT(o) AS count, SUM(o.totalAmount) AS total FROM Order o WHERE o.createdAt >= :startDate AND o.createdAt < :endDate AND o.vendorShop.id = :shopId AND o.status = :status")
  SalesSummary getTotalOrdersAndSalesByMonthForShop(
          @Param("startDate") LocalDateTime startDate,
          @Param("endDate") LocalDateTime endDate,
          @Param("shopId") Long shopId,
          @Param("status") OrderStatusEnum status
          );

  boolean existsByCustomerIdAndStatus(Long customerId, OrderStatusEnum statusEnum);
}
