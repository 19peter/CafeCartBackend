package com.peters.cafecart.features.ProductsManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.entity.Category;


public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p.category FROM Inventory i JOIN i.product p WHERE i.vendorShop.id = :shopId")
    List<Category> findCategoriesByShopId(Long shopId);

}
