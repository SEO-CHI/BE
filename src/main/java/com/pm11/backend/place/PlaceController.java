package com.pm11.backend.place;

import com.pm11.backend.place.dto.PlaceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/places")
@RequiredArgsConstructor
@Tag(name = "place", description = "장소 조회")
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/{place_id}")
    @Operation(summary = "장소 상세 조회")
    public PlaceResponse get(@PathVariable("place_id") Integer placeId) {
        Place place = placeService.getById(placeId);
        return PlaceResponse.of(place, placeService.isOpenNow(place), null);
    }
}
