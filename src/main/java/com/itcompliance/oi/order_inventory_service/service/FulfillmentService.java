package com.itcompliance.oi.order_inventory_service.service;

import com.itcompliance.oi.order_inventory_service.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class FulfillmentService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public FulfillmentService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }


    public void fulfilOrder(Order order) {
        order.setStatus(OrderStatus.FULFILLED);
        orderRepository.save(order);
    }

    @Async
    @Transactional
    public void scheduleFulfilment(Order order) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 300));
            //Thread.sleep(60000);
            //order.setStatus(OrderStatus.FAILED);
            fulfilOrder(order);
        } catch (Exception e) {
            log.error("Fulfilling the order failed , marking order as failed and rolling back stock count {}", order.getId());
           markOrderFailed(order.getId());
        }

    }

    //If the transaction fails mark the order as FAILED and restore original product count
    public void markOrderFailed(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(OrderStatus.FAILED);


        for (OrderItem item : order.getOrderItems()) {
            Product product = productRepository.findBySku(item.getSku())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid SKU " + item.getSku()));
            product.setAvailableQuantity(product.getAvailableQuantity() + item.getQuantity());
        }

        log.info("Order marked as failed and stock rolled back {} ", order.getId());
    }
}
