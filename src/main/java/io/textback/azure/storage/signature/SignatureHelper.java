package io.textback.azure.storage.signature;

import io.textback.azure.storage.blob.Constants;
import io.textback.azure.storage.blob.util.UrlUtil;
import io.vertx.core.MultiMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.text.MessageFormat.format;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

public class SignatureHelper {

    private byte[] payload;
    private MultiMap headers;
    private String method;
    private String subscriptionId;
    private String path;
    private String query;
    private StringBuilder token;

    public SignatureHelper() {
        token = new StringBuilder(300);
    }

    public SignatureHelper setPayload(byte[] payload) {
        this.payload = payload;
        return this;
    }

    public SignatureHelper setHeaders(MultiMap headers) {
        this.headers = headers;
        return this;
    }

    public SignatureHelper setMethod(String method) {
        this.method = method;
        return this;
    }

    public SignatureHelper setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    public SignatureHelper setPath(String path) {
        this.path = path;
        return this;
    }

    public SignatureHelper setQuery(String query) {
        this.query = query;
        return this;
    }

    public String authorizationToken(String accountKey) throws InvalidKeyException {
        final String stringToSign = prepareSignatureString();
        byte[] decodedAccountKey = Base64.getDecoder().decode(accountKey);
        final String base64Signature = Base64.getEncoder().encodeToString(hmacSha256(decodedAccountKey, stringToSign));
        return format("{0} {1}:{2}", "SharedKey", subscriptionId, base64Signature);
    }

    private String prepareSignatureString() {
        token.append(method);

        token.append("\n").append(ofNullable(headers.get("Content-Encoding"))
                .orElse(Constants.EMPTY_STRING));
        token.append("\n").append(ofNullable(headers.get("Content-Language"))
                .orElse(Constants.EMPTY_STRING));
        token.append("\n").append(payload.length <= 0 ? Constants.EMPTY_STRING : String.valueOf(payload.length));
        token.append("\n").append(ofNullable(headers.get("Content-MD5"))
                .orElse(Constants.EMPTY_STRING));
        token.append("\n").append(ofNullable((headers.get("Content-Type")))
                .orElse(Constants.EMPTY_STRING));
        // x-ms-date always presents so, Date should be empty string
        token.append("\n").append(Constants.EMPTY_STRING);
        token.append("\n").append(ofNullable(headers.get("If-Modified-Since"))
                .orElse(Constants.EMPTY_STRING));
        token.append("\n").append(ofNullable(headers.get("If-Match"))
                .orElse(Constants.EMPTY_STRING));
        token.append("\n").append(ofNullable(headers.get("If-None-Match"))
                .orElse(Constants.EMPTY_STRING));
        token.append("\n").append(ofNullable(headers.get("If-Modified-Since"))
                .orElse(Constants.EMPTY_STRING));
        token.append("\n").append(ofNullable(headers.get("Range"))
                .orElse(Constants.EMPTY_STRING));

        String azureHeaders = prepareAzureHeaders();
        if (!azureHeaders.isEmpty()) {
            token.append("\n").append(azureHeaders);
        }

        token.append("\n").append(prepareResource());

        return token.toString();
    }


    private String prepareAzureHeaders() {
        final List<String> azureHeaderNames = new ArrayList<>();

        for (final String key : headers.names()) {
            if (key.toLowerCase(Locale.US).startsWith("x-ms-")) {
                azureHeaderNames.add(key.toLowerCase(Locale.US));
            }
        }
        Collections.sort(azureHeaderNames);
        List<String> resultHeaders = new ArrayList<>();
        for (final String key : azureHeaderNames) {
            final List<String> values = headers.getAll(key);

            String res = ofNullable(values)
                    .orElse(emptyList())
                    .stream()
                    .map(value -> value.replace("\r\n", Constants.EMPTY_STRING))
                    .collect(joining(","));
            if (!res.isEmpty()) {
                resultHeaders.add(key + ":" + res);
            }
        }
        return resultHeaders.stream()
                .collect(joining("\n"));
    }

    private String prepareResource() {
        StringBuilder resourcePath = new StringBuilder("/" + subscriptionId + path);

        final Map<String, List<String>> queryVariables = UrlUtil.parseQueryString(query);
        final Map<String, String> lowercasedKeyNameValue = new HashMap<>();

        for (final Map.Entry<String, List<String>> entry : queryVariables.entrySet()) {
            final List<String> sortedValues = entry.getValue();
            Collections.sort(sortedValues);

            lowercasedKeyNameValue.put(
                    entry.getKey() == null ? null : entry.getKey().toLowerCase(Locale.US),
                    sortedValues.stream()
                            .collect(joining(","))
            );
        }

        final List<String> sortedKeys = new ArrayList<>(lowercasedKeyNameValue.keySet());

        Collections.sort(sortedKeys);

        for (final String key : sortedKeys) {
            String queryParamString = key + ":" + lowercasedKeyNameValue.get(key);
            resourcePath.append("\n").append(queryParamString);
        }

        return resourcePath.toString();
    }

    private static byte[] hmacSha256(byte[] accountKey, final String value) throws InvalidKeyException {
        Mac hmacSha256;
        try {
            hmacSha256 = Mac.getInstance("HmacSHA256");
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException();
        }
        hmacSha256.init(new SecretKeySpec(accountKey, "HmacSHA256"));
        byte[] utf8Bytes;
        try {
            utf8Bytes = value.getBytes(Constants.UTF8_CHARSET);
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        return hmacSha256.doFinal(utf8Bytes);
    }


}
