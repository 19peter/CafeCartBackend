package com.peters.cafecart.features.VendorManagement.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import com.peters.cafecart.features.VendorManagement.Projections.VendorProjections.VendorIdName;
import com.peters.cafecart.features.VendorManagement.Projections.VendorProjections.VendorSummary;

public interface VendorsRepository extends JpaRepository<Vendor, Long> {
    Page<VendorSummary> findAllProjectedByIsActiveTrue(Pageable pageable);
    Page<VendorIdName> findByIsActiveTrue(Pageable pageable);
    Optional<Vendor> findById(Long id);
}
