package io.github.ss3rg3.elsa.exceptions;

import org.elasticsearch.ElasticsearchException;

public class ElsaElasticsearchException extends ElsaException {

    public ElsaElasticsearchException(final ElasticsearchException e) {
        super(e);
    }
}
