package com.pm11.backend.auth;

import com.pm11.backend.auth.dto.LogoutRequest;
import com.pm11.backend.auth.dto.LogoutResponse;
import com.pm11.backend.auth.dto.RefreshRequest;
import com.pm11.backend.auth.dto.RefreshResponse;
import com.pm11.backend.auth.dto.SocialLoginRequest;
import com.pm11.backend.auth.dto.SocialLoginResponse;
import com.pm11.backend.auth.dto.Tokens;
import com.pm11.backend.auth.dto.UserInfo;
import com.pm11.backend.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "auth", description = "소셜 로그인 · 토큰 갱신 · 로그아웃")
public class AuthController {

    private final AuthService authService;
    private final AuthSupport authSupport;

    @PostMapping("/social")
    @Operation(summary = "소셜 로그인", description = "첫 로그인 시 자동 회원가입됩니다. demo 환경에서는 GET /demo/access-token 사용을 권장합니다.")
    public SocialLoginResponse social(@RequestBody SocialLoginRequest request) {
        AuthService.LoginResult result =
                authService.socialLogin(request.provider(), request.id_token(), request.access_token());
        User user = result.user();
        TokenStore.Issued tokens = result.tokens();
        return new SocialLoginResponse(
                new UserInfo(user.getId(), user.getProvider().name(), user.getCreatedAt()),
                new Tokens(tokens.accessToken(), tokens.refreshToken(), "Bearer", tokens.expiresIn()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "refresh_token으로 access_token을 재발급합니다.")
    public RefreshResponse refresh(@RequestBody RefreshRequest request) {
        TokenStore.Issued tokens = authService.refresh(request.refresh_token());
        return new RefreshResponse(
                new Tokens(tokens.accessToken(), tokens.refreshToken(), "Bearer", tokens.expiresIn()));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "refresh_token을 무효화합니다.")
    @SecurityRequirement(name = "bearerAuth")
    public LogoutResponse logout(HttpServletRequest httpRequest, @RequestBody LogoutRequest request) {
        authSupport.requireUserId(httpRequest);
        authService.logout(request.refresh_token());
        return new LogoutResponse(true);
    }
}
