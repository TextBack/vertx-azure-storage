package io.textback.azure.storage.blob.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final String DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern(DATE_PATTERN)
            .withZone(ZoneId.of("GMT"));


    public static String formattedDate(ZonedDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }

}
