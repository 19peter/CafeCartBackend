package com.peters.cafecart.features.InventoryManagement.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;

import com.peters.cafecart.features.InventoryManagement.repository.InventoryRepository;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.InventoryManagement.projections.ShopProductSummary;
import com.peters.cafecart.features.InventoryManagement.projections.VendorProduct;
import com.peters.cafecart.features.CartManagement.dto.CartItemDto;
import com.peters.cafecart.features.InventoryManagement.dto.VendorProductDto;
import com.peters.cafecart.features.InventoryManagement.entity.Inventory;
import com.peters.cafecart.features.InventoryManagement.mappers.InventoryMappers;
import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    InventoryRepository inventoryRepository;
    @Autowired
    InventoryMappers inventoryMappers;
    @Autowired
    ProductServiceImpl productService;
    @Autowired
    EntityManager entityManager;

    @Override
    public Page<VendorProductDto> getProductsByVendorShopIdAndCategory(
            Long vendorShopId,
            int quantity,
            int page,
            int size,
            String category) {
        if (vendorShopId == null)
            throw new ValidationException("Vendor Shop ID cannot be null");
        Pageable pageable = PageRequest.of(page, size);
        Page<VendorProduct> vendorProductPage = inventoryRepository.findByVendorShopIdAndQuantityGreaterThanAndCategory(
                vendorShopId,
                quantity,
                category,
                pageable);
        return inventoryMappers.toDtoPage(vendorProductPage);
    }

    @Override
    public VendorProductDto getProductByVendorShopIdAndProductId(
            Long vendorShopId,
            Long productId) {
        if (vendorShopId == null || productId == null)
            throw new ValidationException("Vendor Shop ID and Product ID cannot be null");
        Optional<VendorProduct> vendorProduct = inventoryRepository.findByVendorShopIdAndProductId(vendorShopId,
                productId);
        if (vendorProduct.isEmpty())
            throw new ResourceNotFoundException("Product not found");
        return inventoryMappers.toDto(vendorProduct.get());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void reduceInventoryStockInBulk(Long vendorShopId, List<CartItemDto> orderItems) {

        if (vendorShopId == null)
            throw new ValidationException("Vendor Shop ID cannot be null");
        for (CartItemDto orderItem : orderItems) {
            Long productId = orderItem.getProductId();
            int quantity = orderItem.getQuantity();
            System.out.println(productId);
            System.out.println(quantity);
            if (productId == null || quantity <= 0)
                throw new ValidationException("Product ID and Quantity cannot be null or less than or equal to zero");
            int res = inventoryRepository.reduceInventoryStock(vendorShopId, productId, quantity);
            System.out.println(res);
        }

    }

    @Override
    public Optional<ShopProductSummary> getShopProductSummaryByVendorShopIdAndProductId(
            Long vendorShopId,
            Long productId) {
        if (vendorShopId == null || productId == null)
            throw new ValidationException("Vendor Shop ID and Product ID cannot be null");
        Optional<ShopProductSummary> shopProductSummary = inventoryRepository
                .findShopProductSummaryByVendorShopIdAndProductId(vendorShopId, productId);
        return shopProductSummary;
    }

    @Transactional
    @Override
    public boolean updateInventoryStock(Long vendorShopId, Long productId, int quantity) {
        if (quantity < 0) throw new ValidationException("Quantity cannot be less than zero");

        Optional<Inventory> inventoryCheck = inventoryRepository.findInventoryByVendorShopIdAndProductId(
                vendorShopId,
                productId);
                
        if (inventoryCheck.isEmpty()) throw new ResourceNotFoundException("Inventory not found");
        Inventory inventory = inventoryCheck.get();
        if (inventory.getQuantity() == quantity) return true;
        inventory.setQuantity(quantity);
        inventoryRepository.save(inventory);
        return true;
    }

    @Override
    @Transactional
    public boolean createInventory(Long vendorShopId, Long productId, int quantity) {
        try {
            Inventory inventory = new Inventory();
            VendorShop vendorShop = entityManager.getReference(VendorShop.class, vendorShopId);
            inventory.setVendorShop(vendorShop);
            Product product = entityManager.getReference(Product.class, productId);
            inventory.setProduct(product);
            inventory.setQuantity(quantity);
            inventoryRepository.save(inventory);
            return true;
        } catch (Exception e) {
            throw new ValidationException("Failed to create inventory " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public int createProductInventoryForAllShops(Long productId, Set<Long> vendorShopIds) {
        LocalDateTime createdAt = LocalDateTime.now();
        return inventoryRepository.addProductToVendorShops(productId, vendorShopIds, createdAt);
    }

    @Override
    public List<Inventory> getInventoryByVendorShopId(Long vendorShopId) {
        return inventoryRepository.findInventoryByVendorShopId(vendorShopId);
    }
}
