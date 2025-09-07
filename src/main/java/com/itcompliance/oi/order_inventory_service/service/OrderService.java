package com.itcompliance.oi.order_inventory_service.service;

import com.itcompliance.oi.order_inventory_service.dto.OrderDTO;
import com.itcompliance.oi.order_inventory_service.dto.OrderItemDTO;
import com.itcompliance.oi.order_inventory_service.exception.InsufficientStockException;
import com.itcompliance.oi.order_inventory_service.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final FulfillmentService fulfillmentService;

    public OrderService(ProductRepository productRepository, OrderRepository orderRepository, FulfillmentService fulfillmentService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.fulfillmentService = fulfillmentService;
    }


    public void createOrder(OrderDTO orderDTO) {
        List<OrderItem> orderItems = new ArrayList<>();
        Order order = Order.builder()
                .customerEmail(orderDTO.getCustomerEmail())
                .status(OrderStatus.NEW)
                .orderItems(orderItems)
                .build();

        for (OrderItemDTO orderItemDTO : orderDTO.getItems()) {
            Product product = productRepository.findBySku(orderItemDTO.getSku()).orElseThrow(() -> new IllegalArgumentException("Invalid sku " + orderItemDTO.getSku()));
            if (product.getAvailableQuantity() < orderItemDTO.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for sku " + orderItemDTO.getSku());
            }

            product.setAvailableQuantity(product.getAvailableQuantity() - orderItemDTO.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .sku(orderItemDTO.getSku())
                    .order(order)
                    .quantity(orderItemDTO.getQuantity())
                    .build();

            order.getOrderItems().add(orderItem);
        }

        order.setStatus(OrderStatus.RESERVED);
        orderRepository.save(order);

        fulfillmentService.scheduleFulfilment(order);

    }

    public Optional<Order> fetchOrder(long id) {
        return orderRepository.findById(id);
    }
}
