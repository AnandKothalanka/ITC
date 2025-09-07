package com.itcompliance.oi.order_inventory_service.service;

import com.itcompliance.oi.order_inventory_service.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FulfillmentServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private FulfillmentService fulfillmentService;

    private Order order;
    private OrderItem item;
    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .sku("SKU123")
                .availableQuantity(10)
                .build();

        item = OrderItem.builder()
                .sku("SKU123")
                .quantity(2)
                .build();

        order = Order.builder()
                .id(1L)
                .orderItems(List.of(item))
                .build();
    }

    @Test
    void testFulfilOrder_ShouldSetStatusToFulfilledAndSave() {
        fulfillmentService.fulfilOrder(order);

        assertEquals(OrderStatus.FULFILLED, order.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testScheduleFulfilment_ShouldEventuallyCallFulfilOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        fulfillmentService.scheduleFulfilment(order);

        assertEquals(OrderStatus.FULFILLED, order.getStatus());
        verify(orderRepository, atLeastOnce()).save(order);
    }

    @Test
    void testMarkOrderFailed_ShouldUpdateStatusAndRestoreStock() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findBySku("SKU123")).thenReturn(Optional.of(product));

        fulfillmentService.markOrderFailed(1L);

        assertEquals(OrderStatus.FAILED, order.getStatus());
        assertEquals(12, product.getAvailableQuantity());
        verify(orderRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findBySku("SKU123");
    }

    @Test
    void testMarkOrderFailed_ShouldThrowException_WhenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> fulfillmentService.markOrderFailed(99L));
    }

    @Test
    void testMarkOrderFailed_ShouldThrowException_WhenProductNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findBySku("SKU123")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> fulfillmentService.markOrderFailed(1L));
    }

}