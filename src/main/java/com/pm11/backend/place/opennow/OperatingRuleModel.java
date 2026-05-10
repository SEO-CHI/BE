package com.pm11.backend.place.opennow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OperatingRuleModel(List<Integer> months, List<Integer> weekdays, String open, String close) {

    public OperatingRuleModel {
        months = months == null ? null : List.copyOf(months);
        weekdays = weekdays == null ? null : List.copyOf(weekdays);
    }
}
