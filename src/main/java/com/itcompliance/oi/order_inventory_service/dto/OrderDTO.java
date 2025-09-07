package com.itcompliance.oi.order_inventory_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itcompliance.oi.order_inventory_service.persistence.Order;
import com.itcompliance.oi.order_inventory_service.persistence.OrderItem;
import com.itcompliance.oi.order_inventory_service.persistence.OrderStatus;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class OrderDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    private OrderStatus status;
    private String customerEmail;
    private List<OrderItemDTO> items;

    public static OrderDTO mapEntityToDto(Order order) {

        List<OrderItemDTO> orderItemDTOList = order.getOrderItems().stream()
                .map(orderItem -> OrderItemDTO.builder()
                        .id(orderItem.getId())
                        .quantity(orderItem.getQuantity())
                        .sku(orderItem.getSku())
                        .build()).collect(Collectors.toList());

        return OrderDTO.builder()
                .customerEmail(order.getCustomerEmail())
                .status(order.getStatus())
                .items(orderItemDTOList)
                .build();

    }
}
