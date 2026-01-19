package com.peters.cafecart.features.ProductsManagement.service;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionDto;
import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionInformationDto;
import com.peters.cafecart.features.ProductsManagement.entity.ProductOption;
import com.peters.cafecart.features.ProductsManagement.repository.ProductOptionsRepository;
import com.peters.cafecart.shared.enums.ProductSizes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductOptionsServiceImpl implements  ProductOptionsService{
    @Autowired ProductOptionsRepository productOptionsRepository;


    @Override
    public List<ProductOption> createProductOptionsForProduct(ProductOptionInformationDto optionsInfo) {
        List<ProductOption> options = new ArrayList<>();
        List<ProductOptionDto> optionsDto = optionsInfo.getOptionList();
        if (optionsDto.isEmpty()) return null;
        Boolean hasDefaultSize = optionsInfo.getHasDefaultSize();
        if (hasDefaultSize) {
            if (optionsDto.size() > 1) throw new ValidationException("Invalid Request Inputs");
            var option = optionsDto.getFirst();
            ProductOption productOption = new ProductOption();
            productOption.setSize(ProductSizes.DEFAULT);
            productOption.setPrice(option.getPrice());
            options.add(productOption);
        } else {
            optionsDto.forEach(option -> {
                if (option.getSize().equals(ProductSizes.DEFAULT)) return;
                ProductOption productOption = new ProductOption();
                productOption.setSize(option.getSize());
                productOption.setPrice(option.getPrice());
                options.add(productOption);
            });
        }

        return options;
    }

    @Override
    public List<ProductOption> updateProductOption(ProductOptionInformationDto informationDto) {
        List<ProductOptionDto> optionList = informationDto.getOptionList();
        if (optionList == null || optionList.isEmpty()) return Collections.emptyList();

        Boolean hasDefaultSize = informationDto.getHasDefaultSize();
        if (hasDefaultSize.equals(Boolean.TRUE)
                && !(optionList.size() > 1)
                && !optionList.getFirst().getSize().equals(ProductSizes.DEFAULT)
        ) throw new ValidationException("Can't Update Product Size");

        // Separate into updates and deletes
        List<Long> idsToUpdate = optionList.stream()
                .map(ProductOptionDto::getId)
                .toList();

        // Fetch and update existing options
        if (idsToUpdate.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, ProductOption> existingOptionsMap =
                productOptionsRepository.findAllByIdIn(idsToUpdate).stream()
                        .collect(Collectors.toMap(ProductOption::getId, po -> po));

        for (ProductOptionDto dto : optionList) {
            if (dto.getId() != null && !Boolean.TRUE.equals(dto.getIsDeleted())) {
                ProductOption option = existingOptionsMap.get(dto.getId());
                if (option != null) {
                    updateProductOption(option, dto);
                }
            }
        }

        // Return only the updated options (excluding deleted ones)
        return new ArrayList<>(existingOptionsMap.values());
    }

    @Override
    public void deleteProductOption(ProductOptionDto options) {
        ProductOption productOption = productOptionsRepository.findById(options.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product Option Not Found"));
        productOptionsRepository.delete(productOption);
    }

    @Override
    public Optional<ProductOption> getProductOption(Long id) {
        return productOptionsRepository.findById(id);
    }



    private void updateProductOption(ProductOption option, ProductOptionDto dto) {
        if (dto.getSize() != null) {
            option.setSize(dto.getSize());
        }
        if (dto.getPrice() != null) {
            option.setPrice(dto.getPrice());
        }
    }
}
