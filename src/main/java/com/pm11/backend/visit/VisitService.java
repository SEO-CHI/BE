package com.pm11.backend.visit;

import com.pm11.backend.ApiException;
import com.pm11.backend.place.Place;
import com.pm11.backend.place.PlaceService;
import com.pm11.backend.user.User;
import com.pm11.backend.user.UserService;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final UserService userService;
    private final PlaceService placeService;

    @Transactional
    public Visit create(String userId, Integer placeId) {
        if (placeId == null) {
            throw ApiException.badRequest("place_id 가 필요합니다.");
        }
        User user = userService.getActiveUser(userId);
        Place place = placeService.getById(placeId);

        if (visitRepository.existsByUserIdAndPlaceId(user.getId(), place.getId())) {
            throw ApiException.conflict("이미 방문 완료한 장소입니다.");
        }

        Visit visit = Visit.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .place(place)
                .rating(null)
                .visitedAt(Instant.now())
                .build();
        return visitRepository.save(visit);
    }

    @Transactional
    public Visit updateRating(String userId, String visitId, Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw ApiException.badRequest("rating 은 1~5 범위여야 합니다.");
        }
        Visit visit = visitRepository
                .findById(visitId)
                .orElseThrow(() -> ApiException.notFound("방문 기록을 찾을 수 없습니다."));
        if (!visit.getUser().getId().equals(userId)) {
            throw ApiException.forbidden("본인의 방문 기록만 수정할 수 있습니다.");
        }
        visit.updateRating(rating);
        return visit;
    }
}
