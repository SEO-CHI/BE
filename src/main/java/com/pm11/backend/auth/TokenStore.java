package com.pm11.backend.auth;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * 단순 인메모리 토큰 저장소.
 * 운영 환경에서는 DB/Redis 등으로 교체할 것.
 */
@Component
public class TokenStore {

    public static final long ACCESS_TOKEN_TTL_SECONDS = 3600L;
    public static final long REFRESH_TOKEN_TTL_SECONDS = 60L * 60 * 24 * 30;

    private final ConcurrentHashMap<String, Record> accessTokens = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Record> refreshTokens = new ConcurrentHashMap<>();

    public Issued issue(String userId) {
        Instant now = Instant.now();
        String access = "ak_" + UUID.randomUUID().toString().replace("-", "");
        String refresh = "rk_" + UUID.randomUUID().toString().replace("-", "");
        accessTokens.put(access, new Record(userId, now.plusSeconds(ACCESS_TOKEN_TTL_SECONDS)));
        refreshTokens.put(refresh, new Record(userId, now.plusSeconds(REFRESH_TOKEN_TTL_SECONDS)));
        return new Issued(access, refresh, ACCESS_TOKEN_TTL_SECONDS);
    }

    public Optional<String> resolveAccess(String accessToken) {
        Record r = accessTokens.get(accessToken);
        if (r == null) return Optional.empty();
        if (r.expiresAt().isBefore(Instant.now())) {
            accessTokens.remove(accessToken);
            return Optional.empty();
        }
        return Optional.of(r.userId());
    }

    public Optional<String> consumeRefresh(String refreshToken) {
        Record r = refreshTokens.remove(refreshToken);
        if (r == null) return Optional.empty();
        if (r.expiresAt().isBefore(Instant.now())) return Optional.empty();
        return Optional.of(r.userId());
    }

    public void revokeRefresh(String refreshToken) {
        refreshTokens.remove(refreshToken);
    }

    public void revokeAllForUser(String userId) {
        accessTokens.entrySet().removeIf(e -> e.getValue().userId().equals(userId));
        refreshTokens.entrySet().removeIf(e -> e.getValue().userId().equals(userId));
    }

    public record Issued(String accessToken, String refreshToken, long expiresIn) {}

    private record Record(String userId, Instant expiresAt) {}
}
