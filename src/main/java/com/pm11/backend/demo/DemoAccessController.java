package com.pm11.backend.demo;

import com.pm11.backend.demo.dto.DemoAccessTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
@Profile("demo")
@RequiredArgsConstructor
@Tag(name = "demo", description = "데모 전용 (demo 프로필에서만 활성)")
public class DemoAccessController {

    private final DemoContext demoContext;

    @GetMapping("/access-token")
    @Operation(summary = "테스트용 access token 발급", description = "Scalar Authorize에 붙여넣을 Bearer 토큰을 반환합니다.")
    public DemoAccessTokenResponse accessToken() {
        return new DemoAccessTokenResponse(demoContext.getAccessToken());
    }
}
