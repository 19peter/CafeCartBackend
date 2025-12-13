package com.peters.cafecart.features.InventoryManagement.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peters.cafecart.features.InventoryManagement.entity.Inventory;
import com.peters.cafecart.features.InventoryManagement.projections.ShopProductSummary;
import com.peters.cafecart.features.InventoryManagement.projections.VendorProduct;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Query("SELECT i.id AS id, v.id AS vendorShopId, p.id AS productId, " +
                "i.quantity AS quantity, p.name AS name, p.price AS price " +
                "FROM Inventory i " +
                "JOIN i.product p " +
                "JOIN i.vendorShop v " +
                "WHERE v.id = :vendorShopId AND i.quantity > :quantity AND p.category.name = :category")
    Page<VendorProduct> findByVendorShopIdAndQuantityGreaterThanAndCategory(
                @Param("vendorShopId") Long vendorShopId,
                @Param("quantity") int quantity,
                @Param("category") String category,
                Pageable pageable);

    @Query("SELECT i.id AS id, v.id AS vendorShopId, p.id AS productId, " +
                "i.quantity AS quantity, p.name AS name, p.price AS price " +
                "FROM Inventory i " +
                "JOIN i.product p " +
                "JOIN i.vendorShop v " +
                "WHERE v.id = :vendorShopId AND i.product.id = :productId")
    Optional<VendorProduct> findByVendorShopIdAndProductId(
                @Param("vendorShopId") Long vendorShopId,
                @Param("productId") Long productId);

    Optional<ShopProductSummary> findShopProductSummaryByVendorShopIdAndProductId(
                @Param("vendorShopId") Long vendorShopId,
                @Param("productId") Long productId);

    
    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity - :quantity " +
                "WHERE i.vendorShop.id = :vendorShopId AND i.product.id = :productId AND i.quantity >= :quantity")
    void reduceInventoryStock(
                @Param("vendorShopId") Long vendorShopId,
                @Param("productId") Long productId,
                @Param("quantity") int quantity);

                

}
