package com.peters.cafecart.features.VendorManagement.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections.VendorShopIndexCover;
import com.peters.cafecart.features.VendorManagement.entity.VendorShop;

public interface VendorShopsRepository extends JpaRepository<VendorShop, Long> {
    List<VendorShopIndexCover> findByVendorId(Long vendorId);
}
