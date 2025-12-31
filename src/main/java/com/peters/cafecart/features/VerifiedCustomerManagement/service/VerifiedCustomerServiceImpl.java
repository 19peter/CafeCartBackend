package com.peters.cafecart.features.VerifiedCustomerManagement.service;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import com.peters.cafecart.features.VerifiedCustomerManagement.dto.VerifiedCustomerDto;
import com.peters.cafecart.features.VerifiedCustomerManagement.entity.VerifiedCustomer;
import com.peters.cafecart.features.VerifiedCustomerManagement.repository.VerifiedCustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class VerifiedCustomerServiceImpl implements VerifiedCustomerService {
    @Autowired VerifiedCustomerRepository verifiedCustomerRepository;


    @Override
    @Transactional
    public boolean verifyCustomer(Vendor vendor, VendorShop vendorShop, Customer customer) {

        VerifiedCustomer verifiedCustomer = verifiedCustomerRepository
                .findByCustomerIdAndVendorId(customer.getId(), vendor.getId())
                .orElseGet(() -> createVerifiedCustomer(customer, vendor));

        if (Boolean.TRUE.equals(verifiedCustomer.getIsVerified())) {
            return true; // already verified → idempotent
        }

        verifiedCustomer.setIsVerified(true);
        verifiedCustomer.setLastUpdatedBy(vendorShop);
        verifiedCustomerRepository.save(verifiedCustomer);

        return true;
    }

    @Override
    public VerifiedCustomerDto isCustomerVerified(Vendor vendor, VendorShop vendorShop, Customer customer) {
        VerifiedCustomerDto verifiedCustomerDto = new VerifiedCustomerDto();
        verifiedCustomerDto.setVerified(false);

        Optional<VerifiedCustomer> verifiedCustomerCheck = verifiedCustomerRepository.findByCustomerIdAndVendorId(customer.getId(), vendor.getId());

        if (verifiedCustomerCheck.isEmpty()) return verifiedCustomerDto;

        VerifiedCustomer verifiedCustomer = verifiedCustomerCheck.get();
        verifiedCustomerDto.setCustomerId(verifiedCustomer.getCustomer().getId());
        verifiedCustomerDto.setVerifiedById(verifiedCustomer.getLastUpdatedBy().getId());
        verifiedCustomerDto.setVendorId(verifiedCustomer.getVendor().getId());
        verifiedCustomerDto.setVerified(verifiedCustomer.getIsVerified());
        return verifiedCustomerDto;

    }

    @Override
    public boolean unverifyCustomer(Vendor vendor, VendorShop vendorShop, Customer customer) {
        VerifiedCustomer verifiedCustomer = verifiedCustomerRepository.findByCustomerIdAndVendorId(customer.getId(), vendor.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Resource Not Found"));
        if (!verifiedCustomer.getIsVerified()) return true;
        verifiedCustomer.setIsVerified(false);
        verifiedCustomer.setLastUpdatedBy(vendorShop);
        verifiedCustomerRepository.save(verifiedCustomer);
        return true;
    }

    @Override
    public List<VerifiedCustomer> bulkFetchVerifiedCustomers(Set<Long> customerIds, Long vendorId) {
        return verifiedCustomerRepository
                        .findByCustomerIdInAndVendorId(customerIds, vendorId);
    }

    private VerifiedCustomer createVerifiedCustomer(Customer customer, Vendor vendor) {
        VerifiedCustomer vc = new VerifiedCustomer();
        vc.setCustomer(customer);
        vc.setVendor(vendor);
        vc.setVerifiedAt(LocalDateTime.now());
        return vc;
    }

}
