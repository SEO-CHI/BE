package com.pm11.backend.recommendation;

import com.pm11.backend.place.Place;
import com.pm11.backend.place.dto.PlaceScanRow;

public record RecommendationFilters(
        String fee,
        String placeType,
        int radiusM,
        int maxWalkMinutes,
        String sort,
        int limit,
        int cursor) {

    public static RecommendationFilters of(
            String fee,
            String placeType,
            Integer radiusM,
            Integer maxWalkMinutes,
            String sort,
            Integer limit,
            String cursor) {
        return new RecommendationFilters(
                fee == null ? "all" : fee,
                placeType == null ? "all" : placeType,
                radiusM == null ? 0 : radiusM,
                maxWalkMinutes == null ? 0 : maxWalkMinutes,
                sort == null ? "distance_asc" : sort,
                limit == null || limit <= 0 ? 30 : limit,
                parseCursor(cursor));
    }

    private static int parseCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) return 0;
        try {
            return Integer.parseInt(cursor);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean matches(Place place, double distanceM, int walkMinutes) {
        return matches(place.getFree(), place.getIndoor(), distanceM, walkMinutes);
    }

    public boolean matches(PlaceScanRow place, double distanceM, int walkMinutes) {
        return matches(place.free(), place.indoor(), distanceM, walkMinutes);
    }

    private boolean matches(Boolean free, Boolean indoor, double distanceM, int walkMinutes) {
        if (radiusM > 0 && distanceM > radiusM) return false;
        if (maxWalkMinutes > 0 && walkMinutes > maxWalkMinutes) return false;
        if ("free".equalsIgnoreCase(fee) && Boolean.FALSE.equals(free)) return false;
        if ("indoor".equalsIgnoreCase(placeType) && !Boolean.TRUE.equals(indoor)) return false;
        if ("outdoor".equalsIgnoreCase(placeType) && Boolean.TRUE.equals(indoor)) return false;
        return true;
    }
}
