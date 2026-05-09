package com.pm11.backend.place;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 * 장소 마스터. API 로 생성·수정·삭제하지 않으며, DB 적재 데이터를 조회만 한다.
 */
@Entity
@Immutable
@Table(name = "place")
@Getter
@NoArgsConstructor
public class Place {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 20)
    private String category;

    @Column(length = 255)
    private String address;

    @Column(name = "is_indoor")
    private Boolean indoor;

    @Column(name = "is_free")
    private Boolean free;

    @Column(name = "needs_reservation")
    private Boolean needsReservation;

    private Double latitude;

    private Double longitude;

    @Column(name = "external_url", columnDefinition = "TEXT")
    private String externalUrl;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "operating_hours", columnDefinition = "TEXT")
    private String operatingHours;

    @Column(name = "closed_days", columnDefinition = "TEXT")
    private String closedDays;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "operating_rules", columnDefinition = "json")
    private String operatingRules;

    @Column(name = "closed_rules", columnDefinition = "json")
    private String closedRules;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
