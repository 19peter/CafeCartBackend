package com.peters.cafecart.features.ShopManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections.VendorShopIndexCover;
import com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections.VendorShopLocation;

import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface VendorShopsRepository extends JpaRepository<VendorShop, Long> {
    // Optional<VendorShopIndexCover> findById(Long id);
    List<VendorShopIndexCover> findByVendorId(Long vendorId);
    Optional<VendorShop> findByEmail(String email);
    Optional<VendorShopLocation> findLatitudeAndLongitudeAndCityById(Long id);
}
