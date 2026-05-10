package com.pm11.backend.emotion;

import com.pm11.backend.emotion.dto.EmotionItemResponse;
import com.pm11.backend.emotion.dto.EmotionListResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/emotions")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;

    @GetMapping
    public EmotionListResponse list() {
        List<EmotionItemResponse> items = emotionService.findAll().stream()
                .map(e -> new EmotionItemResponse(e.getId(), e.getName()))
                .toList();
        return new EmotionListResponse(items);
    }
}
