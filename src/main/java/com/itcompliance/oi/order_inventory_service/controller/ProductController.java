package com.itcompliance.oi.order_inventory_service.controller;

import com.itcompliance.oi.order_inventory_service.dto.ProductDTO;
import com.itcompliance.oi.order_inventory_service.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }


    @PostMapping(path = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createProduct(@RequestBody ProductDTO input) {
        try {
            return ResponseEntity.ok(productService.createProduct(input));
        } catch (Exception e) {
            log.error("Error creating product for input {} ",  input, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(path = "/products/{sku}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDTO> getProduct(@PathVariable String sku) {
        return productService.fetchProductBySku(sku)
                .map((product)->ResponseEntity.ok(ProductDTO.mapEntityToDto(product)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }
}
