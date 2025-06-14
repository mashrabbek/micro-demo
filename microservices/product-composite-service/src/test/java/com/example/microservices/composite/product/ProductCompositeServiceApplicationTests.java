package com.example.microservices.composite.product;

import com.example.api.composite.product.ProductAggregate;
import com.example.api.composite.product.RecommendationSummary;
import com.example.api.composite.product.ReviewSummary;
import com.example.api.core.product.Product;
import com.example.api.core.recommendation.Recommendation;
import com.example.api.core.review.Review;
import com.example.api.exceptions.InvalidInputException;
import com.example.api.exceptions.NotFoundException;
import com.example.microservices.composite.product.services.ProductCompositeIntegration;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;


@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"eureka.client.enabled=false"})
@AutoConfigureMockMvc
class ProductCompositeServiceApplicationTests {

    private static final int PRODUCT_ID_OK = 1;
    private static final int PRODUCT_ID_NOT_FOUND = 2;
    private static final int PRODUCT_ID_INVALID = 3;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
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
    void contextLoads() {
    }

    @Test
    void createCompositeProduct1() throws Exception {
        ProductAggregate compositeProduct = new ProductAggregate(1, "name", 1, null, null, null);
        postAndVerifyProduct(compositeProduct, status().isOk());
    }

    @Test
    void createCompositeProduct2() throws Exception {
        ProductAggregate compositeProduct = new ProductAggregate(
                1, "name", 1,
                singletonList(new RecommendationSummary(1, "a", 1, "c")),
                singletonList(new ReviewSummary(1, "a", "s", "c")), null
        );
        postAndVerifyProduct(compositeProduct, status().isOk());
    }

    @Test
    void deleteCompositeProduct() throws Exception {
        ProductAggregate compositeProduct = new ProductAggregate(
                1, "name", 1,
                singletonList(new RecommendationSummary(1, "a", 1, "c")),
                singletonList(new ReviewSummary(1, "a", "s", "c")), null
        );

        postAndVerifyProduct(compositeProduct, status().isOk());
        deleteAndVerifyProduct(compositeProduct.getProductId(), status().isOk());
        deleteAndVerifyProduct(compositeProduct.getProductId(), status().isOk());
    }

    @Test
    void getProductById() throws Exception {
        mockMvc.perform(get("/product-composite/" + PRODUCT_ID_OK)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(PRODUCT_ID_OK))
                .andExpect(jsonPath("$.recommendations.length()").value(1))
                .andExpect(jsonPath("$.reviews.length()").value(1));
    }

    @Test
    void getProductNotFound() throws Exception {
        mockMvc.perform(get("/product-composite/" + PRODUCT_ID_NOT_FOUND)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.path").value("/product-composite/" + PRODUCT_ID_NOT_FOUND))
                .andExpect(jsonPath("$.message").value("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));
    }

    @Test
    void getProductInvalidInput() throws Exception {
        mockMvc.perform(get("/product-composite/" + PRODUCT_ID_INVALID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.path").value("/product-composite/" + PRODUCT_ID_INVALID))
                .andExpect(jsonPath("$.message").value("INVALID: " + PRODUCT_ID_INVALID));
    }

    private void postAndVerifyProduct(ProductAggregate compositeProduct, ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(post("/product-composite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compositeProduct)))
                .andExpect(expectedStatus);
    }

    private void deleteAndVerifyProduct(int productId, ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(delete("/product-composite/" + productId))
                .andExpect(expectedStatus);
    }
}