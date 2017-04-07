package io.textback.azure.storage.blob.util;

public class StringUtil {

    public static boolean isNullOrEmpty(final String value) {
        return value == null || value.isEmpty();
    }

    public static String trimStart(final String value) {
        int spaceDex = 0;
        while (spaceDex < value.length() && value.charAt(spaceDex) == ' ') {
            spaceDex++;
        }

        return value.substring(spaceDex);
    }
}
