package com.example.microservices.composite.product.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
public class SpringDocConfig {

    @Value("${api.common.version:v1}")
    String apiVersion;
    @Value("${api.common.title:Product Composite Service API}")
    String apiTitle;
    @Value("${api.common.description:This is the Product Composite Service API}")
    String apiDescription;
    @Value("${api.common.termsOfService:https://example.com/terms}")
    String apiTermsOfService;
    @Value("${api.common.license:Apache 2.0}")
    String apiLicense;
    @Value("${api.common.licenseUrl:https://www.apache.org/licenses/LICENSE-2.0}")
    String apiLicenseUrl;
    @Value("${api.common.externalDocDesc:Documentation for Product Composite Service API}")
    String apiExternalDocDesc;
    @Value("${api.common.externalDocUrl:https://example.com/docs/product-composite}")
    String apiExternalDocUrl;
    @Value("${api.common.contact.name:Product Composite Service Team}")
    String apiContactName;
    @Value("${api.common.contact.url:https://example.com/contact}")
    String apiContactUrl;
    @Value("${api.common.contact.email:bYvH6@example.com}")
    String apiContactEmail;

    /**
     * Will exposed on $HOST:$PORT/swagger-ui.html
     *
     * @return the common OpenAPI documentation
     */
    @Bean
    public OpenAPI getOpenApiDocumentation() {
        return new OpenAPI()
                .info(new Info().title(apiTitle)
                        .description(apiDescription)
                        .version(apiVersion)
                        .contact(new Contact()
                                .name(apiContactName)
                                .url(apiContactUrl)
                                .email(apiContactEmail))
                        .termsOfService(apiTermsOfService)
                        .license(new License()
                                .name(apiLicense)
                                .url(apiLicenseUrl)))
                .externalDocs(new ExternalDocumentation()
                        .description(apiExternalDocDesc)
                        .url(apiExternalDocUrl));
    }
}
