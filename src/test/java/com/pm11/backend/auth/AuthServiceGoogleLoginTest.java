package com.pm11.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pm11.backend.ApiException;
import com.pm11.backend.user.AuthProvider;
import com.pm11.backend.user.User;
import com.pm11.backend.user.UserService;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AuthServiceGoogleLoginTest {

    @Mock
    private UserService userService;

    @Mock
    private GoogleIdTokenVerificationService googleIdTokenVerificationService;

    private TokenStore tokenStore;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        tokenStore = new TokenStore();
        authService = new AuthService(userService, tokenStore, googleIdTokenVerificationService);
    }

    @Test
    void googleLogin_verifiesIdToken_andIssuesTokens() {
        String jwt = "header.payload.sig";
        var verified = new GoogleIdTokenVerificationService.Verified("google-sub-123", "user@gmail.com");
        when(googleIdTokenVerificationService.verify(jwt)).thenReturn(verified);

        Instant created = Instant.parse("2026-05-10T12:00:00Z");
        User user = User.builder()
                .id("u-test-1")
                .provider(AuthProvider.google)
                .providerId("google-sub-123")
                .email("user@gmail.com")
                .createdAt(created)
                .build();
        when(userService.findOrCreate(AuthProvider.google, "google-sub-123", "user@gmail.com"))
                .thenReturn(user);

        AuthService.LoginResult result = authService.socialLogin("google", jwt, null);

        assertThat(result.user().getId()).isEqualTo("u-test-1");
        assertThat(result.tokens().accessToken()).startsWith("ak_");
        assertThat(result.tokens().refreshToken()).startsWith("rk_");
        assertThat(result.tokens().expiresIn()).isEqualTo(TokenStore.ACCESS_TOKEN_TTL_SECONDS);

        verify(googleIdTokenVerificationService).verify(eq(jwt));
    }

    @Test
    void googleLogin_propagatesVerificationFailure() {
        when(googleIdTokenVerificationService.verify("bad"))
                .thenThrow(ApiException.unauthorized("유효하지 않은 Google ID 토큰입니다."));

        ApiException ex = assertThrows(ApiException.class, () -> authService.socialLogin("google", "bad", null));
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
