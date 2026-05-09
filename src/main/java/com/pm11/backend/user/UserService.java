package com.pm11.backend.user;

import com.pm11.backend.ApiException;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User findOrCreate(AuthProvider provider, String providerId, String email) {
        return userRepository
                .findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> userRepository.save(User.builder()
                        .id(UUID.randomUUID().toString())
                        .provider(provider)
                        .providerId(providerId)
                        .email(email)
                        .createdAt(Instant.now())
                        .build()));
    }

    @Transactional(readOnly = true)
    public User getActiveUser(String userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> ApiException.notFound("사용자를 찾을 수 없습니다."));
        if (user.isDeleted()) {
            throw ApiException.notFound("탈퇴한 사용자입니다.");
        }
        return user;
    }

    @Transactional
    public Instant softDelete(String userId) {
        User user = getActiveUser(userId);
        Instant now = Instant.now();
        user.softDelete(now);
        return now;
    }
}
