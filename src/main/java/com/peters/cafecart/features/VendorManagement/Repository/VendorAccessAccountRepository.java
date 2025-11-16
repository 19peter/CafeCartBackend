package com.peters.cafecart.features.VendorManagement.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peters.cafecart.features.VendorManagement.entity.VendorAccessAccount;

public interface VendorAccessAccountRepository extends JpaRepository<VendorAccessAccount, Long> {
    Optional<VendorAccessAccount> findByEmail(String email);
}
