package io.textback.azure.storage.blob;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
@DataObject(generateConverter = true)
public class BlobClientOptions {

    private HttpClientOptions httpClientOptions;

    private String subscriptionId;
    private String containerName;
    private String accountKey;

    public BlobClientOptions() {
        init();
    }

    public BlobClientOptions(String subscriptionId,
                             String containerName,
                             String accountKey) {
        this.subscriptionId = subscriptionId;
        this.containerName = containerName;
        this.accountKey = accountKey;
        init();
    }

    public BlobClientOptions(String subscriptionId,
                             String containerName,
                             String accountKey,
                             HttpClientOptions httpClientOptions) {
        this.subscriptionId = subscriptionId;
        this.containerName = containerName;
        this.accountKey = accountKey;
        this.httpClientOptions = httpClientOptions;
    }

    public BlobClientOptions(JsonObject config) {
        init();
        BlobClientOptionsConverter.fromJson(config, this);
    }

    private void init() {
        httpClientOptions = new HttpClientOptions()
                .setSsl(true)
                .setLogActivity(true);
    }

    public BlobClientOptions setHttpClientOptions(HttpClientOptions httpClientOptions) {
        this.httpClientOptions = httpClientOptions;
        return this;
    }

    public BlobClientOptions setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    public BlobClientOptions setContainerName(String containerName) {
        this.containerName = containerName;
        return this;
    }

    public BlobClientOptions setAccountKey(String accountKey) {
        this.accountKey = accountKey;
        return this;
    }
}
