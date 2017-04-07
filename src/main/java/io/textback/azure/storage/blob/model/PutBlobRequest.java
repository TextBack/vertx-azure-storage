package io.textback.azure.storage.blob.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import lombok.Getter;

@Getter
@DataObject(generateConverter = true)
public class PutBlobRequest {

    private Buffer data;
    private String blobName;

    public PutBlobRequest() {}

    public PutBlobRequest(JsonObject config) {

    }

    public PutBlobRequest setData(Buffer data) {
        this.data = data;
        return this;
    }

    public PutBlobRequest setBlobName(String blobName) {
        this.blobName = blobName;
        return this;
    }
}
