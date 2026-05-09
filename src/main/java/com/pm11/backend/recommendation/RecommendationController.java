package com.pm11.backend.recommendation;

import com.pm11.backend.auth.AuthSupport;
import com.pm11.backend.place.Place;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/check-ins")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final AuthSupport authSupport;

    @GetMapping("/{check_in_id}/recommendations")
    public RecommendationListResponse list(
            HttpServletRequest request,
            @PathVariable("check_in_id") String checkInId,
            @RequestParam(required = false) String fee,
            @RequestParam(name = "place_type", required = false) String placeType,
            @RequestParam(name = "radius_m", required = false) Integer radiusM,
            @RequestParam(name = "max_walk_time_min", required = false) Integer maxWalkMin,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String cursor) {

        boolean memberAuthorized = authSupport.optionalUserId(request).isPresent();
        RecommendationFilters filters =
                RecommendationFilters.of(fee, placeType, radiusM, maxWalkMin, sort, limit, cursor);

        RecommendationService.Result result =
                recommendationService.recommend(checkInId, filters, memberAuthorized);

        List<Item> items = result.items().stream()
                .map(it -> Item.of(it.place(), it.distanceM(), it.walkMinutes(), it.isOpenNow(), it.reason()))
                .toList();

        return new RecommendationListResponse(
                result.checkIn().getId(),
                new Filters(
                        filters.fee(),
                        filters.placeType(),
                        filters.radiusM(),
                        filters.maxWalkMinutes(),
                        filters.sort()),
                items,
                new Page(filters.limit(), result.nextCursor(), result.hasMore()));
    }

    public record RecommendationListResponse(
            String check_in_id, Filters filters, List<Item> items, Page page) {}

    public record Filters(
            String fee, String place_type, int radius_m, int max_walk_time_min, String sort) {}

    public record Page(int limit, String next_cursor, boolean has_more) {}

    public record Item(
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
            double distance_m,
            int walk_minutes,
            boolean is_open_now,
            String reason) {

        static Item of(Place p, double distance, int walkMinutes, boolean openNow, String reason) {
            return new Item(
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
                    distance,
                    walkMinutes,
                    openNow,
                    reason);
        }
    }
}
