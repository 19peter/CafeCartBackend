package com.peters.cafecart.features.ShopProductManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peters.cafecart.features.ShopProductManagement.projection.ShopProductStock;
import com.peters.cafecart.features.ShopProductManagement.entity.ShopProduct;

public interface ShopProductRepository extends JpaRepository<ShopProduct, Long> {

    @Query("""
                SELECT sp.id                    AS id,
                       v.id                     AS vendorShopId,
                       p.id                     AS productId,
                       COALESCE(inv.quantity, 0) AS quantity,
                       p.name                   AS name,
                       p.price                  AS price,
                       p.imageUrl               AS imageUrl,
                       p.category.name          AS categoryName,
                       p.category.id            AS categoryId,
                       p.description            AS description,
                       p.isStockTracked         AS isStockTracked,
                       sp.isAvailable           AS isAvailable
                FROM ShopProduct sp
                JOIN sp.product p
                JOIN sp.vendorShop v
                LEFT JOIN Inventory inv
                  ON inv.product = p AND inv.vendorShop = v
                WHERE v.id = :vendorShopId
                  AND p.id = :productId
            """)
    Optional<ShopProductStock> findByProductAndVendorShop(
            @Param("productId") long productId,
            @Param("vendorShopId") long vendorShopId);

    @Query("""
                SELECT sp.id                    AS id,
                       v.id                     AS vendorShopId,
                       p.id                     AS productId,
                       COALESCE(inv.quantity, 0) AS quantity,
                       p.name                   AS name,
                       p.price                  AS price,
                       p.imageUrl AS imageUrl,
                       p.category.name AS categoryName,
                       p.category.id AS categoryId,
                       p.isStockTracked AS isStockTracked,
                       sp.isAvailable AS isAvailable
                FROM ShopProduct sp
                JOIN sp.product p
                JOIN sp.vendorShop v
                LEFT JOIN Inventory inv
                  ON inv.product = p AND inv.vendorShop = v
                WHERE v.id = :vendorShopId
                  AND sp.isAvailable = true
            """)
    List<ShopProductStock> findAllByVendorShopIdAndIsAvailableTrue(
            @Param("vendorShopId") long vendorShopId);
}
