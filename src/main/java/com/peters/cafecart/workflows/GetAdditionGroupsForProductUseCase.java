package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.UnauthorizedAccessException;
import com.peters.cafecart.features.AdditionsManagement.dto.AdditionGroupDto;
import com.peters.cafecart.features.AdditionsManagement.entity.ProductAdditionGroup;
import com.peters.cafecart.features.AdditionsManagement.mapper.AdditionMapper;
import com.peters.cafecart.features.AdditionsManagement.repository.ProductAdditionGroupRepository;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetAdditionGroupsForProductUseCase {

    private final ProductRepository productRepository;
    private final ProductAdditionGroupRepository productAdditionGroupRepository;
    private final AdditionMapper mapper;

    @Transactional(readOnly = true)
    public List<AdditionGroupDto> execute(Long productId, Long vendorId) {
        log.info("Executing GetAdditionGroupsForProductUseCase: product {}, vendor {}", productId, vendorId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getVendor().getId().equals(vendorId)) {
            log.warn("Vendor {} does not own product {}", vendorId, productId);
            throw new UnauthorizedAccessException("You do not own this product");
        }

        List<com.peters.cafecart.features.AdditionsManagement.entity.AdditionGroup> groups = productAdditionGroupRepository.findByProduct_Id(productId)
                .stream()
                .map(ProductAdditionGroup::getAdditionGroup)
                .toList();

        return mapper.toGroupDtoList(groups);
    }
}
