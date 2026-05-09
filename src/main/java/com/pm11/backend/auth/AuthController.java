package com.pm11.backend.auth;

import com.pm11.backend.user.User;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthSupport authSupport;

    @PostMapping("/social")
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
    public RefreshResponse refresh(@RequestBody RefreshRequest request) {
        TokenStore.Issued tokens = authService.refresh(request.refresh_token());
        return new RefreshResponse(
                new Tokens(tokens.accessToken(), tokens.refreshToken(), "Bearer", tokens.expiresIn()));
    }

    @PostMapping("/logout")
    public LogoutResponse logout(HttpServletRequest httpRequest, @RequestBody LogoutRequest request) {
        authSupport.requireUserId(httpRequest);
        authService.logout(request.refresh_token());
        return new LogoutResponse(true);
    }

    public record SocialLoginRequest(String provider, String id_token, String access_token) {}

    public record SocialLoginResponse(UserInfo user, Tokens tokens) {}

    public record RefreshRequest(String refresh_token) {}

    public record RefreshResponse(Tokens tokens) {}

    public record LogoutRequest(String refresh_token) {}

    public record LogoutResponse(boolean success) {}

    public record UserInfo(String user_id, String provider, Instant created_at) {}

    public record Tokens(String access_token, String refresh_token, String token_type, long expires_in) {}
}
