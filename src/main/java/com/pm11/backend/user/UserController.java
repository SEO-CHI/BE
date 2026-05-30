package com.pm11.backend.user;

import com.pm11.backend.auth.AuthSupport;
import com.pm11.backend.auth.TokenStore;
import com.pm11.backend.user.dto.DeleteMeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "user", description = "회원")
public class UserController {

    private final UserService userService;
    private final AuthSupport authSupport;
    private final TokenStore tokenStore;

    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴")
    @SecurityRequirement(name = "bearerAuth")
    public DeleteMeResponse deleteMe(HttpServletRequest request) {
        String userId = authSupport.requireUserId(request);
        Instant deletedAt = userService.softDelete(userId);
        tokenStore.revokeAllForUser(userId);
        return new DeleteMeResponse(userId, deletedAt);
    }
}
