package io.github.ss3rg3.elsa.exceptions;

import org.elasticsearch.ElasticsearchException;
import io.github.ss3rg3.elsa.statics.ElsaStatics;

import java.io.IOException;
import java.util.regex.Matcher;

public class ElsaException extends Exception {

    private final int httpStatus;

    public ElsaException(final ElasticsearchException e) {
        super(e);
        this.httpStatus = e.status().getStatus();
    }

    public ElsaException(final IOException e) {
        super(e);

        final Matcher matcher = ElsaStatics.jsonExtractorPattern.matcher(e.getMessage());
        if (matcher.find()) {
            final JsonInExceptionAsPojo pojo = ElsaStatics.GSON.fromJson(matcher.group(0), JsonInExceptionAsPojo.class);
            this.httpStatus = pojo.status;
        } else if (e.getMessage().contains("Connection refused")) {
            this.httpStatus = 503;
        } else {
            this.httpStatus = 0; // DUMMY, meaning that this library didn't implement handling it
        }
    }

    public int getHttpStatus() {
        return this.httpStatus;
    }

    public static ElsaExceptionType instanceOf(final ElsaException e) {
        if (e instanceof ElsaIOException) {
            return ElsaExceptionType.IO_EXCEPTION;
        } else if (e instanceof ElsaElasticsearchException) {
            return ElsaExceptionType.ELASTICSEARCH_EXCEPTION;
        } else {
            return ElsaExceptionType.UNKNOWN_EXCEPTION;
        }
    }

    public enum ElsaExceptionType {
        ELASTICSEARCH_EXCEPTION,
        IO_EXCEPTION,
        UNKNOWN_EXCEPTION
    }

    private static class JsonInExceptionAsPojo {
        private final Integer status;
        private final Error error;

        public JsonInExceptionAsPojo(final Integer status, final Error error) {
            this.status = status;
            this.error = error;
        }
    }
}
