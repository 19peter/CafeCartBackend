package com.peters.cafecart.features.InventoryManagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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
    int reduceInventoryStock(
                @Param("vendorShopId") Long vendorShopId,
                @Param("productId") Long productId,
                @Param("quantity") int quantity);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.vendorShop.id = :vendorShopId AND i.product.id = :productId")            
    Optional<Inventory> findInventoryByVendorShopIdAndProductId(
                @Param("vendorShopId") Long vendorShopId,
                @Param("productId") Long productId);

    @Query("SELECT i FROM Inventory i WHERE i.vendorShop.id = :vendorShopId")            
    List<Inventory> findInventoryByVendorShopId(
                @Param("vendorShopId") Long vendorShopId);

    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO inventory (quantity, product_id, vendor_shop_id)
    SELECT 0, p.id, vs.id
    FROM products p
    CROSS JOIN vendor_shops vs
    WHERE p.id = :productId
      AND vs.id IN (:vendorShopIds)
      AND NOT EXISTS (
          SELECT 1
          FROM inventory i
          WHERE i.product_id = p.id
            AND i.vendor_shop_id = vs.id
      )
""", nativeQuery = true)
    int addProductToVendorShops(
            @Param("productId") Long productId,
            @Param("vendorShopIds") Set<Long> vendorShopIds
    );
}
