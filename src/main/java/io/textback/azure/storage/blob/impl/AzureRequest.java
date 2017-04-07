package io.textback.azure.storage.blob.impl;

import io.textback.azure.storage.blob.util.DateTimeUtil;
import io.textback.azure.storage.signature.SignatureHelper;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpVersion;

import java.security.InvalidKeyException;
import java.time.Clock;
import java.time.ZonedDateTime;

import static io.netty.handler.codec.http.HttpHeaders.Names.AUTHORIZATION;
import static io.textback.azure.storage.blob.http.HttpHeaders.VERSION;
import static io.textback.azure.storage.blob.http.HttpHeaders.X_MS_DATE;

class AzureRequest implements HttpClientRequest {

    private static final String MS_API_VERSION = "2016-05-31";

    private final HttpClientRequest httpClientRequest;
    private final String subscriptionId;
    private final String accountKey;
    private final Clock clock;

    private AzureRequest(HttpClientRequest httpClientRequest,
                         String subscriptionId,
                         String accountKey,
                         Clock clock) {
        this.httpClientRequest = httpClientRequest;
        this.subscriptionId = subscriptionId;
        this.accountKey = accountKey;
        this.clock = clock;
    }


    static AzureRequest create(HttpClientRequest request,
                               String subscriptionId,
                               String accountKey) {
        return new AzureRequest(request, subscriptionId, accountKey, Clock.systemDefaultZone());
    }

    @Override
    public AzureRequest exceptionHandler(Handler<Throwable> handler) {
        httpClientRequest.exceptionHandler(handler);
        return this;
    }

    @Override
    public AzureRequest write(Buffer data) {
        httpClientRequest.write(data);
        return this;
    }

    @Override
    public AzureRequest setWriteQueueMaxSize(int maxSize) {
        httpClientRequest.setWriteQueueMaxSize(maxSize);
        return this;
    }

    @Override
    public boolean writeQueueFull() {
        return httpClientRequest.writeQueueFull();
    }

    @Override
    public AzureRequest drainHandler(Handler<Void> handler) {
        httpClientRequest.drainHandler(handler);
        return this;
    }

    @Override
    public AzureRequest handler(Handler<HttpClientResponse> handler) {
        httpClientRequest.handler(handler);
        return this;
    }

    @Override
    public AzureRequest pause() {
        httpClientRequest.pause();
        return this;
    }

    @Override
    public AzureRequest resume() {
        httpClientRequest.resume();
        return this;
    }

    @Override
    public AzureRequest endHandler(Handler<Void> endHandler) {
        httpClientRequest.endHandler(endHandler);
        return this;
    }

    @Override
    public AzureRequest setFollowRedirects(boolean followRedirects) {
        httpClientRequest.setFollowRedirects(followRedirects);
        return this;
    }

    @Override
    public AzureRequest setChunked(boolean chunked) {
        httpClientRequest.setChunked(true);
        return this;
    }

    @Override
    public boolean isChunked() {
        return httpClientRequest.isChunked();
    }

    @Override
    public HttpMethod method() {
        return httpClientRequest.method();
    }

    @Override
    public String getRawMethod() {
        return httpClientRequest.getRawMethod();
    }

    @Override
    public AzureRequest setRawMethod(String method) {
        httpClientRequest.setRawMethod(method);
        return this;
    }

    @Override
    public String absoluteURI() {
        return httpClientRequest.absoluteURI();
    }

    @Override
    public String uri() {
        return httpClientRequest.uri();
    }

    @Override
    public String path() {
        return httpClientRequest.path();
    }

    @Override
    public String query() {
        return httpClientRequest.query();
    }

    @Override
    public AzureRequest setHost(String host) {
        httpClientRequest.setHost(host);
        return this;
    }

    @Override
    public String getHost() {
        return httpClientRequest.getHost();
    }

    @Override
    public MultiMap headers() {
        return httpClientRequest.headers();
    }

    @Override
    public AzureRequest putHeader(String name, String value) {
        httpClientRequest.putHeader(name, value);
        return this;
    }

    @Override
    public AzureRequest putHeader(CharSequence name, CharSequence value) {
        httpClientRequest.putHeader(name, value);
        return this;
    }

    @Override
    public AzureRequest putHeader(String name, Iterable<String> values) {
        httpClientRequest.putHeader(name, values);
        return this;
    }

    @Override
    public AzureRequest putHeader(CharSequence name, Iterable<CharSequence> values) {
        httpClientRequest.putHeader(name, values);
        return this;
    }

    @Override
    public AzureRequest write(String chunk) {
        httpClientRequest.write(chunk);
        return this;
    }

    @Override
    public AzureRequest write(String chunk, String enc) {
        httpClientRequest.write(chunk, enc);
        return this;
    }

    @Override
    public AzureRequest continueHandler(@Nullable Handler<Void> handler) {
        httpClientRequest.continueHandler(handler);
        return this;
    }

    @Override
    public AzureRequest sendHead() {
        httpClientRequest.sendHead();
        return this;
    }

    @Override
    public AzureRequest sendHead(Handler<HttpVersion> completionHandler) {
        httpClientRequest.sendHead(completionHandler);
        return this;
    }

    @Override
    public void end(String chunk) {
        initAzureHeaders(Buffer.buffer(chunk));
        httpClientRequest.end(chunk);
    }

    @Override
    public void end(String chunk, String enc) {
        initAzureHeaders(Buffer.buffer(chunk, enc));
        httpClientRequest.end(chunk, enc);
    }

    @Override
    public void end(Buffer chunk) {
        initAzureHeaders(chunk);
        httpClientRequest.end(chunk);
    }

    @Override
    public void end() {
        initAzureHeaders(Buffer.buffer());
        httpClientRequest.end();
    }

    @Override
    public AzureRequest setTimeout(long timeoutMs) {
        httpClientRequest.setTimeout(timeoutMs);
        return this;
    }

    @Override
    public AzureRequest pushHandler(Handler<HttpClientRequest> handler) {
        httpClientRequest.pushHandler(handler);
        return this;
    }

    @Override
    public boolean reset(long code) {
        return httpClientRequest.reset(code);
    }

    @Override
    public HttpConnection connection() {
        return httpClientRequest.connection();
    }

    @Override
    public AzureRequest connectionHandler(@Nullable Handler<HttpConnection> handler) {
        httpClientRequest.connectionHandler(handler);
        return this;
    }

    @Override
    public AzureRequest writeCustomFrame(int type, int flags, Buffer payload) {
        httpClientRequest.writeCustomFrame(type, flags, payload);
        return this;
    }

    private void initAzureHeaders(Buffer buffer) {
        ZonedDateTime dateTime = ZonedDateTime.now(clock);
        SignatureHelper signatureHelper = new SignatureHelper()
                .setHeaders(headers())
                .setMethod(method().name())
                .setSubscriptionId(subscriptionId)
                .setPath(path())
                .setQuery(query())
                .setPayload(buffer.getBytes());

        putHeader(VERSION, MS_API_VERSION);
        putHeader(X_MS_DATE, DateTimeUtil.formattedDate(dateTime));
        try {
            putHeader(AUTHORIZATION, signatureHelper.authorizationToken(accountKey));
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
