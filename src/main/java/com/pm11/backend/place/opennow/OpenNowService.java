package com.pm11.backend.place.opennow;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.pm11.backend.place.Place;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenNowService {

    static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final ObjectMapper objectMapper;
    private final TodayHolidayService todayHolidayService;
    private final Clock clock;

    public boolean isOpenNow(Place place) {
        ZonedDateTime now = ZonedDateTime.now(clock).withZoneSameInstant(SEOUL);
        HolidaySnapshot holidays = todayHolidayService.today(SEOUL);
        ClosedRulesModel closed = parseClosedRules(place.getClosedRules());
        List<OperatingRuleModel> operating = parseOperatingRules(place.getOperatingRules());
        return OpenNowEvaluator.isOpenNow(closed, operating, now, holidays);
    }

    ClosedRulesModel parseClosedRules(String json) {
        if (json == null || json.isBlank()) {
            return ClosedRulesModel.empty();
        }
        try {
            return objectMapper.readValue(json, ClosedRulesModel.class);
        } catch (Exception e) {
            return ClosedRulesModel.empty();
        }
    }

    List<OperatingRuleModel> parseOperatingRules(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<OperatingRuleModel>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
