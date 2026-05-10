package com.pm11.backend.place.opennow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClosedRulesModel(
        @JsonProperty("always_open") Boolean alwaysOpen,
        @JsonProperty("weekly_closed") List<Integer> weeklyClosed,
        @JsonProperty("public_holiday") Boolean publicHoliday,
        @JsonProperty("seollal") String seollal,
        @JsonProperty("chuseok") String chuseok,
        @JsonProperty("new_year") Boolean newYear,
        @JsonProperty("christmas") Boolean christmas) {

    public ClosedRulesModel {
        weeklyClosed = weeklyClosed == null ? List.of() : List.copyOf(weeklyClosed);
    }

    public static ClosedRulesModel empty() {
        return new ClosedRulesModel(null, List.of(), null, null, null, null, null);
    }
}
