package com.pm11.backend.checkin;

import com.pm11.backend.auth.AuthSupport;
import com.pm11.backend.checkin.dto.CreateCheckInRequest;
import com.pm11.backend.checkin.dto.CreateCheckInResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "check-in", description = "감정 체크인")
public class CheckInController {

    private final CheckInService checkInService;
    private final AuthSupport authSupport;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "체크인 생성", description = "Bearer 토큰이 있으면 회원 체크인, 없으면 비회원 체크인으로 처리됩니다.")
    public CreateCheckInResponse create(HttpServletRequest request, @RequestBody CreateCheckInRequest body) {
        String userId = authSupport.optionalUserId(request).orElse(null);
        CheckIn checkIn = checkInService.create(userId, body.emotion_id(), body.latitude(), body.longitude());
        return new CreateCheckInResponse(checkIn.getId());
    }
}
