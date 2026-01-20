package com.peters.cafecart.features.ProductsManagement.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.AdditionsManagement.dto.AdditionGroupDto;
import com.peters.cafecart.features.AdditionsManagement.entity.AdditionGroup;
import com.peters.cafecart.features.AdditionsManagement.entity.ProductAdditionGroup;
import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionDto;
import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionInformationDto;
import com.peters.cafecart.features.ProductsManagement.dto.request.AddCategoryDto;
import com.peters.cafecart.features.ProductsManagement.dto.request.AddProductRequestDto;
import com.peters.cafecart.features.ProductsManagement.dto.request.UpdateProductRequestDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.AddProductResponseDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.CategoryDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.ProductDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.UpdateProductResponseDto;
import com.peters.cafecart.features.ProductsManagement.entity.Category;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.entity.ProductOption;
import com.peters.cafecart.features.ProductsManagement.mapper.CategoryMapper;
import com.peters.cafecart.features.ProductsManagement.repository.CategoryRepository;
import com.peters.cafecart.features.ProductsManagement.repository.ProductOptionsRepository;
import com.peters.cafecart.features.ProductsManagement.repository.ProductRepository;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;

import com.peters.cafecart.shared.enums.ProductSizes;
import jakarta.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProductServiceImpl implements ProductService {

    @Autowired ProductRepository productRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired ProductOptionsRepository productOptionsRepository;
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
    public AddProductResponseDto addProduct(AddProductRequestDto productDto,
                                            Long vendorId,
                                            List<ProductOption> options,
                                            List<AdditionGroup> additionGroups) {

        Optional<Category> categoryCheck = productRepository.findCategoryById(productDto.getCategoryId());
        if (categoryCheck.isEmpty())
            throw new ResourceNotFoundException("Category not found");

        Vendor vendor = entityManager.getReference(Vendor.class, vendorId);
        Product product = createProduct(productDto);
        product.setCategory(categoryCheck.get());
        product.setVendor(vendor);
        product.addProductOptions(options);
        additionGroups.forEach(group -> {
            ProductAdditionGroup pad = new ProductAdditionGroup();
            pad.setProduct(product);
            pad.setAdditionGroup(group);
            product.getProductAdditionGroups().add(pad);
        });
        Product savedProduct = productRepository.save(product);

        ProductOptionInformationDto productOptionDto = mapToProductOptionsDto(product);
        List<AdditionGroupDto> additions = mapToAdditionGroupDtoList(product);
        AddProductResponseDto responseDto =  mapToAddedProductResponseDto(savedProduct);
        responseDto.setOptions(productOptionDto);
        responseDto.setAdditionGroups(additions);
        return responseDto;
    }

    @Override
    public UpdateProductResponseDto updateProduct(UpdateProductRequestDto updateProductDto,
                                                  Product product,
                                                  List<AdditionGroup> groups) {

        Category category = fetchCategory(updateProductDto.getCategoryId());
        updateProductFields(product, updateProductDto, category);

        List<ProductAdditionGroup> existing = product.getProductAdditionGroups();
        Map<Long, ProductAdditionGroup> existingMap = existing.stream()
                .collect(Collectors.toMap(
                        pag -> pag.getAdditionGroup().getId(),
                        Function.identity()
                ));

        List<ProductAdditionGroup> newList = new ArrayList<>();

        for (AdditionGroup group : groups) {
            ProductAdditionGroup pag = existingMap.get(group.getId());

            if (pag != null) {
                newList.add(pag);
            } else {
                ProductAdditionGroup newPag = new ProductAdditionGroup();
                newPag.setProduct(product);
                newPag.setAdditionGroup(group);
                newList.add(newPag);
            }
        }

        existing.clear();
        existing.addAll(newList);
        Product savedProduct = productRepository.save(product);

        ProductOptionInformationDto productOptionDto = mapToProductOptionsDto(product);
        List<AdditionGroupDto> additionGroupDtoList = mapToAdditionGroupDtoList(product);
        UpdateProductResponseDto responseDto = mapToUpdatedProductResponseDto(savedProduct);
        responseDto.setAdditionGroups(additionGroupDtoList);
        responseDto.setOptions(productOptionDto);
        return responseDto;
    }

    @Override
    public List<ProductDto> getProductsForVendorShopByVendorId(Long vendorId) {
        if (vendorId == null)
            throw new ResourceNotFoundException("Vendor ID is required");
        
        List<Product> products = productRepository.findProductsByVendorId(vendorId);
        List<ProductDto> productDtoList = new ArrayList<>();

        products.forEach(product -> {
            ProductDto dto = mapToProductDto(product);
            List<AdditionGroupDto> additionGroupDtoList = mapToAdditionGroupDtoList(product);
            dto.setAdditionGroups(additionGroupDtoList);
            productDtoList.add(dto);
        });

        return productDtoList;
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

    private Product createProduct(AddProductRequestDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setIsDeleted(false);
        product.setIsStockTracked(productDto.getIsStockTracked());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

    private AddProductResponseDto mapToAddedProductResponseDto(Product product) {
        AddProductResponseDto addedProductResponseDto = new AddProductResponseDto();
        addedProductResponseDto.setId(product.getId());
        addedProductResponseDto.setName(product.getName());
        addedProductResponseDto.setDescription(product.getDescription());
        addedProductResponseDto.setCategoryId(product.getCategory().getId());
        addedProductResponseDto.setIsStockTracked(product.getIsStockTracked());
        addedProductResponseDto.setVendorId(product.getVendor().getId());
        return addedProductResponseDto;
    }

    private ProductOptionInformationDto mapToProductOptionsDto(Product product) {
        ProductOptionInformationDto informationDto = new ProductOptionInformationDto();
        List<ProductOption> options = product.getProductOptionList();
        List<ProductOptionDto> dtoList = new ArrayList<>();

        if (options.size() == 1 && options.getFirst().getSize().equals(ProductSizes.DEFAULT))
            informationDto.setHasDefaultSize(Boolean.TRUE);
        else
            informationDto.setHasDefaultSize(Boolean.FALSE);

        options.forEach(option -> {
            ProductOptionDto optionDto = new ProductOptionDto(option.getSize(), option.getPrice());
            optionDto.setId(option.getId());
            dtoList.add(optionDto);
        });

        informationDto.setOptionList(dtoList);
        return informationDto;
    }

    private UpdateProductResponseDto mapToUpdatedProductResponseDto(Product product) {
        UpdateProductResponseDto updatedProductResponseDto = new UpdateProductResponseDto();
        updatedProductResponseDto.setId(product.getId());
        updatedProductResponseDto.setName(product.getName());
        updatedProductResponseDto.setDescription(product.getDescription());
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
        productDto.setCategoryId(product.getCategory().getId());
        productDto.setCategoryName(product.getCategory().getName());
        productDto.setIsStockTracked(product.getIsStockTracked());
        productDto.setVendorId(product.getVendor().getId());
        productDto.setImageUrl(product.getImageUrl());

        ProductOptionInformationDto informationDto = new ProductOptionInformationDto();
        var options = product.getProductOptionList();

        if (options.size() == 1 && options.getFirst().getSize().equals(ProductSizes.DEFAULT))
            informationDto.setHasDefaultSize(Boolean.TRUE);
        else
            informationDto.setHasDefaultSize(Boolean.FALSE);

        product.getProductOptionList().forEach(productOption -> {
            ProductOptionDto optionDto = new ProductOptionDto(productOption.getSize(), productOption.getPrice());
            optionDto.setId(productOption.getId());
            informationDto.getOptionList().add(optionDto);
        });
        productDto.setOptions(informationDto);
        return productDto;
    }

    private Category fetchCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return productRepository.findCategoryById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
    }

    private void updateProductFields(Product product, UpdateProductRequestDto dto, Category category) {
        if (dto.getName() != null)  product.setName(dto.getName());

        if (dto.getDescription() != null) product.setDescription(dto.getDescription());

        if (dto.getIsStockTracked() != null) product.setIsStockTracked(dto.getIsStockTracked());

        if (category != null) product.setCategory(category);

        product.setUpdatedAt(LocalDateTime.now());
    }

    private List<AdditionGroupDto> mapToAdditionGroupDtoList(Product product) {
        List<AdditionGroupDto> additionGroupList = new ArrayList<>();
        List<ProductAdditionGroup> pads = product.getProductAdditionGroups();
        pads.forEach(pad -> {
            AdditionGroupDto dto = new AdditionGroupDto();
            dto.setId(pad.getAdditionGroup().getId());
            dto.setName(pad.getAdditionGroup().getName());
            additionGroupList.add(dto);
        });

        return additionGroupList;
    }
}
