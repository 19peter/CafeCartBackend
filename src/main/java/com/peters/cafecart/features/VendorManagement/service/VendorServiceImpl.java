package com.peters.cafecart.features.VendorManagement.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.peters.cafecart.features.ShopManagement.dto.ShopDetailsDto;
import com.peters.cafecart.features.VendorManagement.Repository.VendorAccessAccountRepository;
import com.peters.cafecart.features.VendorManagement.dto.request.CreateVendorDto;
import com.peters.cafecart.features.VendorManagement.dto.response.CreatedVendorDto;
import com.peters.cafecart.features.VendorManagement.dto.response.VendorInfoDto;
import com.peters.cafecart.features.VendorManagement.entity.VendorAccessAccount;
import com.peters.cafecart.shared.dtos.Response.UploadUrlResponse;
import com.peters.cafecart.shared.services.S3.S3SignedUrlService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.peters.cafecart.features.VendorManagement.dto.VendorIdNameDto;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import com.peters.cafecart.features.VendorManagement.dto.VendorDto;
import com.peters.cafecart.features.VendorManagement.Repository.VendorsRepository;
import com.peters.cafecart.features.VendorManagement.mappers.VendorMappers;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.VendorManagement.Projections.VendorProjections.VendorIdName;
import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;

@Service
public class VendorServiceImpl implements VendorService {

    @Autowired PasswordEncoder passwordEncoder;
    @Autowired VendorsRepository vendorsRepository;
    @Autowired VendorMappers vendorMappers;
    @Autowired VendorAccessAccountRepository vendorAccessAccountRepository;
    @Autowired private S3SignedUrlService s3SignedUrlService;

    @Override
    public Page<VendorIdNameDto> getAllVendors(int page, int size) {
        if(page < 0 || size < 0) throw new ValidationException("Page and size must be greater than 0");
        Pageable pageable = PageRequest.of(page, size);
        Page<VendorIdName> vendorsProjection = vendorsRepository.findByIsActiveTrue(pageable);
        return vendorMappers.toDtoPageIdName(vendorsProjection);
    }

    @Override
    public Optional<VendorDto> getVendorById(Long id) {
        if(id == null) throw new ValidationException("Vendor ID cannot be null");
        return vendorsRepository.findById(id).map(vendorMappers::toDto);
    }

    @Override
    public Optional<Vendor> getVendor(Long id) {
        if (id == null) throw  new ValidationException("Id is missing");
        return vendorsRepository.findById(id);
    }

    @Override
    public Set<Long> getShopIdsByVendorId(Long vendorId) {
        if(vendorId == null) throw new ValidationException("Vendor ID cannot be null");
        Optional<Vendor> vendor = vendorsRepository.findById(vendorId);
        if(vendor.isEmpty()) throw new ResourceNotFoundException("Vendor not found");
        return vendor.get().getShops().stream().map(VendorShop::getId).collect(Collectors.toSet());
    }

    @Override
    public List<ShopDetailsDto> getAllShopsDetails(Long vendorId) {
        Vendor vendor = vendorsRepository.findById(vendorId).orElseThrow(() -> new ResourceNotFoundException("Vendor Not Found"));
        List<VendorShop> shops = vendor.getShops();
        return toShopDetailsDto(shops);
    }

    @Override
    @Transactional
    public CreatedVendorDto createVendor(CreateVendorDto createVendorDto) {
        Vendor vendor = createVendorFromDto(createVendorDto);
        vendor = vendorsRepository.save(vendor);

        VendorAccessAccount vaa = createVendorAccessAccount(createVendorDto);
        vaa.setVendor(vendor);
        vendorAccessAccountRepository.save(vaa);

        CreatedVendorDto dto = new CreatedVendorDto();
        dto.setEmail(createVendorDto.getEmail());
        dto.setName(createVendorDto.getName());
        dto.setPhoneNumber(createVendorDto.getPhoneNumber());
        dto.setVaaEmail(createVendorDto.getVaaEmail());
        dto.setId(vendor.getId());
        String imageUrl = createVendorDto.getImageUrl();
        String contentType = createVendorDto.getContentType();

        if (imageUrl != null && contentType != null) {
            UploadUrlResponse urlResponse = s3SignedUrlService.generateUploadUrl(vendor.getId(), imageUrl,contentType);
            dto.setFileUrl(urlResponse.getFileUrl());
            dto.setUploadUrl(urlResponse.getUploadUrl());
            vendor.setImageUrl(urlResponse.getFileUrl());
            vendorsRepository.save(vendor);
        }
        return dto;
    }

    @Override
    public List<VendorInfoDto> getAllVendors() {
        List<Vendor> vendors = vendorsRepository.findAll();
        List<VendorInfoDto> list = new ArrayList<>();
        vendors.forEach(vendor -> {
            VendorInfoDto dto = new VendorInfoDto();
            dto.setCreatedAt(vendor.getCreatedAt());
            dto.setIsActive(vendor.getIsActive());
            dto.setEmail(vendor.getEmail());
            dto.setName(vendor.getName());
            dto.setId(vendor.getId());
            dto.setPhoneNumber(vendor.getPhoneNumber());
            dto.setTotalShops(vendor.getShops().size());
            list.add(dto);
        });

        return list;
    }

    @Override
    public boolean vendorExistsById(Long id) {
        return vendorsRepository.existsById(id);
    }

    private List<ShopDetailsDto> toShopDetailsDto(List<VendorShop> shopsList) {
        List<ShopDetailsDto> shopDetailsList = new ArrayList<>();
        shopsList.forEach(shop -> {
            ShopDetailsDto shopDetailsDto = new ShopDetailsDto();
            shopDetailsDto.setId(shop.getId());
            shopDetailsDto.setName(shop.getName());
            shopDetailsDto.setPhoneNumber(shop.getPhoneNumber());
            shopDetailsDto.setAddress(shop.getAddress());
            shopDetailsDto.setCity(shop.getCity());
            shopDetailsDto.setIsOnline(shop.getIsOnline());
            shopDetailsDto.setIsActive(shop.getIsActive());
            shopDetailsDto.setEmail(shop.getEmail());
            shopDetailsList.add(shopDetailsDto);
        });
        return shopDetailsList;
    }


    private Vendor createVendorFromDto(CreateVendorDto dto) {
        Vendor vendor = new Vendor();
        vendor.setEmail(dto.getEmail());
        vendor.setName(dto.getName());
        vendor.setPhoneNumber(dto.getPhoneNumber());
        vendor.setCreatedAt(LocalDateTime.now());
        vendor.setIsActive(true);
        vendor.setImageUrl(dto.getImageUrl());
        return vendor;
    }

    private VendorAccessAccount createVendorAccessAccount(CreateVendorDto dto) {
        VendorAccessAccount vaa = new VendorAccessAccount();
        vaa.setEmail(dto.getVaaEmail());
        vaa.setPassword(passwordEncoder.encode(dto.getVaaPassword()));
        vaa.setIsActive(true);
        return  vaa;
    }
}
