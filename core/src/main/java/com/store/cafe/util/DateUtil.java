package com.store.cafe.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateUtil {

    private static final ZoneId ZONE_SEOUL = ZoneId.of("Asia/Seoul");

    public static ZonedDateTime now() {
        return ZonedDateTime.now(ZONE_SEOUL);
    }
}
