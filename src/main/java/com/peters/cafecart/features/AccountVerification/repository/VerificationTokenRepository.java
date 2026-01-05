package com.peters.cafecart.features.AccountVerification.repository;

import com.peters.cafecart.features.AccountVerification.entity.VerificationToken;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByCustomer(Customer customer);
}
