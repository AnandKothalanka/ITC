package com.itcompliance.oi.order_inventory_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class OrderItemDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private String sku;
    private int quantity;
}
