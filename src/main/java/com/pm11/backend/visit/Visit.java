package com.pm11.backend.visit;

import com.pm11.backend.place.Place;
import com.pm11.backend.user.User;
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
        name = "visit",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_visit_user_place",
                columnNames = {"user_id", "place_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Visit {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    /** 1~5, 별점 등록 전엔 null */
    private Integer rating;

    @Column(name = "visited_at", nullable = false)
    private Instant visitedAt;

    @Builder
    public Visit(String id, User user, Place place, Integer rating, Instant visitedAt) {
        this.id = id;
        this.user = user;
        this.place = place;
        this.rating = rating;
        this.visitedAt = visitedAt;
    }

    public void updateRating(int rating) {
        this.rating = rating;
    }
}
