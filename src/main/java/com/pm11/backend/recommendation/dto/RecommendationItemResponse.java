package com.pm11.backend.recommendation.dto;

import com.pm11.backend.place.Place;

public record RecommendationItemResponse(
        Integer place_id,
        String name,
        String category,
        Boolean is_indoor,
        Boolean is_free,
        Boolean needs_reservation,
        String address,
        Double latitude,
        Double longitude,
        String external_url,
        String phone_number,
        String operating_hours,
        String closed_days,
        String note,
        String price,
        double distance_m,
        int walk_minutes,
        boolean is_open_now,
        String reason) {

    public static RecommendationItemResponse of(
            Place p, double distance, int walkMinutes, boolean openNow, String reason) {
        return new RecommendationItemResponse(
                p.getId(),
                p.getName(),
                p.getCategory(),
                p.getIndoor(),
                p.getFree(),
                p.getNeedsReservation(),
                p.getAddress(),
                p.getLatitude(),
                p.getLongitude(),
                p.getExternalUrl(),
                p.getPhoneNumber(),
                p.getOperatingHours(),
                p.getClosedDays(),
                p.getNote(),
                p.getPrice(),
                distance,
                walkMinutes,
                openNow,
                reason);
    }
}
