package com.example.microservices.core.review;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
        "spring.cloud.stream.defaultBinder=rabbit",
        "logging.level.com.example=DEBUG",
        "spring.jpa.hibernate.ddl-auto=update"})
class ReviewServiceApplicationTests extends MySqlTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getReviewsByProductId() throws Exception {
        int productId = 1;

        mockMvc.perform(get("/review")
                        .param("productId", String.valueOf(productId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//                .andExpect(jsonPath("$.length()").value(3))
//                .andExpect(jsonPath("$[0].productId").value(productId));
    }

    @Test
    void getReviewsMissingParameter() throws Exception {
        mockMvc.perform(get("/review")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.path").value("/review"))
                .andExpect(jsonPath("$.message").value("Required request parameter 'productId' for method parameter type int is not present"));
    }

    @Test
    void getReviewsInvalidParameter() throws Exception {
        mockMvc.perform(get("/review")
                        .param("productId", "no-integer")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.path").value("/review"));
//                .andExpect(jsonPath("$.message").value("Type mismatch."));
    }


    @Test
    void getReviewsNotFound() throws Exception {
        int productIdNotFound = 213;

        mockMvc.perform(get("/review")
                        .param("productId", String.valueOf(productIdNotFound))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getReviewsInvalidParameterNegativeValue() throws Exception {
        int productIdInvalid = -1;

        mockMvc.perform(get("/review")
                        .param("productId", String.valueOf(productIdInvalid))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.path").value("/review"))
                .andExpect(jsonPath("$.message").value("Invalid productId: " + productIdInvalid));
    }
}
