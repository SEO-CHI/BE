package com.pm11.backend.emotion;

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
        List<EmotionItem> items = emotionService.findAll().stream()
                .map(e -> new EmotionItem(e.getId(), e.getName()))
                .toList();
        return new EmotionListResponse(items);
    }

    public record EmotionListResponse(List<EmotionItem> emotions) {}

    public record EmotionItem(String emotion_id, String name) {}
}
