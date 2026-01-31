package com.peters.cafecart.config;

import java.util.List;

import com.peters.cafecart.exceptions.CustomExceptions.UnauthorizedAccessException;
import com.peters.cafecart.features.Admin.entity.Admin;
import com.peters.cafecart.features.Admin.repository.AdminRepository;
import com.peters.cafecart.shared.interfaces.Authenticatable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.CustomerManagement.repository.CustomerRepository;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.ShopManagement.repository.VendorShopsRepository;
import com.peters.cafecart.features.VendorManagement.Repository.VendorAccessAccountRepository;
import com.peters.cafecart.features.VendorManagement.entity.VendorAccessAccount;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired private CustomerRepository customerRepository;
    @Autowired private VendorShopsRepository vendorShopRepository;
    @Autowired private VendorAccessAccountRepository vendorAccessAccountRepository;
    @Autowired private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        throw new UnsupportedOperationException("Use domain-specific loaders: loadCustomerByUsername, loadVendorShopByUsername, loadVendorAccessAccountByUsername");
    }

    public CustomUserPrincipal loadCustomerByUsername(String username) {
        Customer c = customerRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (c.getIsEmailVerified() == null || !c.getIsEmailVerified()) {
            throw new ValidationException("Please verify your email before logging in");
        }

        return buildUserDetails(c, "ROLE_CUSTOMER");
    }

    public CustomUserPrincipal loadVendorShopByUsername(String username) {
        VendorShop vs = vendorShopRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor Shop not found"));

        if (!vs.getIsActive())
            throw new UnauthorizedAccessException("Unauthorized Login");
        return buildUserDetails(vs, "ROLE_SHOP");
    }

    public CustomUserPrincipal loadVendorAccessAccountByUsername(String username) {
        VendorAccessAccount vaa = vendorAccessAccountRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        if (!vaa.getIsActive())
            throw new UnauthorizedAccessException("Unauthorized Login");
        return buildUserDetails(vaa, "ROLE_VENDOR");
    }

    public CustomUserPrincipal loadAdminByUsername(String username) {
        Admin admin = adminRepository.findByEmail(username)
                .orElseThrow(()-> new ResourceNotFoundException("Admin Not found"));
        return buildUserDetails(admin, "ROLE_ADMIN");
    }

    private CustomUserPrincipal buildUserDetails(Authenticatable user, String role) {
        String username = extractUsername(user);
        String password = extractPassword(user);
        Long id = extractId(user);

        return new CustomUserPrincipal(id,
                username,
                password,
                true,
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority(role)));
    }

    private String extractUsername(Authenticatable user) {
        if (user.getEmail() != null) return user.getEmail();
        throw new IllegalArgumentException("Unknown user type");
    }

    private String extractPassword(Authenticatable user) {
        if (user.getPassword() != null) return user.getPassword();
        throw new IllegalArgumentException("Unknown user type");
    }

    private Long extractId(Authenticatable user) {
        if (user.getId() != null) return user.getId();
        throw new IllegalArgumentException("Unknown user type");
    }
}