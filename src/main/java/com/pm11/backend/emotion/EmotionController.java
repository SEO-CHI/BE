package com.pm11.backend.emotion;

import com.pm11.backend.emotion.dto.EmotionItemResponse;
import com.pm11.backend.emotion.dto.EmotionListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/emotions")
@RequiredArgsConstructor
@Tag(name = "emotion", description = "감정 목록")
public class EmotionController {

    private final EmotionService emotionService;

    @GetMapping
    @Operation(summary = "감정 목록 조회")
    public EmotionListResponse list() {
        List<EmotionItemResponse> items = emotionService.findAll().stream()
                .map(e -> new EmotionItemResponse(e.getId(), e.getName()))
                .toList();
        return new EmotionListResponse(items);
    }
}
