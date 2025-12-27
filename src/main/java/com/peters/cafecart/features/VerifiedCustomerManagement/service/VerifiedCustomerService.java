package com.peters.cafecart.features.VerifiedCustomerManagement.service;

import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import com.peters.cafecart.features.VerifiedCustomerManagement.dto.VerifiedCustomerDto;
import com.peters.cafecart.features.VerifiedCustomerManagement.entity.VerifiedCustomer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface VerifiedCustomerService {
    boolean verifyCustomer(Vendor vendor, VendorShop vendorShop, Customer customer);
    VerifiedCustomerDto isCustomerVerified(Vendor vendor, VendorShop vendorShop, Customer customer);
    boolean unverifyCustomer(Vendor vendor, VendorShop vendorShop, Customer customer);
    List<VerifiedCustomer> bulkFetchVerifiedCustomers(Set<Long> customerIds, Long vendorId);
}
