package com.peters.cafecart.features.ProductsManagement.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.ProductsManagement.dto.request.AddCategoryDto;
import com.peters.cafecart.features.ProductsManagement.dto.request.AddProductRequestDto;
import com.peters.cafecart.features.ProductsManagement.dto.request.UpdateProductRequestDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.AddProductResponseDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.CategoryDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.ProductDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.UpdateProductResponseDto;
import com.peters.cafecart.features.ProductsManagement.entity.Category;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.mapper.CategoryMapper;
import com.peters.cafecart.features.ProductsManagement.repository.CategoryRepository;
import com.peters.cafecart.features.ProductsManagement.repository.ProductRepository;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;

import jakarta.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired ProductRepository productRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired CategoryMapper categoryMapper;
    @Autowired EntityManager entityManager;

    @Override
    public Optional<Product> getProductById(Long id) {
        if (id == null)
            throw new ResourceNotFoundException("Product ID is required");
        return productRepository.findById(id);
    }

    @Override
    public List<CategoryDto> getCategories() {
        return categoryMapper.toDtoList(productRepository.findAllCategories());
    }

    @Override
    public CategoryDto addCategory(AddCategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setIsActive(true);
        category.setCreatedAt(LocalDateTime.now());
        category = categoryRepository.save(category);

        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return  dto;
    }

    @Override
    public List<CategoryDto> getCategoriesByVendorShopId(Long vendorShopId) {
        if (vendorShopId == null)
            throw new ResourceNotFoundException("VendorShop ID is required");
        return categoryMapper.toDtoList(productRepository.findCategoriesByShopId(vendorShopId));
    }

    @Override
    public boolean isStockTracked(Long productId) {
        if (productId == null)
            throw new ResourceNotFoundException("Product ID is required");
        Optional<Product> productCheck = productRepository.findById(productId);
        if (productCheck.isEmpty())
            throw new ResourceNotFoundException("Product not found");
        return productCheck.get().getIsStockTracked();
    }

    @Override
    public AddProductResponseDto addProduct(AddProductRequestDto productDto, Long vendorId) {
        System.out.println(productDto.getCategoryId());
        System.out.println(productDto.getName());
        System.out.println(productDto.getVendorId());
        Optional<Category> categoryCheck = productRepository.findCategoryById(productDto.getCategoryId());
        if (categoryCheck.isEmpty())
            throw new ResourceNotFoundException("Category not found");

        Vendor vendor = entityManager.getReference(Vendor.class, vendorId);
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setCategory(categoryCheck.get());
        product.setVendor(vendor);
        product.setIsDeleted(false);
        product.setIsStockTracked(productDto.getIsStockTracked());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        Product savedProduct = productRepository.save(product);
        return mapToAddedProductResponseDto(savedProduct);
    }

    @Override
    public UpdateProductResponseDto updateProduct(UpdateProductRequestDto updateProductDto, Long vendorId) {
        Long productId = updateProductDto.getId();
        if (productId == null || vendorId == null)
            throw new ResourceNotFoundException("Product ID or Vendor ID is required");
        
        Optional<Product> productCheck = productRepository.findById(productId);
        if (productCheck.isEmpty())
            throw new ResourceNotFoundException("Product not found");
        
        Optional<Category> categoryCheck = productRepository.findCategoryById(updateProductDto.getCategoryId());
        if (categoryCheck.isEmpty())
            throw new ResourceNotFoundException("Category not found");

        Product product = productCheck.get();
        if (updateProductDto.getName() != null)
            product.setName(updateProductDto.getName());
        if (updateProductDto.getDescription() != null)
            product.setDescription(updateProductDto.getDescription());
        if (updateProductDto.getPrice() != null)
            product.setPrice(updateProductDto.getPrice());
        if (updateProductDto.getIsStockTracked() != null)
            product.setIsStockTracked(updateProductDto.getIsStockTracked());
        if (updateProductDto.getCategoryId() != null)
            product.setCategory(categoryCheck.get());

        product.setUpdatedAt(LocalDateTime.now());
        Product savedProduct = productRepository.save(product);
        return mapToUpdatedProductResponseDto(savedProduct);
    }

    @Override
    public List<ProductDto> getProductsForVendorShopByVendorId(Long vendorId) {
        if (vendorId == null)
            throw new ResourceNotFoundException("Vendor ID is required");
        
        List<Product> products = productRepository.findProductsByVendorId(vendorId);
        return mapToProductDtoList(products);
    }


    @Override
    public boolean saveProductImage(Long productId, String imageUrl) {
        if (productId == null || imageUrl == null) throw new ValidationException("Product Id or Image url is missing");
        Optional<Product> productCheck = productRepository.findById(productId);
        if (productCheck.isEmpty()) throw new ResourceNotFoundException("Resource Not Found");
        Product product = productCheck.get();
        product.setImageUrl(imageUrl);
        productRepository.save(product);
        return true;
    }

    private AddProductResponseDto mapToAddedProductResponseDto(Product product) {
        AddProductResponseDto addedProductResponseDto = new AddProductResponseDto();
        addedProductResponseDto.setId(product.getId());
        addedProductResponseDto.setName(product.getName());
        addedProductResponseDto.setDescription(product.getDescription());
        addedProductResponseDto.setPrice(product.getPrice());
        addedProductResponseDto.setCategoryId(product.getCategory().getId());
        addedProductResponseDto.setIsStockTracked(product.getIsStockTracked());
        addedProductResponseDto.setVendorId(product.getVendor().getId());
        return addedProductResponseDto;
    }

    private UpdateProductResponseDto mapToUpdatedProductResponseDto(Product product) {
        UpdateProductResponseDto updatedProductResponseDto = new UpdateProductResponseDto();
        updatedProductResponseDto.setId(product.getId());
        updatedProductResponseDto.setName(product.getName());
        updatedProductResponseDto.setDescription(product.getDescription());
        updatedProductResponseDto.setPrice(product.getPrice());
        updatedProductResponseDto.setCategoryId(product.getCategory().getId());
        updatedProductResponseDto.setIsStockTracked(product.getIsStockTracked());
        updatedProductResponseDto.setVendorId(product.getVendor().getId());
        return updatedProductResponseDto;
    }

    private List<ProductDto> mapToProductDtoList(List<Product> products) {
        return products.stream().map(this::mapToProductDto).collect(Collectors.toList());
    }

    private ProductDto mapToProductDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setCategoryId(product.getCategory().getId());
        productDto.setCategoryName(product.getCategory().getName());
        productDto.setIsStockTracked(product.getIsStockTracked());
        productDto.setVendorId(product.getVendor().getId());
        productDto.setImageUrl(product.getImageUrl());
        return productDto;
    }
}
