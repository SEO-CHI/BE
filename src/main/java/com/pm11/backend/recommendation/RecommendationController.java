package com.pm11.backend.recommendation;

import com.pm11.backend.auth.AuthSupport;
import com.pm11.backend.recommendation.dto.RecommendationItemResponse;
import com.pm11.backend.recommendation.dto.RecommendationListFiltersResponse;
import com.pm11.backend.recommendation.dto.RecommendationListPageResponse;
import com.pm11.backend.recommendation.dto.RecommendationListResponse;
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

        List<RecommendationItemResponse> items = result.items().stream()
                .map(it -> RecommendationItemResponse.of(
                        it.place(), it.distanceM(), it.walkMinutes(), it.isOpenNow(), it.reason()))
                .toList();

        return new RecommendationListResponse(
                result.checkIn().getId(),
                new RecommendationListFiltersResponse(
                        filters.fee(),
                        filters.placeType(),
                        filters.radiusM(),
                        filters.maxWalkMinutes(),
                        filters.sort()),
                items,
                new RecommendationListPageResponse(filters.limit(), result.nextCursor(), result.hasMore()));
    }
}
