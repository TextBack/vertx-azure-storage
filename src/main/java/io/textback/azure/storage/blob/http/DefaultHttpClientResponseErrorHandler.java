package io.textback.azure.storage.blob.http;


import io.textback.azure.storage.blob.http.exception.HttpClientResponseException;
import io.textback.azure.storage.blob.http.exception.UnsupportedResponseContentType;
import io.textback.azure.storage.blob.model.ErrorResponse;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import static io.textback.azure.storage.blob.util.XmlUtil.convertToSaxSource;
import static java.text.MessageFormat.format;

public class DefaultHttpClientResponseErrorHandler implements HttpClientResponseErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultHttpClientResponseErrorHandler.class);

    private final Unmarshaller jaxbUnmarshaller;

    public DefaultHttpClientResponseErrorHandler(Unmarshaller jaxbUnmarshaller) {
        this.jaxbUnmarshaller = jaxbUnmarshaller;
    }

    @Override
    public boolean hasError(final ErrorHandlerContext context) {
        final HttpClientResponse response = context.getHttpClientResponse();
        HttpStatusSeries httpStatusSeries = httpStatusSeries(response);
        return (httpStatusSeries == HttpStatusSeries.CLIENT_ERROR || httpStatusSeries == HttpStatusSeries.SERVER_ERROR);
    }

    @Override
    public void handleError(final ErrorHandlerContext context, Handler<Throwable> exceptionHandler) {
        final HttpClientResponse response = context.getHttpClientResponse();
        final HttpStatusSeries httpStatusSeries = httpStatusSeries(response);
        String contentTypeHeader = response.getHeader("Content-Type");
        if ("application/xml".equals(contentTypeHeader)) {
            response.bodyHandler(body -> {
                ErrorResponse errorMessage = tryParseBody(body.toString());
                switch (httpStatusSeries) {
                    case CLIENT_ERROR:
                    case SERVER_ERROR: {
                        exceptionHandler.handle(new HttpClientResponseException(response.statusCode(), response.statusMessage(), errorMessage, response.headers()));
                        break;
                    }
                    default: {
                        exceptionHandler.handle(new IllegalArgumentException(format("Unrecognizable HTTP status code ''{0}''", httpStatusSeries.getValue())));
                        break;
                    }
                }
            });
        } else {
            exceptionHandler.handle(new UnsupportedResponseContentType(contentTypeHeader));
        }
    }

    private ErrorResponse tryParseBody(String body) {
        try {
            JAXBElement<ErrorResponse> element = jaxbUnmarshaller.unmarshal(convertToSaxSource(body.getBytes("UTF-8")), ErrorResponse.class);
            return element.getValue();
        } catch (Exception e) {
            LOG.info("", e);
            return null;
        }
    }

    private static HttpStatusSeries httpStatusSeries(final HttpClientResponse response) {
        final int statusCode = response.statusCode();
        return HttpStatusSeries.valueOf(statusCode)
                .orElseThrow(() -> new IllegalArgumentException(format("Unrecognizable HTTP status code ''{0}''", statusCode)));
    }


}
