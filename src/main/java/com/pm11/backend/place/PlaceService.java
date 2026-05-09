package com.pm11.backend.place;

import com.pm11.backend.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    @Transactional(readOnly = true)
    public Place getById(Integer id) {
        return placeRepository
                .findById(id)
                .orElseThrow(() -> ApiException.notFound("장소를 찾을 수 없습니다."));
    }

    /**
     * 운영시간 파싱은 추후 도입. 현재는 운영 정보가 있고 장소 데이터가 살아있으면 true 로 단순 처리.
     * TODO: operating_rules / closed_rules 기반 시간 판정.
     */
    public boolean isOpenNow(Place place) {
        return place.getOperatingHours() != null;
    }
}
