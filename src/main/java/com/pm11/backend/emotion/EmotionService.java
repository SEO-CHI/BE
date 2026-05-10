package com.pm11.backend.emotion;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmotionService {

    private static final List<String> SEED_NAMES = List.of("번아웃", "외로움", "무기력", "불안", "답답함");

    private final EmotionRepository emotionRepository;
    private final Environment environment;

    @PostConstruct
    @Transactional
    public void seedIfEmpty() {
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            return;
        }
        if (emotionRepository.count() > 0) {
            return;
        }
        for (String name : SEED_NAMES) {
            emotionRepository.save(new Emotion(UUID.randomUUID().toString(), name));
        }
    }

    @Transactional(readOnly = true)
    public List<Emotion> findAll() {
        return emotionRepository.findAll();
    }
}
