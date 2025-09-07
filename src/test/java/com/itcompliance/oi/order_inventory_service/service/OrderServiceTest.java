package com.itcompliance.oi.order_inventory_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.itcompliance.oi.order_inventory_service.dto.OrderDTO;
import com.itcompliance.oi.order_inventory_service.dto.OrderItemDTO;
import com.itcompliance.oi.order_inventory_service.exception.InsufficientStockException;
import com.itcompliance.oi.order_inventory_service.persistence.Order;
import com.itcompliance.oi.order_inventory_service.persistence.OrderRepository;
import com.itcompliance.oi.order_inventory_service.persistence.Product;
import com.itcompliance.oi.order_inventory_service.persistence.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private FulfillmentService fulfillmentService;

    @InjectMocks
    private OrderService orderService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .sku("SKU1")
                .availableQuantity(10)
                .build();

        product2 = Product.builder()
                .sku("SKU2")
                .availableQuantity(5)
                .build();

        when(productRepository.findBySku(anyString())).thenAnswer(invocation -> {
            String sku = invocation.getArgument(0);
            if ("SKU1".equals(sku)) return Optional.of(product1);
            if ("SKU2".equals(sku)) return Optional.of(product2);
            return Optional.empty();
        });

    }

    @Test
    void createOrder_decrementsStockCorrectly() {

        OrderItemDTO item1 = OrderItemDTO.builder().sku("SKU1").quantity(2).build();
        OrderItemDTO item2 = OrderItemDTO.builder().sku("SKU2").quantity(3).build();
        OrderDTO orderDTO = OrderDTO.builder()
                .customerEmail("customer@example.com")
                .items(List.of(item1, item2))
                .build();

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        orderService.createOrder(orderDTO);

        assertEquals(8, product1.getAvailableQuantity());
        assertEquals(2, product2.getAvailableQuantity());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(fulfillmentService, times(1)).scheduleFulfilment(any(Order.class));
    }

    @Test
    void createOrder_insufficientStock_throwsException() {
        OrderItemDTO item1 = OrderItemDTO.builder().sku("SKU1").quantity(20).build();
        OrderDTO orderDTO = OrderDTO.builder()
                .customerEmail("customer@example.com")
                .items(List.of(item1))
                .build();

        InsufficientStockException exception = assertThrows(InsufficientStockException.class,
                () -> orderService.createOrder(orderDTO));

        assertEquals("Insufficient stock for sku SKU1", exception.getMessage());
        assertEquals(10, product1.getAvailableQuantity());
        verify(orderRepository, never()).save(any(Order.class));
        verify(fulfillmentService, never()).scheduleFulfilment(any(Order.class));
    }

    @Test
    void createOrder_partialStockInsufficient_noPartialUpdate() {
        OrderItemDTO item1 = OrderItemDTO.builder().sku("SKU1").quantity(5).build();
        OrderItemDTO item2 = OrderItemDTO.builder().sku("SKU2").quantity(10).build();
        OrderDTO orderDTO = OrderDTO.builder()
                .customerEmail("customer@example.com")
                .items(List.of(item1, item2))
                .build();

        InsufficientStockException exception = assertThrows(InsufficientStockException.class,
                () -> orderService.createOrder(orderDTO));

        assertEquals("Insufficient stock for sku SKU2", exception.getMessage());

        assertEquals(5, product1.getAvailableQuantity());
        assertEquals(5, product2.getAvailableQuantity());

        verify(orderRepository, never()).save(any(Order.class));
        verify(fulfillmentService, never()).scheduleFulfilment(any(Order.class));
    }
}
