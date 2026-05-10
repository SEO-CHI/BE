package com.pm11.backend.checkin;

import com.pm11.backend.auth.AuthSupport;
import com.pm11.backend.checkin.dto.CreateCheckInRequest;
import com.pm11.backend.checkin.dto.CreateCheckInResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/check-ins")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;
    private final AuthSupport authSupport;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCheckInResponse create(HttpServletRequest request, @RequestBody CreateCheckInRequest body) {
        String userId = authSupport.optionalUserId(request).orElse(null);
        CheckIn checkIn = checkInService.create(userId, body.emotion_id(), body.latitude(), body.longitude());
        return new CreateCheckInResponse(checkIn.getId());
    }
}
