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
        name = "visit",
        uniqueConstraints = @UniqueConstraint(name = "uk_visit_user_place", columnNames = {"user_id", "place_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Visit {

    @Id
    @Column(length = 64, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    /** 1–5, optional until rated */
    @Column
    private Integer rating;

    @Column(name = "visited_at", nullable = false)
    private Instant visitedAt;
}
