package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.UnauthorizedAccessException;
import com.peters.cafecart.features.AdditionsManagement.entity.AdditionGroup;
import com.peters.cafecart.features.AdditionsManagement.entity.ProductAdditionGroup;
import com.peters.cafecart.features.AdditionsManagement.repository.AdditionGroupRepository;
import com.peters.cafecart.features.AdditionsManagement.repository.ProductAdditionGroupRepository;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachAdditionGroupToProductUseCase {

    private final ProductRepository productRepository;
    private final AdditionGroupRepository additionGroupRepository;
    private final ProductAdditionGroupRepository productAdditionGroupRepository;

    @Transactional
    public void execute(Long productId, Long groupId, Long vendorId) {
        log.info("Executing AttachAdditionGroupToProductUseCase: product {}, group {}, vendor {}", productId, groupId, vendorId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getVendor().getId().equals(vendorId)) {
            log.warn("Vendor {} does not own product {}", vendorId, productId);
            throw new UnauthorizedAccessException("You do not own this product");
        }

        AdditionGroup group = additionGroupRepository.findByIdAndVendor_Id(groupId, vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Addition group not found"));

        ProductAdditionGroup pag = new ProductAdditionGroup();
        pag.setProduct(product);
        pag.setAdditionGroup(group);
        productAdditionGroupRepository.save(pag);
        
        log.info("Successfully attached group {} to product {}", groupId, productId);
    }
}
