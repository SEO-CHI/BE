package com.pm11.backend.place.opennow;

import java.time.ZoneId;

/** 공휴일 API 연동 지점. zone 기준 “오늘”의 휴일 플래그를 채운다. */
public interface TodayHolidayService {

    HolidaySnapshot today(ZoneId zone);
}
