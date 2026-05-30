package com.pm11.backend.demo;

import com.pm11.backend.auth.TokenStore;
import com.pm11.backend.emotion.Emotion;
import com.pm11.backend.emotion.EmotionRepository;
import com.pm11.backend.user.AuthProvider;
import com.pm11.backend.user.User;
import com.pm11.backend.user.UserRepository;
import jakarta.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("demo")
@RequiredArgsConstructor
public class DemoDataSeeder {

    public static final String DEMO_USER_ID = "u_demo000000000000000000000001";
    public static final String DEMO_EMOTION_ID = "em_01HZX2B7C3D4E5F6A7B8C9D0";
    public static final int DEMO_PLACE_ID = 167;

    private static final List<Emotion> EMOTIONS = List.of(
            new Emotion("em_01HZX2B7C3D4E5F6A7B8C9D0", "번아웃"),
            new Emotion("em_01HZX2B7C3D4E5F6A7B8C9D1", "외로움"),
            new Emotion("em_01HZX2B7C3D4E5F6A7B8C9D2", "무기력"),
            new Emotion("em_01HZX2B7C3D4E5F6A7B8C9D3", "불안"),
            new Emotion("em_01HZX2B7C3D4E5F6A7B8C9D4", "답답함"));

    private final EmotionRepository emotionRepository;
    private final UserRepository userRepository;
    private final TokenStore tokenStore;
    private final DemoContext demoContext;
    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    @Transactional
    public void seed() {
        if (emotionRepository.count() == 0) {
            emotionRepository.saveAll(EMOTIONS);
        }

        if (!userRepository.existsById(DEMO_USER_ID)) {
            userRepository.save(User.builder()
                    .id(DEMO_USER_ID)
                    .provider(AuthProvider.kakao)
                    .providerId("demo-user")
                    .email(null)
                    .createdAt(Instant.now())
                    .build());
        }

        Integer placeCount =
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM place WHERE id = ?", Integer.class, DEMO_PLACE_ID);
        if (placeCount != null && placeCount == 0) {
            jdbcTemplate.update(
                    """
                    INSERT INTO place (id, name, category, created_at, latitude, longitude, is_indoor, is_free, needs_reservation)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    DEMO_PLACE_ID,
                    "테스트장소",
                    "갤러리",
                    Timestamp.from(Instant.now()),
                    37.5665,
                    126.978,
                    true,
                    false,
                    false);
        }

        demoContext.setAccessToken(tokenStore.issue(DEMO_USER_ID).accessToken());
    }
}
