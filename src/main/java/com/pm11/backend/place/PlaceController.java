package com.pm11.backend.place;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/{place_id}")
    public PlaceResponse get(@PathVariable("place_id") Integer placeId) {
        Place place = placeService.getById(placeId);
        return PlaceResponse.of(place, placeService.isOpenNow(place), null);
    }

    public record PlaceResponse(
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
            boolean is_open_now,
            String reason) {

        public static PlaceResponse of(Place p, boolean openNow, String reason) {
            return new PlaceResponse(
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
                    openNow,
                    reason);
        }
    }
}
