package com.pm11.backend.checkin;

import com.pm11.backend.emotion.Emotion;
import com.pm11.backend.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "check_in")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckIn {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    /** 비회원 체크인 시 null. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emotion_id", nullable = false)
    private Emotion emotion;

    private Double latitude;

    private Double longitude;

    @Column(name = "is_raining")
    private Boolean raining;

    @Column(name = "checked_at", nullable = false)
    private Instant checkedAt;

    @Builder
    public CheckIn(
            String id,
            User user,
            Emotion emotion,
            Double latitude,
            Double longitude,
            Boolean raining,
            Instant checkedAt) {
        this.id = id;
        this.user = user;
        this.emotion = emotion;
        this.latitude = latitude;
        this.longitude = longitude;
        this.raining = raining;
        this.checkedAt = checkedAt;
    }
}
