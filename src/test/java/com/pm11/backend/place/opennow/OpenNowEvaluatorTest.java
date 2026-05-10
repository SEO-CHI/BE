package com.pm11.backend.place.opennow;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class OpenNowEvaluatorTest {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    @Test
    void weeklyClosed_closes() {
        ClosedRulesModel closed =
                new ClosedRulesModel(false, List.of(2), false, null, null, false, false); // 화요일 휴무
        ZonedDateTime tuesday = ZonedDateTime.of(2026, 5, 12, 14, 0, 0, 0, SEOUL); // 2026-05-12 화
        assertThat(OpenNowEvaluator.isClosedDay(closed, tuesday, HolidaySnapshot.none())).isTrue();

        ZonedDateTime wed = ZonedDateTime.of(2026, 5, 13, 14, 0, 0, 0, SEOUL);
        assertThat(OpenNowEvaluator.isClosedDay(closed, wed, HolidaySnapshot.none())).isFalse();
    }

    @Test
    void alwaysOpen_skipsClosed_checksStillNeedsOperatingRule() {
        ClosedRulesModel closed = new ClosedRulesModel(true, List.of(2), false, null, null, false, false);
        ZonedDateTime tuesdayNoon = ZonedDateTime.of(2026, 5, 12, 12, 0, 0, 0, SEOUL);
        assertThat(OpenNowEvaluator.isoWeekday(tuesdayNoon.getDayOfWeek())).isEqualTo(2);
        assertThat(OpenNowEvaluator.isClosedDay(closed, tuesdayNoon, HolidaySnapshot.none())).isTrue();

        List<OperatingRuleModel> rules =
                List.of(new OperatingRuleModel(null, null, "09:00", "18:00"));
        assertThat(OpenNowEvaluator.isOpenNow(closed, rules, tuesdayNoon, HolidaySnapshot.none())).isTrue();
    }

    @Test
    void publicHoliday_flag_closes() {
        ClosedRulesModel closed = new ClosedRulesModel(false, List.of(), true, null, null, false, false);
        HolidaySnapshot h = new HolidaySnapshot(true, false, false, false, false);
        ZonedDateTime now = ZonedDateTime.of(2026, 5, 5, 12, 0, 0, 0, SEOUL);
        assertThat(OpenNowEvaluator.isOpenNow(closed, List.of(new OperatingRuleModel(null, null, "09:00", "23:00")), now, h))
                .isFalse();
    }

    @Test
    void seollalHoliday_mode_closesEntirePeriod() {
        ClosedRulesModel closed = new ClosedRulesModel(false, List.of(), false, "holiday", null, false, false);
        HolidaySnapshot h = new HolidaySnapshot(false, true, false, false, false);
        ZonedDateTime now = ZonedDateTime.of(2026, 2, 18, 12, 0, 0, 0, SEOUL);
        assertThat(OpenNowEvaluator.isOpenNow(
                        closed, List.of(new OperatingRuleModel(null, null, "09:00", "23:00")), now, h))
                .isFalse();
    }

    @Test
    void seollalDay_mode_closesOnlyOnDay() {
        ClosedRulesModel closed = new ClosedRulesModel(false, List.of(), false, "day", null, false, false);
        HolidaySnapshot onDay = new HolidaySnapshot(false, true, true, false, false);
        ZonedDateTime now = ZonedDateTime.of(2026, 2, 19, 12, 0, 0, 0, SEOUL);
        assertThat(OpenNowEvaluator.isOpenNow(
                        closed, List.of(new OperatingRuleModel(null, null, "09:00", "23:00")), now, onDay))
                .isFalse();

        HolidaySnapshot eve = new HolidaySnapshot(false, true, false, false, false);
        assertThat(OpenNowEvaluator.isOpenNow(
                        closed, List.of(new OperatingRuleModel(null, null, "09:00", "23:00")), now, eve))
                .isTrue();
    }

    @Test
    void newYear_and_christmas() {
        ClosedRulesModel closed = new ClosedRulesModel(false, List.of(), false, null, null, true, true);
        ZonedDateTime ny = ZonedDateTime.of(2026, 1, 1, 15, 0, 0, 0, SEOUL);
        assertThat(OpenNowEvaluator.isOpenNow(
                        closed, List.of(new OperatingRuleModel(null, null, "09:00", "23:00")), ny, HolidaySnapshot.none()))
                .isFalse();

        ZonedDateTime xm = ZonedDateTime.of(2026, 12, 25, 15, 0, 0, 0, SEOUL);
        assertThat(OpenNowEvaluator.isOpenNow(
                        closed, List.of(new OperatingRuleModel(null, null, "09:00", "23:00")), xm, HolidaySnapshot.none()))
                .isFalse();
    }

    @Test
    void firstMatchingRule_wins_and_timeWindow() {
        List<OperatingRuleModel> rules = List.of(
                new OperatingRuleModel(List.of(6), null, "10:00", "12:00"),
                new OperatingRuleModel(null, null, "09:00", "18:00"));
        ZonedDateTime juneNoon = ZonedDateTime.of(2026, 6, 15, 11, 0, 0, 0, SEOUL);
        assertThat(OpenNowEvaluator.isOpenNow(
                        ClosedRulesModel.empty(), rules, juneNoon, HolidaySnapshot.none()))
                .isTrue();

        ZonedDateTime juneAfternoon = ZonedDateTime.of(2026, 6, 15, 14, 0, 0, 0, SEOUL);
        assertThat(OpenNowEvaluator.isOpenNow(
                        ClosedRulesModel.empty(), rules, juneAfternoon, HolidaySnapshot.none()))
                .isFalse();

        ZonedDateTime marchAfternoon = ZonedDateTime.of(2026, 3, 15, 14, 0, 0, 0, SEOUL);
        assertThat(OpenNowEvaluator.isOpenNow(
                        ClosedRulesModel.empty(), rules, marchAfternoon, HolidaySnapshot.none()))
                .isTrue();

        ZonedDateTime juneNight = ZonedDateTime.of(2026, 6, 15, 20, 0, 0, 0, SEOUL);
        assertThat(OpenNowEvaluator.isOpenNow(
                        ClosedRulesModel.empty(), rules, juneNight, HolidaySnapshot.none()))
                .isFalse();
    }

    @Test
    void overnight_hours() {
        List<OperatingRuleModel> rules = List.of(new OperatingRuleModel(null, null, "22:00", "02:00"));
        ZonedDateTime late = ZonedDateTime.of(2026, 6, 15, 23, 30, 0, 0, SEOUL);
        assertThat(OpenNowEvaluator.isOpenNow(
                        ClosedRulesModel.empty(), rules, late, HolidaySnapshot.none()))
                .isTrue();

        ZonedDateTime early = ZonedDateTime.of(2026, 6, 15, 1, 30, 0, 0, SEOUL);
        assertThat(OpenNowEvaluator.isOpenNow(
                        ClosedRulesModel.empty(), rules, early, HolidaySnapshot.none()))
                .isTrue();

        ZonedDateTime noon = ZonedDateTime.of(2026, 6, 15, 12, 0, 0, 0, SEOUL);
        assertThat(OpenNowEvaluator.isOpenNow(
                        ClosedRulesModel.empty(), rules, noon, HolidaySnapshot.none()))
                .isFalse();
    }

    @Test
    void noOperatingRules_returnsFalse() {
        assertThat(OpenNowEvaluator.isOpenNow(
                        ClosedRulesModel.empty(), List.of(), ZonedDateTime.now(SEOUL), HolidaySnapshot.none()))
                .isFalse();
    }

    @Test
    void parseTime_acceptsHm() {
        assertThat(OpenNowEvaluator.parseTime("9:30")).isEqualTo(LocalTime.of(9, 30));
        assertThat(OpenNowEvaluator.parseTime("09:30:00")).isEqualTo(LocalTime.of(9, 30));
    }

    @Test
    void inclusive_close_boundary() {
        LocalTime open = LocalTime.of(9, 0);
        LocalTime close = LocalTime.of(18, 0);
        assertThat(OpenNowEvaluator.isWithinHours(open, close, LocalTime.of(18, 0))).isTrue();
        assertThat(OpenNowEvaluator.isWithinHours(open, close, LocalTime.of(9, 0))).isTrue();
    }
}
