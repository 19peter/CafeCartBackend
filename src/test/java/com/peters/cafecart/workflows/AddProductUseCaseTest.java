package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.InventoryManagement.service.InventoryServiceImpl;
import com.peters.cafecart.features.ProductsManagement.dto.request.AddProductRequestDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.AddProductResponseDto;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.features.ShopProductManagement.service.ShopProductServiceImpl;
import com.peters.cafecart.features.VendorManagement.dto.VendorDto;
import com.peters.cafecart.features.VendorManagement.service.VendorServiceImpl;
import com.peters.cafecart.shared.dtos.Response.UploadUrlResponse;
import com.peters.cafecart.shared.services.S3.S3SignedUrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddProductUseCaseTest {

    @InjectMocks
    private AddProductUseCase addProductUseCase;

    @Mock private ProductServiceImpl productService;
    @Mock private VendorServiceImpl vendorService;
    @Mock private ShopProductServiceImpl shopProductService;
    @Mock private S3SignedUrlService s3SignedUrlService;
    @Mock private InventoryServiceImpl inventoryService;

    private AddProductRequestDto requestDto;
    private AddProductResponseDto responseDto;

    private final Long VENDOR_ID = 1L;
    private final Long PRODUCT_ID = 100L;

    @BeforeEach
    void setUp() {
        requestDto = new AddProductRequestDto();
        requestDto.setIsAvailable(true);
        requestDto.setIsStockTracked(true);
        requestDto.setImageUrl("image.png");
        requestDto.setContentType("image/png");

        responseDto = new AddProductResponseDto();
        responseDto.setId(PRODUCT_ID);
        responseDto.setIsStockTracked(true);
    }

    /* ---------------- Vendor validation ---------------- */

    @Test
    void shouldThrowWhenVendorNotFound() {
        when(vendorService.getVendorById(VENDOR_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> addProductUseCase.execute(requestDto, VENDOR_ID));
    }

    /* ---------------- Happy path ---------------- */

    @Test
    void shouldAddProductWithInventoryAndImageUpload() {
        mockVendorExists();

        Set<Long> shopIds = Set.of(1L, 2L);

        when(productService.addProduct(requestDto, VENDOR_ID))
                .thenReturn(responseDto);
        when(vendorService.getShopIdsByVendorId(VENDOR_ID))
                .thenReturn(shopIds);

        UploadUrlResponse uploadResponse = new UploadUrlResponse("upload-url","file-url");

        when(s3SignedUrlService.generateUploadUrl(
                VENDOR_ID,
                requestDto.getImageUrl(),
                requestDto.getContentType()))
                .thenReturn(uploadResponse);

        AddProductResponseDto result =
                addProductUseCase.execute(requestDto, VENDOR_ID);

        verify(shopProductService)
                .addAProductToAllShops(PRODUCT_ID, shopIds, true);
        verify(inventoryService)
                .createProductInventoryForAllShops(PRODUCT_ID, shopIds);
        verify(productService)
                .saveProductImage(PRODUCT_ID, "file-url");

        assertEquals("file-url", result.getFileUrl());
        assertEquals("upload-url", result.getUploadUrl());
    }

    /* ---------------- Stock not tracked ---------------- */

    @Test
    void shouldSkipInventoryWhenStockNotTracked() {
        mockVendorExists();

        responseDto.setIsStockTracked(false);

        requestDto.setImageUrl(null);
        requestDto.setContentType(null);

        when(productService.addProduct(requestDto, VENDOR_ID))
                .thenReturn(responseDto);
        when(vendorService.getShopIdsByVendorId(VENDOR_ID))
                .thenReturn(Set.of(1L));

        addProductUseCase.execute(requestDto, VENDOR_ID);

        verify(inventoryService, never())
                .createProductInventoryForAllShops(any(), any());
    }


    /* ---------------- No image upload ---------------- */

    @Test
    void shouldSkipImageUploadWhenImageDataMissing() {
        mockVendorExists();

        requestDto.setImageUrl(null);
        requestDto.setContentType(null);

        when(productService.addProduct(requestDto, VENDOR_ID))
                .thenReturn(responseDto);
        when(vendorService.getShopIdsByVendorId(VENDOR_ID))
                .thenReturn(Set.of(1L));

        addProductUseCase.execute(requestDto, VENDOR_ID);

        verify(s3SignedUrlService, never()).generateUploadUrl(any(), any(), any());
        verify(productService, never()).saveProductImage(any(), any());
    }

    /* ---------------- helpers ---------------- */

    private void mockVendorExists() {
        when(vendorService.getVendorById(VENDOR_ID))
                .thenReturn(Optional.of(new VendorDto()));
    }
}

