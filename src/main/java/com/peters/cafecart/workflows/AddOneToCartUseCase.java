package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.AdditionsManagement.service.AdditionGroupService;
import com.peters.cafecart.features.CartManagement.dto.request.AddOneToCart;
import com.peters.cafecart.features.CartManagement.entity.CartItem;
import com.peters.cafecart.features.CartManagement.repository.CartItemRepository;
import com.peters.cafecart.features.CartManagement.repository.CartRepository;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.entity.ProductOption;
import com.peters.cafecart.features.ShopProductManagement.projection.ShopProductAvailabilityView;
import com.peters.cafecart.features.ShopProductManagement.service.ShopProductServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AddOneToCartUseCase {

    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private ShopProductServiceImpl shopProductService;
    @Autowired private AdditionGroupService additionGroupService;

    public void execute(Long customerId, AddOneToCart addOneToCartUseCaseDto) {
        if (customerId == null || addOneToCartUseCaseDto.getCartItemId() == null)
            throw new ValidationException("Invalid parameters");

        CartItem cartItem = cartItemRepository.findById(addOneToCartUseCaseDto.getCartItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getCustomer().getId().equals(customerId))
            throw new ValidationException("Cart item does not belong to this customer");

        ProductOption productOption = cartItem.getProductOption();
        Product product = productOption.getProduct();
        Long shopId = cartItem.getCart().getShop().getId();

        ShopProductAvailabilityView shopProduct = shopProductService.getShopProductAvailability(product.getId(), shopId);
        if (!shopProduct.getIsAvailable()) 
            throw new ValidationException("Action Failed: Product is no longer available");

        if (shopProduct.getIsStockTracked() && cartItem.getQuantity() + 1 > shopProduct.getQuantity())
            throw new ValidationException("Action Failed: Not enough stock");

        additionGroupService.validateAdditions(product, cartItem.getAdditionsIds());

        cartItem.setQuantity(cartItem.getQuantity() + 1);
        cartRepository.save(cartItem.getCart());
    }

}
