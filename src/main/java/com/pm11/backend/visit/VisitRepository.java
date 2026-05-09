package com.pm11.backend.visit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitRepository extends JpaRepository<Visit, String> {

    boolean existsByUserIdAndPlaceId(String userId, Integer placeId);
}
