package com.nagarro.driven.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtil {

    private DateUtil() {
        // helper class
    }

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T' HH:mm:ss";

    public static String getCurrentTimeStamp(){
        return DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT).format(LocalDateTime.now());
    }

}
