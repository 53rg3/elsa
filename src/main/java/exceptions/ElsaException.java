package exceptions;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;

public class ElsaException extends Exception {

    private final RestStatus restStatus;

    public ElsaException(final ElasticsearchException e) {
        super(e);
        this.restStatus = e.status();
    }

    public ElsaException(final IOException e) {
        super(e);

        if(e.getMessage().contains("Connection refused")) {
            this.restStatus = RestStatus.SERVICE_UNAVAILABLE;
        } else {
            this.restStatus = RestStatus.NOT_IMPLEMENTED;
        }
    }

    public RestStatus getRestStatus() {
        return this.restStatus;
    }

    public static ElsaExceptionType instanceOf(final ElsaException e) {
        if(e instanceof ElsaIOException) {
            return ElsaExceptionType.IO_EXCEPTION;
        } else if(e instanceof ElsaElasticsearchException) {
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
}
