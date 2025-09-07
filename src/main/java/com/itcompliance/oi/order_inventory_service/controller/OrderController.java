package com.itcompliance.oi.order_inventory_service.controller;

import com.itcompliance.oi.order_inventory_service.dto.OrderDTO;
import com.itcompliance.oi.order_inventory_service.dto.ProductDTO;
import com.itcompliance.oi.order_inventory_service.exception.InsufficientStockException;
import com.itcompliance.oi.order_inventory_service.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(path = "/orders")
    public ResponseEntity<Object> createOrder(@RequestBody OrderDTO orderDTO) {
        try {
            orderService.createOrder(orderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Error creating order {} ", orderDTO, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(path = "/orders/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDTO> getOrder(@PathVariable long id) {
        return orderService.fetchOrder(id)
                .map((order)->ResponseEntity.ok(OrderDTO.mapEntityToDto(order)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }
}
