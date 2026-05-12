package com.pm11.backend.recommendation;

import com.pm11.backend.checkin.CheckIn;
import com.pm11.backend.checkin.CheckInService;
import com.pm11.backend.place.Place;
import com.pm11.backend.place.PlaceRepository;
import com.pm11.backend.place.PlaceService;
import com.pm11.backend.place.dto.PlaceScanRow;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final Map<String, String> EMOTION_REASON = Map.of(
            "번아웃", "잠시 멈춰 숨을 고르기 좋은 곳이에요.",
            "외로움", "조용히 사람의 온기를 느끼기 좋은 곳이에요.",
            "무기력", "가볍게 움직이며 기분을 환기하기 좋은 곳이에요.",
            "불안", "마음을 가라앉히는 데 도움이 될 수 있어요.",
            "답답함", "탁 트인 기분으로 환기하기 좋은 곳이에요.");

    private static final int WALK_SPEED_M_PER_MIN = 67; // 약 4km/h

    private final CheckInService checkInService;
    private final PlaceRepository placeRepository;
    private final PlaceService placeService;

    @Transactional(readOnly = true)
    public Result recommend(String checkInId, RecommendationFilters filters, boolean memberAuthorized) {
        CheckIn checkIn = checkInService.getById(checkInId);

        List<PlaceScanRow> scanRows = placeRepository.findAllScanForRecommendation();
        List<Item> items = scanRows.stream()
                .filter(p -> p.latitude() != null && p.longitude() != null)
                .map(p -> {
                    double distance = haversine(
                            checkIn.getLatitude(), checkIn.getLongitude(),
                            p.latitude(), p.longitude());
                    int walk = (int) Math.ceil(distance / WALK_SPEED_M_PER_MIN);
                    return new Item(p, distance, walk);
                })
                .filter(it -> filters.matches(it.scan(), it.distanceM(), it.walkMinutes()))
                .sorted(Comparator.comparingDouble(Item::distanceM))
                .toList();

        int from = Math.max(filters.cursor(), 0);
        int to = Math.min(items.size(), from + filters.limit());
        List<Item> page = from >= items.size() ? List.of() : items.subList(from, to);
        boolean hasMore = to < items.size();

        String reason = memberAuthorized
                ? EMOTION_REASON.getOrDefault(checkIn.getEmotion().getName(), "도움이 될 수 있어요.")
                : null;

        List<Integer> pageIds = page.stream().map(it -> it.scan().id()).toList();
        Map<Integer, Place> placeById = placeRepository.findAllById(pageIds).stream()
                .collect(Collectors.toMap(Place::getId, Function.identity()));

        List<Enriched> enriched = page.stream()
                .map(it -> {
                    Place place = placeById.get(it.scan().id());
                    if (place == null) {
                        throw new IllegalStateException("Place not found for id=" + it.scan().id());
                    }
                    return new Enriched(
                            place, it.distanceM(), it.walkMinutes(), placeService.isOpenNow(place), reason);
                })
                .toList();

        String nextCursor = hasMore ? String.valueOf(to) : null;
        return new Result(checkIn, enriched, hasMore, nextCursor);
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000.0; // 지구 반경(m)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public record Item(PlaceScanRow scan, double distanceM, int walkMinutes) {}

    public record Enriched(Place place, double distanceM, int walkMinutes, boolean isOpenNow, String reason) {}

    public record Result(CheckIn checkIn, List<Enriched> items, boolean hasMore, String nextCursor) {}
}
