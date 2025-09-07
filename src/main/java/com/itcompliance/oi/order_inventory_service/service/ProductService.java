package com.itcompliance.oi.order_inventory_service.service;

import com.itcompliance.oi.order_inventory_service.dto.ProductDTO;
import com.itcompliance.oi.order_inventory_service.persistence.Product;
import com.itcompliance.oi.order_inventory_service.persistence.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public long createProduct(ProductDTO productDTO) {

        Product product = Product.builder()
                .sku(productDTO.getSku())
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .availableQuantity(productDTO.getAvailableQuantity())
                .build();
        Product save = productRepository.save(product);

        return save.getId();
    }

    public Optional<Product> fetchProductBySku(String sku) {
        return productRepository.findBySku(sku);

    }
}
