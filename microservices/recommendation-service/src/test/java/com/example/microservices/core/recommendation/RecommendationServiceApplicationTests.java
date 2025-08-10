package com.example.microservices.core.recommendation;

import com.example.api.core.recommendation.Recommendation;
import com.example.microservices.core.recommendation.persistence.RecommendationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RecommendationServiceApplicationTests extends MongoDbTestBase {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RecommendationRepository repository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setupDb() {
        repository.deleteAll();
    }

    @Test
    void getRecommendationsByProductId() throws Exception {
        int productId = 1;

        postAndVerifyRecommendation(productId, 1, status().isOk());
        postAndVerifyRecommendation(productId, 2, status().isOk());
        postAndVerifyRecommendation(productId, 3, status().isOk());

        assertEquals(3, repository.findByProductId(productId).size());

        getAndVerifyRecommendationsByProductId(productId, status().isOk())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$[2].productId", is(productId)))
                .andExpect(jsonPath("$[2].recommendationId", is(3)));
    }

    @Test
    void duplicateError() throws Exception {
        int productId = 1;
        int recommendationId = 1;

        postAndVerifyRecommendation(productId, recommendationId, status().isOk())
                .andExpect(jsonPath("$.productId", is(productId)))
                .andExpect(jsonPath("$.recommendationId", is(recommendationId)));

        assertEquals(1, repository.count());

        postAndVerifyRecommendation(productId, recommendationId, status().isUnprocessableEntity())
                .andExpect(jsonPath("$.path", is("/recommendation")))
                .andExpect(jsonPath("$.message", is("Duplicate key, Product Id: 1, Recommendation Id:1")));

        assertEquals(1, repository.count());
    }

    @Test
    void deleteRecommendations() throws Exception {
        int productId = 1;
        int recommendationId = 1;

        postAndVerifyRecommendation(productId, recommendationId, status().isOk());
        assertEquals(1, repository.findByProductId(productId).size());

        deleteAndVerifyRecommendationsByProductId(productId, status().isOk());
        assertEquals(0, repository.findByProductId(productId).size());

        deleteAndVerifyRecommendationsByProductId(productId, status().isOk());
    }

    @Test
    void getRecommendationsMissingParameter() throws Exception {
        mockMvc.perform(get("/recommendation")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path", is("/recommendation")))
                .andExpect(jsonPath("$.message", is("Required request parameter 'productId' for method parameter type int is not present")));
    }

    @Test
    void getRecommendationsInvalidParameter() throws Exception {
        mockMvc.perform(get("/recommendation?productId=no-integer")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path", is("/recommendation")));
        //.andExpect(jsonPath("$.message", is("Type mismatch.")));
    }

    @Test
    void getRecommendationsNotFound() throws Exception {
        getAndVerifyRecommendationsByProductId(113, status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void getRecommendationsInvalidParameterNegativeValue() throws Exception {
        int productIdInvalid = -1;

        getAndVerifyRecommendationsByProductId(productIdInvalid, status().isUnprocessableEntity())
                .andExpect(jsonPath("$.path", is("/recommendation")))
                .andExpect(jsonPath("$.message", is("Invalid productId: " + productIdInvalid)));
    }

    // --- Utility methods below ---

    private ResultActions getAndVerifyRecommendationsByProductId(int productId, org.springframework.test.web.servlet.ResultMatcher expectedStatus) throws Exception {
        return getAndVerifyRecommendationsByProductId("?productId=" + productId, expectedStatus);
    }

    private ResultActions getAndVerifyRecommendationsByProductId(String productIdQuery, org.springframework.test.web.servlet.ResultMatcher expectedStatus) throws Exception {
        return mockMvc.perform(get("/recommendation" + productIdQuery)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions postAndVerifyRecommendation(int productId, int recommendationId, org.springframework.test.web.servlet.ResultMatcher expectedStatus) throws Exception {
        Recommendation recommendation = new Recommendation(productId, recommendationId,
                "Author " + recommendationId, recommendationId,
                "Content " + recommendationId, "SA");

        return mockMvc.perform(post("/recommendation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recommendation))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private void deleteAndVerifyRecommendationsByProductId(int productId, org.springframework.test.web.servlet.ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(delete("/recommendation")
                        .param("productId", String.valueOf(productId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus);
    }
}
