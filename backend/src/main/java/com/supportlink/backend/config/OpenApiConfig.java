package com.supportlink.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .openapi("3.0.1")
                                .info(new Info()
                                                .title("SupportLink API")
                                                .version("1.0.0")
                                                .description("고객 지원 포털을 위한 RESTful API"))
                                .specVersion(SpecVersion.V30)
                                .components(new Components()
                                                .addSecuritySchemes("BearerAuth",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")));
        }

        @Bean
        public OpenApiCustomizer openApiCustomizer() {
                return openApi -> openApi.setSpecVersion(SpecVersion.V30);
        }
}
