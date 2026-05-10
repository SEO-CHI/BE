package com.pm11.backend.recommendation.dto;

import java.util.List;

public record RecommendationListResponse(
        String check_in_id,
        RecommendationListFiltersResponse filters,
        List<RecommendationItemResponse> items,
        RecommendationListPageResponse page) {}
