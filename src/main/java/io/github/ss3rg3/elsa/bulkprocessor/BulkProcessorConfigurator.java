package io.github.ss3rg3.elsa.bulkprocessor;

import org.elasticsearch.action.bulk.BulkProcessor;

@FunctionalInterface
public interface BulkProcessorConfigurator {
    BulkProcessor.Builder configure(BulkProcessor.Builder config);
}
