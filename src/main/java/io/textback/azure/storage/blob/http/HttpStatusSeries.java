package io.textback.azure.storage.blob.http;


import java.util.Arrays;
import java.util.Optional;

public enum HttpStatusSeries {
    INFORMATIONAL(1),
    SUCCESSFUL(2),
    REDIRECTION(3),
    CLIENT_ERROR(4),
    SERVER_ERROR(5);


    private final int value;

    HttpStatusSeries(int value) {
        this.value = value;
    }

    public static Optional<HttpStatusSeries> valueOf(final int status) {
        final int seriesCode = status / 100;
        return Arrays.stream(values())
                .filter(item -> item.value == seriesCode)
                .findFirst();
    }

    public int getValue() {
        return value;
    }
}
