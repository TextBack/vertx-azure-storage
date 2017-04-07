package io.textback.azure.storage.blob.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.Getter;

@Getter
@DataObject(generateConverter = true)
public class DeleteBlobRequest {

    private String blobName;

    public DeleteBlobRequest() {
    }

    public DeleteBlobRequest(JsonObject config) {

    }

    public DeleteBlobRequest setBlobName(String blobName) {
        this.blobName = blobName;
        return this;
    }
}
