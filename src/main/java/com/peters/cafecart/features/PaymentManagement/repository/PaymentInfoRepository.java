package com.peters.cafecart.features.PaymentManagement.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.peters.cafecart.features.PaymentManagement.entity.PaymentInfo;
import com.peters.cafecart.features.PaymentManagement.projections.VendorPaymentInfo;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {
    Optional<VendorPaymentInfo> findByVendorId(Long vendorId);
}
