package com.pm11.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "check_in")
@Getter
@Setter
@NoArgsConstructor
public class CheckIn {

    @Id
    @Column(length = 64, nullable = false)
    private String id;

    /** 비회원 체크인 시 {@code null} (FK {@code user_id} 미설정) */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emotion_id", nullable = false)
    private Emotion emotion;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "is_raining", nullable = false)
    private boolean raining;

    @Column(name = "checked_at", nullable = false)
    private Instant checkedAt;
}
