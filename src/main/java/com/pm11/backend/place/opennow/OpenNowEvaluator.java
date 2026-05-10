package com.pm11.backend.place.opennow;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * operating_rules / closed_rules JSON과 현재 시각·휴일 스냅샷으로 영업 중 여부를 판정한다.
 */
public final class OpenNowEvaluator {

    private static final DateTimeFormatter[] TIME_FORMATTERS = {
        DateTimeFormatter.ISO_LOCAL_TIME,
        DateTimeFormatter.ofPattern("H:mm", Locale.ROOT),
        DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)
    };

    private OpenNowEvaluator() {}

    public static boolean isOpenNow(
            ClosedRulesModel closed,
            List<OperatingRuleModel> operatingRules,
            ZonedDateTime nowInZone,
            HolidaySnapshot holidays) {

        ClosedRulesModel c = closed != null ? closed : ClosedRulesModel.empty();

        if (!Boolean.TRUE.equals(c.alwaysOpen()) && isClosedDay(c, nowInZone, holidays)) {
            return false;
        }

        if (operatingRules == null || operatingRules.isEmpty()) {
            return false;
        }

        OperatingRuleModel rule = firstMatchingRule(operatingRules, nowInZone);
        if (rule == null) {
            return false;
        }

        LocalTime now = nowInZone.toLocalTime();
        LocalTime open = parseTime(rule.open());
        LocalTime close = parseTime(rule.close());
        if (open == null || close == null) {
            return false;
        }

        return isWithinHours(open, close, now);
    }

    /** Step1 휴무일: 하나라도 해당하면 true (영업 아님). always_open은 호출부에서 스킵한다. */
    static boolean isClosedDay(ClosedRulesModel c, ZonedDateTime now, HolidaySnapshot h) {
        LocalDate date = now.toLocalDate();
        int weekday = isoWeekday(now.getDayOfWeek());

        if (c.weeklyClosed().contains(weekday)) {
            return true;
        }

        if (h.publicHoliday() && Boolean.TRUE.equals(c.publicHoliday())) {
            return true;
        }

        if (h.seollalHolidayPeriod()) {
            if ("holiday".equalsIgnoreCase(safe(c.seollal()))) {
                return true;
            }
            if ("day".equalsIgnoreCase(safe(c.seollal())) && h.seollalDay()) {
                return true;
            }
        }

        if (h.chuseokHolidayPeriod()) {
            if ("holiday".equalsIgnoreCase(safe(c.chuseok()))) {
                return true;
            }
            if ("day".equalsIgnoreCase(safe(c.chuseok())) && h.chuseokDay()) {
                return true;
            }
        }

        if (date.getMonthValue() == 1 && date.getDayOfMonth() == 1 && Boolean.TRUE.equals(c.newYear())) {
            return true;
        }

        if (date.getMonthValue() == 12 && date.getDayOfMonth() == 25 && Boolean.TRUE.equals(c.christmas())) {
            return true;
        }

        return false;
    }

    /** ISO: 월=1 … 일=7 */
    static int isoWeekday(DayOfWeek dow) {
        return dow.getValue();
    }

    static OperatingRuleModel firstMatchingRule(List<OperatingRuleModel> rules, ZonedDateTime now) {
        int month = now.getMonthValue();
        int weekday = isoWeekday(now.getDayOfWeek());
        for (OperatingRuleModel r : rules) {
            if (!monthMatches(r.months(), month)) {
                continue;
            }
            if (!weekdayMatches(r.weekdays(), weekday)) {
                continue;
            }
            return r;
        }
        return null;
    }

    static boolean monthMatches(List<Integer> months, int month) {
        if (months == null || months.isEmpty()) {
            return true;
        }
        return months.contains(month);
    }

    static boolean weekdayMatches(List<Integer> weekdays, int weekday) {
        if (weekdays == null || weekdays.isEmpty()) {
            return true;
        }
        return weekdays.contains(weekday);
    }

    /**
     * 같은 날 open~close 이면 open ≤ now ≤ close.
     * 자정을 넘기는 경우(open 이 close 보다 늦은 시각)는 overnight로 처리한다.
     */
    static boolean isWithinHours(LocalTime open, LocalTime close, LocalTime now) {
        if (!close.isBefore(open)) {
            return !now.isBefore(open) && !now.isAfter(close);
        }
        return !now.isBefore(open) || !now.isAfter(close);
    }

    static LocalTime parseTime(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return null;
        }
        for (DateTimeFormatter f : TIME_FORMATTERS) {
            try {
                return LocalTime.parse(s, f);
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }
        return null;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
