package com.pm11.backend.auth;

import com.pm11.backend.ApiException;
import com.pm11.backend.user.AuthProvider;
import com.pm11.backend.user.User;
import com.pm11.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final TokenStore tokenStore;

    /**
     * 소셜 로그인. 외부 OIDC/OAuth 검증은 단순화해 id_token 문자열을 그대로 provider 사용자 식별자로 사용한다.
     * TODO: 운영 환경에서는 google/kakao 토큰 검증 절차 추가.
     */
    @Transactional
    public LoginResult socialLogin(String providerName, String idToken, String accessToken) {
        if (providerName == null) {
            throw ApiException.badRequest("provider 값이 필요합니다.");
        }
        AuthProvider provider;
        try {
            provider = AuthProvider.valueOf(providerName.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("지원하지 않는 provider 입니다.");
        }

        String providerId = idToken != null && !idToken.isBlank()
                ? idToken
                : (accessToken != null && !accessToken.isBlank() ? accessToken : null);
        if (providerId == null) {
            throw ApiException.badRequest("id_token 또는 access_token 이 필요합니다.");
        }

        User user = userService.findOrCreate(provider, providerId, null);
        TokenStore.Issued issued = tokenStore.issue(user.getId());
        return new LoginResult(user, issued);
    }

    @Transactional
    public TokenStore.Issued refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw ApiException.badRequest("refresh_token 이 필요합니다.");
        }
        String userId = tokenStore.consumeRefresh(refreshToken)
                .orElseThrow(() -> ApiException.unauthorized("유효하지 않은 refresh_token 입니다."));
        return tokenStore.issue(userId);
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            tokenStore.revokeRefresh(refreshToken);
        }
    }

    public record LoginResult(User user, TokenStore.Issued tokens) {}
}
