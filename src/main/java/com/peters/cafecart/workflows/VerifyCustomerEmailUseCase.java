package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.AccountVerification.entity.VerificationToken;
import com.peters.cafecart.features.AccountVerification.repository.VerificationTokenRepository;
import com.peters.cafecart.features.CartManagement.service.CartServiceImpl;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.CustomerManagement.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerifyCustomerEmailUseCase {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartServiceImpl cartService;

    @Transactional
    public void execute(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ValidationException("Invalid verification token"));

        if (verificationToken.isExpired()) {
            tokenRepository.delete(verificationToken);
            throw new ValidationException("Verification token has expired");
        }

        Customer customer = verificationToken.getCustomer();
        customer.setIsEmailVerified(true);
        customerRepository.save(customer);

        // Once verified, create the cart
        cartService.createCartForNewCustomer(customer);

        // Delete the token after successful verification
        tokenRepository.delete(verificationToken);
    }
}
