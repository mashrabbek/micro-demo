package com.example.microservices.composite.product.config;

//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Contact;
//import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;

//@Configuration
public class SpringDocConfig {

    @Value("${api.common.version}")         String apiVersion;
    @Value("${api.common.title}")           String apiTitle;
    @Value("${api.common.description}")     String apiDescription;
    @Value("${api.common.termsOfService}")  String apiTermsOfService;
    @Value("${api.common.license}")         String apiLicense;
    @Value("${api.common.licenseUrl}")      String apiLicenseUrl;
    @Value("${api.common.externalDocDesc}") String apiExternalDocDesc;
    @Value("${api.common.externalDocUrl}")  String apiExternalDocUrl;
    @Value("${api.common.contact.name}")    String apiContactName;
    @Value("${api.common.contact.url}")     String apiContactUrl;
    @Value("${api.common.contact.email}")   String apiContactEmail;


//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("Product Composite Service API")
//                        .version("1.0")
//                        .description("API documentation for Product Composite Service")
//                        .contact(new Contact()
//                                .name("Mashrabbek")
//                                .email("mashrabbek@example.com")
//                        )
//                );
//    }
    /**
     * Will exposed on $HOST:$PORT/swagger-ui.html
     *
     * @return the common OpenAPI documentation
     */


    //    @Bean
//    public OpenAPI getOpenApiDocumentation() {
//        return new OpenAPI()
//                .info(new Info().title(apiTitle)
//                        .description(apiDescription)
//                        .version(apiVersion)
//                        .contact(new Contact()
//                                .name(apiContactName)
//                                .url(apiContactUrl)
//                                .email(apiContactEmail))
//                        .termsOfService(apiTermsOfService)
//                        .license(new License()
//                                .name(apiLicense)
//                                .url(apiLicenseUrl)))
//                .externalDocs(new ExternalDocumentation()
//                        .description(apiExternalDocDesc)
//                        .url(apiExternalDocUrl));
//    }
//

    @Bean
    public StandardReflectionParameterNameDiscoverer standardReflectionParameterNameDiscoverer() {
        return new StandardReflectionParameterNameDiscoverer();
    }
}
