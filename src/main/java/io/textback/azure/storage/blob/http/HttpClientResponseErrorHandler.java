package io.textback.azure.storage.blob.http;


import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientResponse;

public interface HttpClientResponseErrorHandler {

    boolean hasError(ErrorHandlerContext context);

    void handleError(ErrorHandlerContext context, Handler<Throwable> exceptionHandler);

    class ErrorHandlerContext {
        //        private final Buffer body;
        private final HttpClientResponse httpClientResponse;

        public ErrorHandlerContext(/*Buffer body, */HttpClientResponse httpClientResponse) {
//            this.body = body;
            this.httpClientResponse = httpClientResponse;
        }

//        public Buffer getBody() {
//            return body;
//        }

        public HttpClientResponse getHttpClientResponse() {
            return httpClientResponse;
        }
    }
}
