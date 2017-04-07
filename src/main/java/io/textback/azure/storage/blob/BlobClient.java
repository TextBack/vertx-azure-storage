package io.textback.azure.storage.blob;

import io.textback.azure.storage.blob.impl.BlobClientImpl;
import io.textback.azure.storage.blob.model.DeleteBlobRequest;
import io.textback.azure.storage.blob.model.GetBlobRequest;
import io.textback.azure.storage.blob.model.PutBlobRequest;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientResponse;

@VertxGen
public interface BlobClient {


    static BlobClient blobClient(Vertx vertx, BlobClientOptions options) {
        return new BlobClientImpl(vertx, options);
    }

    void getBlob(GetBlobRequest getBlobRequest,
                 Handler<AsyncResult<HttpClientResponse>> resultHandler);

    void putBlob(PutBlobRequest putBlobRequest,
                 Handler<AsyncResult<HttpClientResponse>> resultHandler);

    void deleteBlob(DeleteBlobRequest deleteBlobRequest,
                    Handler<AsyncResult<HttpClientResponse>> resultHandler);

    void close();
}
