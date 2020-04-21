/*
 * Copyright 2018 Sergej Schaefer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

public class BulkProcessorCreator {

    protected static BulkProcessor createBulkProcessor(final RestHighLevelClient client,
                                                       final Listener bulkResponseListener,
                                                       final BulkProcessorConfigurator bulkProcessorConfigurator) {

        final BulkProcessor.Builder bulkProcessorBuilder = BulkProcessor.builder(client::bulkAsync, bulkResponseListener);
        if (bulkProcessorConfigurator != null) {
            return bulkProcessorConfigurator.configure(bulkProcessorBuilder).build();
        }
        return bulkProcessorBuilder.build();
    }

    @FunctionalInterface
    public interface BulkProcessorConfigurator {
        BulkProcessor.Builder configure(BulkProcessor.Builder config);
    }

}
