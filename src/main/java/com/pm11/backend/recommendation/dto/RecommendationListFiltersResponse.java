package com.pm11.backend.recommendation.dto;

public record RecommendationListFiltersResponse(
        String fee, String place_type, int radius_m, int max_walk_time_min, String sort) {}
