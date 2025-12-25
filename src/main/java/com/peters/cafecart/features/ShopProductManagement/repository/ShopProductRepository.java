package com.peters.cafecart.features.ShopProductManagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peters.cafecart.features.ShopProductManagement.projection.ShopProductStock;

import jakarta.transaction.Transactional;

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
                 p.category.name          AS categoryName,
                 p.category.id            AS categoryId,
                 p.isStockTracked         AS isStockTracked,
                 p.description            AS description,
                 sp.isAvailable           AS isAvailable
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

  List<ShopProduct> findAllByVendorShopId(long vendorShopId);

  @Query("SELECT sp FROM ShopProduct sp WHERE sp.product.id = :productId AND sp.vendorShop.id = :vendorShopId")
  Optional<ShopProduct> findShopProductByProductAndVendorShop(
      @Param("productId") long productId,
      @Param("vendorShopId") long vendorShopId);

  @Query("SELECT sp.product.id FROM ShopProduct sp WHERE sp.vendorShop.id = :vendorShopId")
  Set<Long> findAllProductIdsForVendorShop(@Param("vendorShopId") long vendorShopId);

  /**
   * Creates ShopProduct entries for a product across multiple vendor shops
   * 
   * @param productId     the product to add
   * @param vendorShopIds list of vendor shop IDs
   */
  @Modifying
  @Transactional
  @Query(value = "INSERT INTO shop_product (product_id, vendor_shop_id, is_available) " +
      "SELECT :productId, vs.id, :isAvailable " +
      "FROM vendor_shops vs " +
      "WHERE vs.id IN :vendorShopIds", nativeQuery = true)
  void addProductToVendorShops(@Param("productId") Long productId,
      @Param("vendorShopIds") Set<Long> vendorShopIds,
      @Param("isAvailable") boolean isAvailable);

  /**
   * Creates ShopProduct entries for multiple products in a single vendor shop
   * 
   * @param productIds   list of product IDs to add
   * @param vendorShopId the vendor shop ID
   */
  @Modifying
  @Transactional
  @Query(value = "INSERT INTO shop_product (product_id, vendor_shop_id, is_available) " +
      "SELECT p.id, :vendorShopId, :isAvailable " +
      "FROM product p " +
      "WHERE p.id IN :productIds", nativeQuery = true)
  void addProductsToVendorShop(
      @Param("productIds") Set<Long> productIds,
      @Param("vendorShopId") Long vendorShopId,
      @Param("isAvailable") boolean isAvailable);
}
