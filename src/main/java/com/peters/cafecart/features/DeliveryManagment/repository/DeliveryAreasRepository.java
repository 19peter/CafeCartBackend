package com.peters.cafecart.features.DeliveryManagment.repository;

import com.peters.cafecart.features.DeliveryManagment.entity.DeliveryAreas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryAreasRepository extends JpaRepository<DeliveryAreas, Long> {
    List<DeliveryAreas> findAllByVendorShopId(Long vendorShopId);

    Optional<DeliveryAreas> findById(Long id);
}
