package com.example.springcloud.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class HealthCheckConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckConfiguration.class);

    private final RestTemplate restTemplate;

    public HealthCheckConfiguration(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Bean
    public HealthContributor healthcheckMicroservices() {
        Map<String, HealthIndicator> registry = new LinkedHashMap<>();

        registry.put("product", () -> getHealth("http://product"));
        registry.put("recommendation", () -> getHealth("http://recommendation"));
        registry.put("review", () -> getHealth("http://review"));
        registry.put("product-composite", () -> getHealth("http://product-composite"));

        return CompositeHealthContributor.fromMap(registry);
    }

    private Health getHealth(String baseUrl) {
        String url = baseUrl + "/actuator/health";
        LOG.debug("Calling Health API at URL: {}", url);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return Health.up().build();
            } else {
                return Health.down().withDetail("status", response.getStatusCode()).build();
            }
        } catch (Exception ex) {
            LOG.warn("Health check failed for {}: {}", baseUrl, ex.getMessage());
            return Health.down(ex).build();
        }
    }


}
