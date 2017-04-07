package io.textback.azure.storage.blob.impl;

import io.textback.azure.storage.blob.BlobClient;
import io.textback.azure.storage.blob.BlobClientOptions;
import io.textback.azure.storage.blob.http.DefaultHttpClientResponseErrorHandler;
import io.textback.azure.storage.blob.http.HttpClientResponseErrorHandler;
import io.textback.azure.storage.blob.impl.handler.BlobResponseHandler;
import io.textback.azure.storage.blob.model.DeleteBlobRequest;
import io.textback.azure.storage.blob.model.GetBlobRequest;
import io.textback.azure.storage.blob.model.PutBlobRequest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;

import static io.textback.azure.storage.blob.http.HttpHeaders.BLOB_TYPE;
import static io.textback.azure.storage.blob.impl.BlobType.BLOCK_BLOB;
import static io.textback.azure.storage.blob.util.XmlUtil.createJaxbUnmarshaller;
import static io.vertx.core.Future.failedFuture;
import static java.text.MessageFormat.format;

public class BlobClientImpl implements BlobClient {

    private static final String HOSTNAME_PATTERN = "{0}.blob.core.windows.net";


    private final HttpClient httpClient;
    private final String subscriptionId;
    private final String containerName;
    private final String hostname;
    private final String accountKey;
    private final HttpClientResponseErrorHandler httpClientResponseErrorHandler;

    public BlobClientImpl(Vertx vertx,
                          BlobClientOptions options) {
        this(vertx, options, new DefaultHttpClientResponseErrorHandler(createJaxbUnmarshaller()));
    }

    public BlobClientImpl(Vertx vertx,
                          BlobClientOptions options,
                          HttpClientResponseErrorHandler httpClientResponseErrorHandler) {
        httpClient = vertx.createHttpClient(options.getHttpClientOptions());

        subscriptionId = options.getSubscriptionId();
        containerName = options.getContainerName();
        accountKey = options.getAccountKey();
        hostname = format(HOSTNAME_PATTERN, subscriptionId);

        this.httpClientResponseErrorHandler = httpClientResponseErrorHandler;
    }


    @Override
    public void getBlob(GetBlobRequest getBlobRequest,
                        Handler<AsyncResult<HttpClientResponse>> resultHandler) {
        final String url = "https://" + hostname + "/" + containerName + "/" + getBlobRequest.getBlobName();

        HttpClientRequest httpRequest = httpClient.requestAbs(HttpMethod.GET, url);
        final HttpClientRequest azureRequest = AzureRequest.create(httpRequest, subscriptionId, accountKey)
                .handler(new BlobResponseHandler(httpClientResponseErrorHandler, resultHandler))
                .exceptionHandler(e -> resultHandler.handle(failedFuture(e)));

        try {
            azureRequest.end();
        } catch (RuntimeException e) {
            resultHandler.handle(failedFuture(e));
        }
    }


    @Override
    public void putBlob(PutBlobRequest putBlobRequest,
                        Handler<AsyncResult<HttpClientResponse>> resultHandler) {
        final String url = "https://" + hostname + "/" + containerName + "/" + putBlobRequest.getBlobName();

        HttpClientRequest httpRequest = httpClient.requestAbs(HttpMethod.PUT, url);
        final HttpClientRequest azureRequest = AzureRequest.create(httpRequest, subscriptionId, accountKey)
                .handler(new BlobResponseHandler(httpClientResponseErrorHandler, resultHandler))
                .exceptionHandler(e -> resultHandler.handle(failedFuture(e)));

        azureRequest.putHeader(BLOB_TYPE, BLOCK_BLOB.getValue());

        try {
            azureRequest.end(putBlobRequest.getData());
        } catch (RuntimeException e) {
            resultHandler.handle(failedFuture(e));
        }
    }

    @Override
    public void deleteBlob(DeleteBlobRequest deleteBlobRequest,
                           Handler<AsyncResult<HttpClientResponse>> resultHandler) {
        final String url = "https://" + hostname + "/" + containerName + "/" + deleteBlobRequest.getBlobName();

        HttpClientRequest httpRequest = httpClient.requestAbs(HttpMethod.DELETE, url);
        final HttpClientRequest azureRequest = AzureRequest.create(httpRequest, subscriptionId, accountKey)
                .exceptionHandler(e -> resultHandler.handle(failedFuture(e)))
                .handler(new BlobResponseHandler(httpClientResponseErrorHandler, resultHandler));


        try {
            azureRequest.end();
        } catch (RuntimeException e) {
            resultHandler.handle(failedFuture(e));
        }
    }


    @Override
    public void close() {
        httpClient.close();
    }


}