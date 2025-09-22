package com.peters.cafecart.features.InventoryManagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peters.cafecart.features.InventoryManagement.entity.Inventory;
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
            "WHERE v.id = :vendorShopId AND i.quantity > :quantity")
    Page<VendorProduct> findByVendorShopIdAndQuantityGreaterThan(
            @Param("vendorShopId") Long vendorShopId,
            @Param("quantity") int quantity,
            Pageable pageable);

}
