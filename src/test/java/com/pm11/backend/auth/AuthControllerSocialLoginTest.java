package com.pm11.backend.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.pm11.backend.user.AuthProvider;
import com.pm11.backend.user.User;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class AuthControllerSocialLoginTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private AuthSupport authSupport;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new AuthController(authService, authSupport)).build();
    }

    @Test
    void social_google_returnsUserAndTokens() throws Exception {
        Instant created = Instant.parse("2026-05-10T12:00:00Z");
        User user = User.builder()
                .id("u-test-1")
                .provider(AuthProvider.google)
                .providerId("google-sub")
                .email("user@gmail.com")
                .createdAt(created)
                .build();
        TokenStore.Issued tokens = new TokenStore.Issued("ak_test_access", "rk_test_refresh", 3600);
        when(authService.socialLogin(eq("google"), eq("mock-id-token"), eq(null)))
                .thenReturn(new AuthService.LoginResult(user, tokens));

        mockMvc.perform(post("/v1/auth/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "provider": "google",
                                  "id_token": "mock-id-token",
                                  "access_token": null
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.user_id").value("u-test-1"))
                .andExpect(jsonPath("$.user.provider").value("google"))
                .andExpect(jsonPath("$.tokens.access_token").value("ak_test_access"))
                .andExpect(jsonPath("$.tokens.refresh_token").value("rk_test_refresh"))
                .andExpect(jsonPath("$.tokens.token_type").value("Bearer"))
                .andExpect(jsonPath("$.tokens.expires_in").value(3600));
    }

    @Test
    void social_google_invokesServiceWithRequestBody() throws Exception {
        when(authService.socialLogin(any(), any(), any()))
                .thenAnswer(inv -> {
                    String provider = inv.getArgument(0);
                    String idToken = inv.getArgument(1);
                    String accessToken = inv.getArgument(2);
                    if (!"google".equals(provider)
                            || !"jwt-from-client".equals(idToken)
                            || accessToken != null) {
                        throw new AssertionError("unexpected arguments");
                    }
                    User user = User.builder()
                            .id("u2")
                            .provider(AuthProvider.google)
                            .providerId("sub")
                            .email(null)
                            .createdAt(Instant.EPOCH)
                            .build();
                    return new AuthService.LoginResult(
                            user, new TokenStore.Issued("ak_x", "rk_y", 3600));
                });

        mockMvc.perform(post("/v1/auth/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "provider": "google",
                                  "id_token": "jwt-from-client",
                                  "access_token": null
                                }
                                """))
                .andExpect(status().isOk());
    }
}
