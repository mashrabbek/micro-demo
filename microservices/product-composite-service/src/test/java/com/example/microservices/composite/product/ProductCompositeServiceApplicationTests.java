package com.example.microservices.composite.product;

import com.example.api.core.product.Product;
import com.example.api.core.recommendation.Recommendation;
import com.example.api.core.review.Review;
import com.example.api.exceptions.InvalidInputException;
import com.example.api.exceptions.NotFoundException;
import com.example.microservices.composite.product.services.ProductCompositeIntegration;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
class ProductCompositeServiceApplicationTests {

    private static final int PRODUCT_ID_OK = 1;
    private static final int PRODUCT_ID_NOT_FOUND = 2;
    private static final int PRODUCT_ID_INVALID = 3;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductCompositeIntegration compositeIntegration;

    @BeforeEach
    void setUp() {

        when(compositeIntegration.getProduct(PRODUCT_ID_OK))
                .thenReturn(new Product(PRODUCT_ID_OK, "name", 1, "mock-address"));
        when(compositeIntegration.getRecommendations(PRODUCT_ID_OK))
                .thenReturn(singletonList(new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock address")));
        when(compositeIntegration.getReviews(PRODUCT_ID_OK))
                .thenReturn(singletonList(new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address")));

        when(compositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND))
                .thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

        when(compositeIntegration.getProduct(PRODUCT_ID_INVALID))
                .thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
    }

    @Test
    void contextLoads() {}
    @Test
    void getProductById() throws Exception {
        mockMvc.perform(get("/product-composite/" + PRODUCT_ID_OK)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.productId").value(PRODUCT_ID_OK))
                .andExpect(jsonPath("$.recommendations.length()").value(1))
                .andExpect(jsonPath("$.reviews.length()").value(1));
    }

    @Test
    void getProductNotFound() throws Exception {
        mockMvc.perform(get("/product-composite/" + PRODUCT_ID_NOT_FOUND)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.path").value("/product-composite/" + PRODUCT_ID_NOT_FOUND))
                .andExpect(jsonPath("$.message").value("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));
    }

    @Test
    void getProductInvalidInput() throws Exception {
        mockMvc.perform(get("/product-composite/" + PRODUCT_ID_INVALID)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.path").value("/product-composite/" + PRODUCT_ID_INVALID))
                .andExpect(jsonPath("$.message").value("INVALID: " + PRODUCT_ID_INVALID));
    }
}
