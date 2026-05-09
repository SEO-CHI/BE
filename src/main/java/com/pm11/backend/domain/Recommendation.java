package com.pm11.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "recommendation",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_recommendation_check_in_place", columnNames = {"check_in_id", "place_id"}),
            @UniqueConstraint(name = "uk_recommendation_check_in_rank", columnNames = {"check_in_id", "rank_no"})
        })
@Getter
@Setter
@NoArgsConstructor
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

    @Column(name = "rank_no", nullable = false)
    private int rankNo;

    @Column(name = "distance_m", nullable = false)
    private double distanceM;

    @Column(name = "walk_minutes", nullable = false)
    private int walkMinutes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
