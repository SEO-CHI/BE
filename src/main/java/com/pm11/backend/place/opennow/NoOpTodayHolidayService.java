package com.pm11.backend.place.opennow;

import java.time.ZoneId;
import org.springframework.stereotype.Service;

@Service
public class NoOpTodayHolidayService implements TodayHolidayService {

    @Override
    public HolidaySnapshot today(ZoneId zone) {
        return HolidaySnapshot.none();
    }
}
