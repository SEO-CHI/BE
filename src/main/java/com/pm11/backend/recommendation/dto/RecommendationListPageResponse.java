package com.pm11.backend.recommendation.dto;

public record RecommendationListPageResponse(int limit, String next_cursor, boolean has_more) {}
