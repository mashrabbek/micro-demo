package com.example.microservices.core.recommendation;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
class RecommendationServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRecommendationsByProductId() throws Exception {
        int productId = 1;
        mockMvc.perform(MockMvcRequestBuilders.get("/recommendation?productId=" + productId)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].productId").value(productId));
    }

    @Test
    void getRecommendationsMissingParameter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/recommendation")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.path").value("/recommendation"))
                .andExpect(jsonPath("$.message").value("Required request parameter 'productId' for method parameter type int is not present"));
    }

    @Test
    void getRecommendationsInvalidParameter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/recommendation?productId=no-integer")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.path").value("/recommendation"));
//                .andExpect(jsonPath("$.message").value("Type mismatch."));
    }

    @Test
    void getRecommendationsNotFound() throws Exception {
        int productIdNotFound = 113;
        mockMvc.perform(MockMvcRequestBuilders.get("/recommendation?productId=" + productIdNotFound)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRecommendationsInvalidParameterNegativeValue() throws Exception {
        int productIdInvalid = -1;
        mockMvc.perform(MockMvcRequestBuilders.get("/recommendation?productId=" + productIdInvalid)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.path").value("/recommendation"))
                .andExpect(jsonPath("$.message").value("Invalid productId: " + productIdInvalid));
    }
}
