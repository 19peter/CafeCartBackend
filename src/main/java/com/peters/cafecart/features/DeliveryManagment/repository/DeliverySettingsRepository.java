package com.peters.cafecart.features.DeliveryManagment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.peters.cafecart.features.DeliveryManagment.entity.DeliverySettings;
import com.peters.cafecart.features.DeliveryManagment.projections.DeliverySettingsDetails;

public interface DeliverySettingsRepository extends JpaRepository<DeliverySettings, Long> {
    Optional<DeliverySettingsDetails> findByVendorShopId(Long vendorShopId);
    Optional<DeliverySettings> findDeliverySettingsByVendorShopId(Long vendorShopId);
}
