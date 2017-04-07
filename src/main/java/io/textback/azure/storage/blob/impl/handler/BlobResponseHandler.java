package io.textback.azure.storage.blob.impl.handler;

import io.textback.azure.storage.blob.http.HttpClientResponseErrorHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientResponse;

public class BlobResponseHandler implements Handler<HttpClientResponse> {

    private final HttpClientResponseErrorHandler httpClientResponseErrorHandler;
    private final Handler<AsyncResult<HttpClientResponse>> resultHandler;

    public BlobResponseHandler(HttpClientResponseErrorHandler httpClientResponseErrorHandler,
                                    Handler<AsyncResult<HttpClientResponse>> resultHandler) {
        this.httpClientResponseErrorHandler = httpClientResponseErrorHandler;
        this.resultHandler = resultHandler;
    }

    @Override
    public void handle(HttpClientResponse responseHandler) {
        HttpClientResponseErrorHandler.ErrorHandlerContext errorHandlerContext = new HttpClientResponseErrorHandler.ErrorHandlerContext(responseHandler);
        if (httpClientResponseErrorHandler.hasError(errorHandlerContext)) {
            httpClientResponseErrorHandler.handleError(errorHandlerContext, e -> resultHandler.handle(Future.failedFuture(e)));
        } else {
            resultHandler.handle(Future.succeededFuture(responseHandler));
        }
    }
}