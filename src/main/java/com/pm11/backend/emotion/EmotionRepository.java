package com.pm11.backend.emotion;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionRepository extends JpaRepository<Emotion, String> {
}
