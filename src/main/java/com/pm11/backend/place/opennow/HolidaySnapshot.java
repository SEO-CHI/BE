package com.pm11.backend.place.opennow;

/**
 * 공휴일 API 등 외부 소스에서 조회한 “오늘” 기준 휴일 정보.
 * 미연동 시 {@link TodayHolidayService} 기본 구현은 모두 false.
 */
public record HolidaySnapshot(
        boolean publicHoliday,
        boolean seollalHolidayPeriod,
        boolean seollalDay,
        boolean chuseokHolidayPeriod,
        boolean chuseokDay) {

    public static HolidaySnapshot none() {
        return new HolidaySnapshot(false, false, false, false, false);
    }
}
