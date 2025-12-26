package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.ProductsManagement.dto.response.ProductDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.VendorProductToShopResponseDto;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.ShopManagement.repository.VendorShopsRepository;
import com.peters.cafecart.features.ShopProductManagement.service.ShopProductServiceImpl;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorShopProductsUseCaseTest {

    @InjectMocks
    private VendorShopProductsUseCase useCase;

    @Mock private ProductServiceImpl productService;
    @Mock private ShopProductServiceImpl shopProductsService;
    @Mock private VendorShopsRepository vendorShopsRepository;

    private VendorShop vendorShop;
    private Vendor vendor;

    private final Long SHOP_ID = 1L;
    private final Long VENDOR_ID = 10L;

    @BeforeEach
    void setUp() {
        vendor = new Vendor();
        vendor.setId(VENDOR_ID);
        vendor.setIsActive(true);

        vendorShop = new VendorShop();
        vendorShop.setId(SHOP_ID);
        vendorShop.setVendor(vendor);
    }

    /* ---------- validations ---------- */

    @Test
    void shouldThrowWhenVendorShopIdIsNull() {
        assertThrows(ValidationException.class,
                () -> useCase.execute(null));
    }

    @Test
    void shouldThrowWhenVendorShopNotFound() {
        when(vendorShopsRepository.findById(SHOP_ID))
                .thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> useCase.execute(SHOP_ID));
    }

    @Test
    void shouldThrowWhenVendorIsInactive() {
        vendor.setIsActive(false);

        when(vendorShopsRepository.findById(SHOP_ID))
                .thenReturn(Optional.of(vendorShop));

        assertThrows(ValidationException.class,
                () -> useCase.execute(SHOP_ID));
    }

    /* ---------- happy path ---------- */

    @Test
    void shouldReturnProductsWithOwnershipFlag() {
        when(vendorShopsRepository.findById(SHOP_ID))
                .thenReturn(Optional.of(vendorShop));

        ProductDto owned = product(1L, "Coffee");
        ProductDto notOwned = product(2L, "Tea");

        when(productService.getProductsForVendorShopByVendorId(VENDOR_ID))
                .thenReturn(List.of(owned, notOwned));

        when(shopProductsService.findAllProductIdsForVendorShop(SHOP_ID))
                .thenReturn(Set.of(1L));

        List<VendorProductToShopResponseDto> result =
                useCase.execute(SHOP_ID);

        assertEquals(2, result.size());

        assertTrue(result.get(0).getIsOwnedByShop());
        assertFalse(result.get(1).getIsOwnedByShop());
    }

    /* ---------- helpers ---------- */

    private ProductDto product(Long id, String name) {
        ProductDto p = new ProductDto();
        p.setId(id);
        p.setName(name);
        p.setPrice(BigDecimal.TEN);
        p.setCategoryId(1L);
        p.setCategoryName("Drinks");
        p.setIsStockTracked(false);
        return p;
    }
}

