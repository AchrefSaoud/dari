package utm.tn.dari.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
        @Bean
        public OpenAPI customOpenAPI() {
            final String securitySchemeName = "bearerAuth";
            
            return new OpenAPI()
                    .components(
                            new Components()
                                    .addSecuritySchemes(securitySchemeName,
                                            new SecurityScheme()
                                                    .name(securitySchemeName)
                                                    .type(SecurityScheme.Type.HTTP)
                                                    .scheme("bearer")
                                                    .bearerFormat("JWT")
                                    )
                    );
        }
}
