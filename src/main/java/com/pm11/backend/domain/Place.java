package com.pm11.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 * 장소 마스터는 API로 생성·수정·삭제하지 않으며, DB에 적재된 행을 조회만 한다.
 */
@Entity
@Immutable
@Table(name = "place")
@Getter
@NoArgsConstructor
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(name = "is_indoor", nullable = false)
    private boolean indoor;

    @Column(name = "is_free", nullable = false)
    private boolean free;

    @Column(name = "needs_reservation", nullable = false)
    private boolean needsReservation;

    @Column(nullable = false, length = 1000)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "external_url", length = 2000)
    private String externalUrl;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(name = "operating_hours", columnDefinition = "TEXT")
    private String operatingHours;

    @Column(name = "closed_days", columnDefinition = "TEXT")
    private String closedDays;

    @Column(columnDefinition = "TEXT")
    private String note;

    // @Column(columnDefinition = "TEXT")
    // private String price;

    @Column(name = "operating_rules", columnDefinition = "TEXT")
    private String operatingRules;

    @Column(name = "closed_rules", columnDefinition = "TEXT")
    private String closedRules;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
