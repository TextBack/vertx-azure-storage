package io.textback.azure.storage.blob.http.exception;

public class UnsupportedResponseContentType extends RuntimeException {

    public UnsupportedResponseContentType(String contentType) {
        super("Unsupported " + contentType);
    }
}
