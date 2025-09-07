package com.itcompliance.oi.order_inventory_service.controller;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private void createProduct() throws Exception {
        String productJson = """
                {
                  "sku": "SKU123",
                  "name": "Integration Product",
                  "price": 49.99,
                  "availableQuantity": 10
                }
                """;

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateOrderAndFetchIt() throws Exception {
        createProduct();
        String orderJson = """
                {
                  "items": [
                    {"sku": "SKU123", "quantity": 2}
                  ]
                }
                """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].sku").value("SKU123"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void testCreateOrder_ShouldFail_WhenProductStockInsufficient() throws Exception {
        String productJson = """
                {
                  "sku": "SKU456",
                  "name": "Low Stock Product",
                  "price": 19.99,
                  "availableQuantity": 1
                }
                """;
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isOk());

        String orderJson = """
                {
                  "items": [
                    {"sku": "SKU456", "quantity": 5}
                  ]
                }
                """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isConflict());
    }
}

