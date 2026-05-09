package com.pm11.backend.auth;

import com.pm11.backend.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Bearer 토큰을 해석해 사용자 ID를 돌려주는 헬퍼.
 * 컨트롤러에서 직접 사용한다.
 */
@Component
@RequiredArgsConstructor
public class AuthSupport {

    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenStore tokenStore;

    public String requireUserId(HttpServletRequest request) {
        return optionalUserId(request).orElseThrow(() -> ApiException.unauthorized("인증이 필요합니다."));
    }

    public java.util.Optional<String> optionalUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return java.util.Optional.empty();
        }
        String token = header.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            return java.util.Optional.empty();
        }
        return tokenStore.resolveAccess(token);
    }
}
