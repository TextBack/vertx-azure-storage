package io.textback.azure.storage.blob.util;

import io.textback.azure.storage.blob.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlUtil {


    public static Map<String, List<String>> parseQueryString(String parseString) {
        if (StringUtil.isNullOrEmpty(parseString)) {
            return Collections.emptyMap();
        }
        final Map<String, List<String>> result = new HashMap<>();

        final int queryDex = parseString.indexOf("?");
        if (queryDex >= 0 && parseString.length() > 0) {
            parseString = parseString.substring(queryDex + 1);
        }

        final String[] valuePairs = parseString.contains("&") ? parseString.split("&") : parseString.split(";");

        for (String valuePair : valuePairs) {
            final int equalDex = valuePair.indexOf("=");

            if (equalDex < 0 || equalDex == valuePair.length() - 1) {
                continue;
            }

            String key = valuePair.substring(0, equalDex);
            String value = valuePair.substring(equalDex + 1);

            key = safeDecode(key);
            value = safeDecode(value);

            List<String> values = result.getOrDefault(key, new ArrayList<>());
            if (!value.equals(Constants.EMPTY_STRING)) {
                values.add(value);
                result.putIfAbsent(key, values);
            }
        }

        return result;
    }

    private static String safeDecode(final String stringToDecode) {
        if (stringToDecode == null) {
            return null;
        }

        if (stringToDecode.length() == 0) {
            return Constants.EMPTY_STRING;
        }

        try {
            if (stringToDecode.contains("+")) {
                final StringBuilder outBuilder = new StringBuilder();

                int startDex = 0;
                for (int m = 0; m < stringToDecode.length(); m++) {
                    if (stringToDecode.charAt(m) == '+') {
                        if (m > startDex) {
                            outBuilder.append(URLDecoder.decode(stringToDecode.substring(startDex, m),
                                    Constants.UTF8_CHARSET));
                        }

                        outBuilder.append("+");
                        startDex = m + 1;
                    }
                }

                if (startDex != stringToDecode.length()) {
                    outBuilder.append(URLDecoder.decode(stringToDecode.substring(startDex, stringToDecode.length()),
                            Constants.UTF8_CHARSET));
                }

                return outBuilder.toString();
            } else {
                return URLDecoder.decode(stringToDecode, Constants.UTF8_CHARSET);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
