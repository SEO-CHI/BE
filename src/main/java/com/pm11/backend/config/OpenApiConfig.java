package com.pm11.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class OpenApiConfig {

    private static final String DEMO_DESCRIPTION =
            """
            ## 빠른 시작 (3단계)

            1. **[여기 클릭](http://localhost:8080/demo/access-token)**하여 테스트용 토큰을 복사하세요.
            2. 우측 **Authentication** 버튼 → `Bearer {복사한_토큰}` 입력
            3. API를 테스트하세요! (감정 ID `em_01HZX2B7C3D4E5F6A7B8C9D0`, 장소 ID `167` 이미 세팅됨)
            """;

    @Bean
    public OpenAPI openAPI(Environment environment) {
        boolean demo = Arrays.asList(environment.getActiveProfiles()).contains("demo");
        String description = demo ? DEMO_DESCRIPTION : "PM11 Backend API v1";

        return new OpenAPI()
                .info(new Info().title("PM11 API").version("v1").description(description))
                .components(new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .description("access_token (Bearer 접두사 없이 토큰만 입력)")));
    }
}
