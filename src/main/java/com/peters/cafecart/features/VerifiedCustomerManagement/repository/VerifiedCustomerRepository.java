package com.peters.cafecart.features.VerifiedCustomerManagement.repository;

import com.peters.cafecart.features.VerifiedCustomerManagement.entity.VerifiedCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@EnableJpaRepositories
@Repository
public interface VerifiedCustomerRepository
        extends JpaRepository<VerifiedCustomer, Long> {

    boolean existsByCustomerIdAndVendorId(Long customerId, Long vendorId);

    Optional<VerifiedCustomer> findByCustomerIdAndVendorId(Long customerId, Long vendorId);

    List<VerifiedCustomer> findByVendorId(Long vendorId);

    List<VerifiedCustomer> findByCustomerId(Long customerId);

    @Query("""
    SELECT vc
    FROM VerifiedCustomer vc
    WHERE vc.customer.id IN :customerIds
      AND vc.vendor.id = :vendorId
    """)
    List<VerifiedCustomer> findByCustomerIdInAndVendorId(
            @Param("customerIds") Set<Long> customerIds,
            @Param("vendorId") Long vendorId
    );
}
