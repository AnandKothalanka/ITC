package com.itcompliance.oi.order_inventory_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateProduct_ShouldReturnId() throws Exception {
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
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    void testGetProduct_ShouldReturnProduct_WhenExists() throws Exception {
        String productJson = """
                {
                  "sku": "SKU999",
                  "name": "Test Product",
                  "price": 19.99,
                  "availableQuantity": 5
                }
                """;

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/products/SKU999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sku").value("SKU999"))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(19.99))
                .andExpect(jsonPath("$.availableQuantity").value(5));
    }

    @Test
    void testGetProduct_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        mockMvc.perform(get("/products/DOES_NOT_EXIST"))
                .andExpect(status().isNotFound());
    }
}
