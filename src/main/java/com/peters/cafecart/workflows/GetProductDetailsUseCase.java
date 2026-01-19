package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionDto;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.entity.ProductOption;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.features.ShopProductManagement.dto.ShopProductDto;
import com.peters.cafecart.features.ShopProductManagement.service.ShopProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GetProductDetailsUseCase {
    @Autowired ShopProductServiceImpl shopProductService;
    @Autowired ProductServiceImpl productService;

    public ShopProductDto execute(Long productId, Long vendorShopId) {
        ShopProductDto shopProduct =  shopProductService.findByProductAndVendorShop(productId, vendorShopId);
        Product product = productService.getProductById(shopProduct.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product Not Found"));
        List<ProductOption> options =  product.getProductOptionList();
        options.forEach(option-> {
            ProductOptionDto optionDto = new ProductOptionDto(option.getSize(), option.getPrice());
            optionDto.setId(option.getId());
            shopProduct.getOptions().add(optionDto);
        });

        return shopProduct;
    }
}
