package com.peters.cafecart.features.ShopManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections.VendorShopIndexCover;
import com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections.VendorShopLocation;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface VendorShopsRepository extends JpaRepository<VendorShop, Long> {
    @Query("""
    SELECT
        vs.id AS id,
        v.id AS vendorId,
        vs.name AS name,
        vs.address AS address,
        vs.phoneNumber AS phoneNumber,
        vs.isActive AS isShopActive,
        v.isActive AS isVendorActive
    FROM VendorShop vs
    JOIN vs.vendor v
    WHERE v.name = :name
""")
    List<VendorShopIndexCover> findByVendorName(@Param("name") String name);

    Optional<VendorShop> findByEmail(String email);
    Optional<VendorShopLocation> findLatitudeAndLongitudeAndCityById(Long id);
    boolean existsByIdAndBlockedCustomers_Id(Long shopId, Long customerId);
}
