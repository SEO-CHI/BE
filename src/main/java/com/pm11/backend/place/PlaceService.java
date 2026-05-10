package com.pm11.backend.place;

import com.pm11.backend.ApiException;
import com.pm11.backend.place.opennow.OpenNowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final OpenNowService openNowService;

    @Transactional(readOnly = true)
    public Place getById(Integer id) {
        return placeRepository
                .findById(id)
                .orElseThrow(() -> ApiException.notFound("장소를 찾을 수 없습니다."));
    }

    /** {@code operating_rules} / {@code closed_rules} JSON과 서울 기준 현재 시각·공휴일 스냅샷으로 판정. */
    public boolean isOpenNow(Place place) {
        return openNowService.isOpenNow(place);
    }
}
