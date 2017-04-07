package io.textback.azure.storage.blob.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.Getter;

@Getter
@DataObject(generateConverter = true)
public class GetBlobRequest {
    private String blobName;

    public GetBlobRequest() {}

    public GetBlobRequest(JsonObject config) {

    }

    public GetBlobRequest setBlobName(String blobName) {
        this.blobName = blobName;
        return this;
    }
}
