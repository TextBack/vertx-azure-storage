package io.textback.azure.storage.blob.http.exception;

import io.textback.azure.storage.blob.model.ErrorResponse;
import io.vertx.core.MultiMap;

public class HttpClientResponseException extends RuntimeException {

    private final int statusCode;
    private final String statusMessage;
    private final ErrorResponse errorResponse;
    private final MultiMap headers;

    public HttpClientResponseException(int statusCode, String statusMessage, ErrorResponse errorResponse, MultiMap headers) {
        super(statusMessage);
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.errorResponse = errorResponse;
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public MultiMap getHeaders() {
        return headers;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }


    @Override
    public String getMessage() {
        return getErrorResponse().getMessage() + "\n" + getErrorResponse().getAuthenticationErrorDetail();
    }
}
