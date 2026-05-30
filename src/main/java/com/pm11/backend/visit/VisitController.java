package com.pm11.backend.visit;

import com.pm11.backend.auth.AuthSupport;
import com.pm11.backend.visit.dto.CreateVisitRequest;
import com.pm11.backend.visit.dto.CreateVisitResponse;
import com.pm11.backend.visit.dto.UpdateRatingRequest;
import com.pm11.backend.visit.dto.UpdateRatingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/visits")
@RequiredArgsConstructor
@Tag(name = "visit", description = "장소 방문 기록")
public class VisitController {

    private final VisitService visitService;
    private final AuthSupport authSupport;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "방문 기록 생성")
    @SecurityRequirement(name = "bearerAuth")
    public CreateVisitResponse create(HttpServletRequest request, @RequestBody CreateVisitRequest body) {
        String userId = authSupport.requireUserId(request);
        Visit visit = visitService.create(userId, body.place_id());
        return new CreateVisitResponse(visit.getId(), visit.getPlace().getId(), visit.getVisitedAt(), visit.getRating());
    }

    @PatchMapping("/{visit_id}/rating")
    @Operation(summary = "방문 평점 수정")
    @SecurityRequirement(name = "bearerAuth")
    public UpdateRatingResponse updateRating(
            HttpServletRequest request,
            @PathVariable("visit_id") String visitId,
            @RequestBody UpdateRatingRequest body) {
        String userId = authSupport.requireUserId(request);
        Visit visit = visitService.updateRating(userId, visitId, body.rating());
        return new UpdateRatingResponse(visit.getId(), visit.getRating());
    }
}
