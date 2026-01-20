package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.AdditionsManagement.entity.AdditionGroup;
import com.peters.cafecart.features.AdditionsManagement.service.AdditionGroupServiceImpl;
import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionDto;
import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionInformationDto;
import com.peters.cafecart.features.ProductsManagement.dto.request.UpdateProductRequestDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.UpdateProductResponseDto;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.entity.ProductOption;
import com.peters.cafecart.features.ProductsManagement.service.ProductOptionsServiceImpl;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.features.VendorManagement.service.VendorServiceImpl;
import com.peters.cafecart.shared.enums.ProductSizes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateProductUseCase {
    @Autowired VendorServiceImpl vendorService;
    @Autowired ProductServiceImpl productService;
    @Autowired ProductOptionsServiceImpl productOptionsService;
    @Autowired AdditionGroupServiceImpl additionGroupService;

    public UpdateProductResponseDto execute(UpdateProductRequestDto updateProductDto, Long vendorId) {
        validateUpdateRequest(updateProductDto);
        Product product = productService.getProductById(updateProductDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product Not Found"));

        ProductOptionInformationDto optionInformation = updateProductDto.getOptions();

        Boolean hasDefaultSize = optionInformation.getHasDefaultSize();
        List<ProductOptionDto> newOptionsList = new ArrayList<>();
        List<ProductOptionDto> updatedOptionsList = new ArrayList<>();

        optionInformation.getOptionList().forEach(dto -> {
            if (dto.getSize() == null ||
                    (hasDefaultSize && !dto.getSize().equals(ProductSizes.DEFAULT)) ||
                    (!hasDefaultSize && dto.getSize().equals(ProductSizes.DEFAULT))
            ) throw new ValidationException("Size cannot be null or empty for option with ID: " + dto.getId());

            if (dto.getId() != null && !Boolean.TRUE.equals(dto.getIsDeleted())) {
                updatedOptionsList.add(dto);
            } else if (dto.getId() == null && !Boolean.TRUE.equals(dto.getIsDeleted())) {
                newOptionsList.add(dto);
            }});

        ProductOptionInformationDto reusableInformation = new ProductOptionInformationDto();
        reusableInformation.setHasDefaultSize(hasDefaultSize);
        if (!updatedOptionsList.isEmpty()) {
            reusableInformation.setOptionList(updatedOptionsList);
            List<ProductOption> updatedOptions = productOptionsService.updateProductOption(reusableInformation);
            if (updatedOptions != null)
                product.updateProductOptions(updatedOptions);
        }

        if (!newOptionsList.isEmpty()) {
            reusableInformation.setOptionList(newOptionsList);
            List<ProductOption> newlyAddedOptions = productOptionsService.createProductOptionsForProduct(reusableInformation);
            if (newlyAddedOptions != null)
                product.addProductOptions(newlyAddedOptions);
        }

        List<AdditionGroup> additionGroups = additionGroupService.getAdditionGroupsByIds(
                vendorId,
                updateProductDto.getAdditionGroupIds());
        if (additionGroups.size() != updateProductDto.getAdditionGroupIds().size())
            throw new ValidationException("Invalid Addition Group");
        return productService.updateProduct(updateProductDto, product, additionGroups);
    }

    private void validateUpdateRequest(UpdateProductRequestDto dto) {
        if (dto.getId() == null ) {
            throw new ResourceNotFoundException("Product ID and Vendor ID are required");
        }

        if (dto.getAdditionGroupIds() == null || dto.getOptions() == null)
            throw new ValidationException("Invalid Additions or Options");
    }
}
