package com.pm11.backend.checkin;

import com.pm11.backend.ApiException;
import com.pm11.backend.emotion.Emotion;
import com.pm11.backend.emotion.EmotionRepository;
import com.pm11.backend.user.User;
import com.pm11.backend.user.UserRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final EmotionRepository emotionRepository;
    private final UserRepository userRepository;

    @Transactional
    public CheckIn create(String userIdOrNull, String emotionId, Double latitude, Double longitude) {
        if (emotionId == null || emotionId.isBlank()) {
            throw ApiException.badRequest("emotion_id 가 필요합니다.");
        }
        if (latitude == null || longitude == null) {
            throw ApiException.badRequest("latitude / longitude 가 필요합니다.");
        }
        Emotion emotion = emotionRepository
                .findById(emotionId)
                .orElseThrow(() -> ApiException.notFound("감정을 찾을 수 없습니다."));

        User user = null;
        if (userIdOrNull != null) {
            user = userRepository
                    .findById(userIdOrNull)
                    .orElse(null);
        }

        CheckIn checkIn = CheckIn.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .emotion(emotion)
                .latitude(latitude)
                .longitude(longitude)
                .raining(false) // TODO: 기상청 API 연동
                .checkedAt(Instant.now())
                .build();
        return checkInRepository.save(checkIn);
    }

    @Transactional(readOnly = true)
    public CheckIn getById(String id) {
        return checkInRepository
                .findById(id)
                .orElseThrow(() -> ApiException.notFound("체크인을 찾을 수 없습니다."));
    }
}
