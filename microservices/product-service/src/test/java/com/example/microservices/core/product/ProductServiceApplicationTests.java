package com.example.microservices.core.product;

import static com.mongodb.assertions.Assertions.assertTrue;
import static com.mongodb.assertions.Assertions.assertFalse;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.is;

import com.example.api.core.product.Product;
import com.example.microservices.core.product.persistence.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
class ProductServiceApplicationTests extends MongoDbTestBase {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductRepository repository;
    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setupDb() {
        repository.deleteAll();
    }

    @Test
    void getProductById() throws Exception {
        int productId = 1;

        postAndVerifyProduct(productId, status().isOk());

        assertTrue(repository.findByProductId(productId).isPresent());

        getAndVerifyProduct(productId, status().isOk())
                .andExpect(jsonPath("$.productId", is(productId)));
    }

    @Test
    void duplicateError() throws Exception {
        int productId = 1;

        postAndVerifyProduct(productId, status().isOk());
        assertTrue(repository.findByProductId(productId).isPresent());

        postAndVerifyProduct(productId, status().isUnprocessableEntity())
                .andExpect(jsonPath("$.path", is("/product")))
                .andExpect(jsonPath("$.message", is("Duplicate key, Product Id: " + productId)));
    }

    @Test
    void deleteProduct() throws Exception {
        int productId = 1;

        postAndVerifyProduct(productId, status().isOk());
        assertTrue(repository.findByProductId(productId).isPresent());

        deleteAndVerifyProduct(productId, status().isOk());
        assertFalse(repository.findByProductId(productId).isPresent());

        deleteAndVerifyProduct(productId, status().isOk());
    }

    @Test
    void getProductInvalidParameterString() throws Exception {
        String invalidId = "no-integer";

        mockMvc.perform(get("/product/{id}", invalidId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path", is("/product/" + invalidId)));
        //.andExpect(jsonPath("$.message", is("Type mismatch.")));
    }


    @Test
    void getProductNotFound() throws Exception {
        int productIdNotFound = 13;

        getAndVerifyProduct(productIdNotFound, status().isNotFound())
                .andExpect(jsonPath("$.path", is("/product/" + productIdNotFound)))
                .andExpect(jsonPath("$.message", is("No product found for productId: " + productIdNotFound)));
    }

    @Test
    void getProductInvalidParameterNegativeValue() throws Exception {
        int productIdInvalid = -1;

        getAndVerifyProduct(productIdInvalid, status().isUnprocessableEntity())
                .andExpect(jsonPath("$.path", is("/product/" + productIdInvalid)))
                .andExpect(jsonPath("$.message", is("Invalid productId: " + productIdInvalid)));
    }

    // Utility Methods

    private ResultActions getAndVerifyProduct(int productId, ResultMatcher expectedStatus) throws Exception {
        return getAndVerifyProduct("/" + productId, expectedStatus);
    }

    private ResultActions getAndVerifyProduct(String productIdPath, ResultMatcher expectedStatus) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get("/product" + productIdPath)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions postAndVerifyProduct(int productId, ResultMatcher expectedStatus) throws Exception {
        Product product = new Product(productId, "Name " + productId, productId, "SA");
        return mockMvc.perform(MockMvcRequestBuilders.post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private void deleteAndVerifyProduct(int productId, ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/product/{id}", productId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus);
    }
}
