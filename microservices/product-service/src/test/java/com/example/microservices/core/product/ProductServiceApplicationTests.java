package com.example.microservices.core.product;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getProductById() throws Exception {

        int productId = 1;

        mockMvc.perform(MockMvcRequestBuilders.get("/product/{productId}", productId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(productId));
    }

    @Test
    void getProductInvalidParameterString() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/product/no-integer")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path").value("/product/no-integer"));
    }

    @Test
    void getProductNotFound() throws Exception {
        int productIdNotFound = 13;

        mockMvc.perform(MockMvcRequestBuilders.get("/product/{productId}", productIdNotFound)
                        .accept(MediaType.APPLICATION_JSON))
               .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path").value("/product/" + productIdNotFound))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("No product found for productId: " + productIdNotFound));
    }

    @Test
    void getProductInvalidParameterNegativeValue() throws Exception {
        int productIdInvalid = -1;

        mockMvc.perform(MockMvcRequestBuilders.get("/product/{productId}", productIdInvalid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path").value("/product/" + productIdInvalid))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid productId: " + productIdInvalid));
    }
}
