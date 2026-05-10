package com.pm11.backend.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "user",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_provider",
                columnNames = {"provider", "provider_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(name = "provider_id", nullable = false, length = 255)
    private String providerId;

    @Column(length = 255)
    private String email;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Builder
    public User(String id, AuthProvider provider, String providerId, String email, Instant createdAt) {
        this.id = id;
        this.provider = provider;
        this.providerId = providerId;
        this.email = email;
        this.createdAt = createdAt;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete(Instant at) {
        this.deletedAt = at;
    }
}
