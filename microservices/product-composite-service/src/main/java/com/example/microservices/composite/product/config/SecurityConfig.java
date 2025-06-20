package com.example.microservices.composite.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("!test")
public class SecurityConfig {

    @Bean
    public SecurityFilterChain protectedApiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/product-composite/**", "/openapi/**", "/webjars/**", "/actuator/**") // <-- scope it
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/openapi/**", "/webjars/**", "/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/product-composite/**").hasAuthority("SCOPE_product:write")
                        .requestMatchers(HttpMethod.DELETE, "/product-composite/**").hasAuthority("SCOPE_product:write")
                        .requestMatchers(HttpMethod.GET, "/product-composite/**").hasAuthority("SCOPE_product:read")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                );
        return http.build();
    }
    
}

