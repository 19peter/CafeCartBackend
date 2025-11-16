package com.peters.cafecart.features.VendorManagement.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections.VendorShopIndexCover;
import com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections.VendorShopLocation;
import com.peters.cafecart.features.VendorManagement.entity.VendorShop;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface VendorShopsRepository extends JpaRepository<VendorShop, Long> {
    List<VendorShopIndexCover> findByVendorId(Long vendorId);
    Optional<VendorShop> findByEmail(String email);

    Optional<VendorShopLocation> findLatitudeAndLongitudeAndCityById(Long id);
}
