package com.itcompliance.oi.order_inventory_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itcompliance.oi.order_inventory_service.persistence.Product;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private String sku;

    private String name;
    private double price;
    private int availableQuantity;


    public static ProductDTO mapEntityToDto(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .price(product.getPrice())
                .availableQuantity(product.getAvailableQuantity())
                .build();

    }

}
