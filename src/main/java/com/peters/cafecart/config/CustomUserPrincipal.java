package com.peters.cafecart.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.peters.cafecart.features.VendorManagement.entity.VendorAccessAccount;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;

public class CustomUserPrincipal implements UserDetails {
    
    private Long id;
    private String username;
    private String password;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;
    private List<GrantedAuthority> authorities;
    
    public CustomUserPrincipal(Long id, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, List<GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = authorities;
    }


    public static CustomUserPrincipal fromVendorShop(VendorShop vendorShop) {
        return new CustomUserPrincipal(
            vendorShop.getId(),
            vendorShop.getEmail(),
            vendorShop.getPassword(),
            true,
            true,
            true,
            true,
            List.of(new SimpleGrantedAuthority("ROLE_SHOP"))
            );
    }

    public static CustomUserPrincipal fromCustomer(Customer customer) {
        return new CustomUserPrincipal(
            customer.getId(),
            customer.getEmail(),
            customer.getPassword(),
            true,
            true,
            true,
            true,
            List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
            );
    }

    public static CustomUserPrincipal fromVendor(VendorAccessAccount vendor) {
        return new CustomUserPrincipal(
            vendor.getId(),
            vendor.getEmail(),
            vendor.getPassword(),
            true,
            true,
            true,
            true,
            List.of(new SimpleGrantedAuthority("ROLE_VENDOR"))
            );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
