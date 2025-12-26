package com.peters.cafecart.workflows;

import com.peters.cafecart.features.InventoryManagement.service.InventoryServiceImpl;
import com.peters.cafecart.features.ProductsManagement.dto.BaseProductDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.ProductDto;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.features.ShopManagement.dto.AddShopDto;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.ShopManagement.service.VendorShopsServiceImpl;
import com.peters.cafecart.features.ShopProductManagement.service.ShopProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddShopUseCaseTest {
    @Mock VendorShopsServiceImpl vendorShopsService;
    @Mock InventoryServiceImpl inventoryService;
    @Mock ShopProductServiceImpl shopProductService;
    @Mock ProductServiceImpl productService;
    @InjectMocks AddShopUseCase addShopUseCase;

    AddShopDto addShopDto;
    Long vendorId = 1L;
    VendorShop vendorShop;
    ProductDto product1;
    ProductDto product2;

    @BeforeEach
    void setup() {
        addShopDto = mock(AddShopDto.class);
        vendorShop = mock(VendorShop.class);
        product1 = mock(ProductDto.class);
        product2 = mock(ProductDto.class);
        when(product1.getId()).thenReturn(101L);
        when(product2.getId()).thenReturn(102L);
        when(product1.getIsStockTracked()).thenReturn(true);
        when(product2.getIsStockTracked()).thenReturn(false);
    }

    @Test
    void execute_addsShopAndProductsAndInventory() {
        when(vendorShopsService.addShop(addShopDto, vendorId)).thenReturn(vendorShop);
        when(vendorShop.getId()).thenReturn(55L);
        when(productService.getProductsForVendorShopByVendorId(vendorId)).thenReturn(List.of(product1, product2));

        addShopUseCase.execute(addShopDto, vendorId);

        verify(vendorShopsService).addShop(addShopDto, vendorId);
        verify(shopProductService).addAllProductsToAShop(eq(55L), anySet(), eq(false));
        verify(inventoryService).createInventory(eq(55L), eq(101L), eq(0));
        verify(inventoryService, never()).createInventory(eq(55L), eq(102L), anyInt());
    }
}
