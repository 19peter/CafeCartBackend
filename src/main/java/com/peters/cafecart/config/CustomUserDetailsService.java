package com.peters.cafecart.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.CustomerManagement.repository.CustomerRepository;
import com.peters.cafecart.features.VendorManagement.Repository.VendorShopsRepository;
import com.peters.cafecart.features.VendorManagement.Repository.VendorAccessAccountRepository;
import com.peters.cafecart.features.VendorManagement.entity.VendorAccessAccount;
import com.peters.cafecart.features.VendorManagement.entity.VendorShop;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VendorShopsRepository vendorShopRepository;

    @Autowired
    private VendorAccessAccountRepository vendorAccessAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String
                 username) {
        throw new UnsupportedOperationException("Use domain-specific loaders: loadCustomerByUsername, loadVendorShopByUsername, loadVendorAccessAccountByUsername");
    }

    public CustomUserPrincipal loadCustomerByUsername(String username) {
        Customer c = customerRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return buildUserDetails(c, "ROLE_CUSTOMER");
    }

    public CustomUserPrincipal loadVendorShopByUsername(String username) {
        VendorShop vs = vendorShopRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor Shop not found"));

        return buildUserDetails(vs, "ROLE_SHOP");
    }

    public CustomUserPrincipal loadVendorAccessAccountByUsername(String username) {
        VendorAccessAccount vaa = vendorAccessAccountRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
        return buildUserDetails(vaa, "ROLE_VENDOR");
    }

    private CustomUserPrincipal buildUserDetails(Object user, String role) {
        String username = extractUsername(user);
        String password = extractPassword(user);
        Long id = extractId(user);

        return new CustomUserPrincipal(id, username, password, true, true, true, true,
                List.of(new SimpleGrantedAuthority(role)));
    }

    private String extractUsername(Object user) {
        if (user instanceof Customer)
            return ((Customer) user).getEmail();
        if (user instanceof VendorAccessAccount)
            return ((VendorAccessAccount) user).getEmail();
        if (user instanceof VendorShop)
            return ((VendorShop) user).getEmail();
        throw new IllegalArgumentException("Unknown user type");
    }

    private String extractPassword(Object user) {
        if (user instanceof Customer)
            return ((Customer) user).getPassword();
        if (user instanceof VendorAccessAccount)
            return ((VendorAccessAccount) user).getPassword();
        if (user instanceof VendorShop)
            return ((VendorShop) user).getPassword();
        throw new IllegalArgumentException("Unknown user type");
    }

    private Long extractId(Object user) {
        if (user instanceof Customer)
            return ((Customer) user).getId();
        if (user instanceof VendorAccessAccount)
            return ((VendorAccessAccount) user).getId();
        if (user instanceof VendorShop)
            return ((VendorShop) user).getId();
        throw new IllegalArgumentException("Unknown user type");
    }
}