package com.itcompliance.oi.order_inventory_service.service;

import com.itcompliance.oi.order_inventory_service.dto.ProductDTO;
import com.itcompliance.oi.order_inventory_service.persistence.Product;
import com.itcompliance.oi.order_inventory_service.persistence.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductDTO productDTO;
    private Product savedProduct;

    @BeforeEach
    void setUp() {
        productDTO = ProductDTO.builder()
                .sku("SKU123")
                .name("Test Product")
                .price(99.99)
                .availableQuantity(10)
                .build();

        savedProduct = Product.builder()
                .id(1L)
                .sku("SKU123")
                .name("Test Product")
                .price(99.99)
                .availableQuantity(10)
                .build();
    }

    @Test
    void testCreateProduct_ShouldSaveAndReturnId() {
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        long productId = productService.createProduct(productDTO);

        assertEquals(1L, productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testFetchProductBySku_ShouldReturnProduct_WhenFound() {
        when(productRepository.findBySku("SKU123")).thenReturn(Optional.of(savedProduct));

        Optional<Product> result = productService.fetchProductBySku("SKU123");

        assertTrue(result.isPresent());
        assertEquals("SKU123", result.get().getSku());
        assertEquals("Test Product", result.get().getName());
        verify(productRepository, times(1)).findBySku("SKU123");
    }

    @Test
    void testFetchProductBySku_ShouldReturnEmpty_WhenNotFound() {
        when(productRepository.findBySku("INVALID")).thenReturn(Optional.empty());

        Optional<Product> result = productService.fetchProductBySku("INVALID");

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findBySku("INVALID");
    }
}
