package com.pm11.backend.recommendation;

import com.pm11.backend.checkin.CheckIn;
import com.pm11.backend.place.Place;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "recommendation",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_recommendation_check_in_place",
                columnNames = {"check_in_id", "place_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recommendation {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "check_in_id", nullable = false)
    private CheckIn checkIn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "rank_no")
    private Integer rankNo;

    @Column(name = "distance_m")
    private Float distanceM;

    @Column(name = "walk_minutes")
    private Integer walkMinutes;

    @Column(name = "reason_text", columnDefinition = "TEXT")
    private String reasonText;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Builder
    public Recommendation(
            String id,
            CheckIn checkIn,
            Place place,
            Integer rankNo,
            Float distanceM,
            Integer walkMinutes,
            String reasonText,
            Instant createdAt) {
        this.id = id;
        this.checkIn = checkIn;
        this.place = place;
        this.rankNo = rankNo;
        this.distanceM = distanceM;
        this.walkMinutes = walkMinutes;
        this.reasonText = reasonText;
        this.createdAt = createdAt;
    }
}
