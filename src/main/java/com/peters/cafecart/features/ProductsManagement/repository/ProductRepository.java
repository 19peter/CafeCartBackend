package com.peters.cafecart.features.ProductsManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.entity.Category;


public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p.category FROM ShopProduct sp JOIN sp.product p WHERE sp.vendorShop.id = :shopId")
    List<Category> findCategoriesByShopId(@Param("shopId") Long shopId);

    @Query("SELECT c FROM Category c")
    List<Category> findAllCategories();

    @Query("SELECT c FROM Category c WHERE c.id = :categoryId")
    Optional<Category> findCategoryById(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.vendor.id = :vendorId")
    List<Product> findProductsByVendorId(@Param("vendorId") Long vendorId);

}
