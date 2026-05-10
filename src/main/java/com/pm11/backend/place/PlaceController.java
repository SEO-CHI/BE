package com.pm11.backend.place;

import com.pm11.backend.place.dto.PlaceResponse;
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
}
