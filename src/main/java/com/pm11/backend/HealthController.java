package com.pm11.backend;

import java.time.Instant;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "health", description = "헬스체크")
public class HealthController {

    @GetMapping(path = "/api/health", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "서버 상태 확인")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "backend",
                "timestamp", Instant.now().toString());
    }
}
