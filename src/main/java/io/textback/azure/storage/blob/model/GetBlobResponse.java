package io.textback.azure.storage.blob.model;

import io.vertx.core.buffer.Buffer;

public class GetBlobResponse {

    private Buffer body;

    public Buffer getBody() {
        return body;
    }

    public void setBody(Buffer body) {
        this.body = body;
    }
}
