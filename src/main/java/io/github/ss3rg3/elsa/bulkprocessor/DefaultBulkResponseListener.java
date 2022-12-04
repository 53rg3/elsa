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

package io.github.ss3rg3.elsa.bulkprocessor;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBulkResponseListener implements BulkProcessor.Listener {

    private static final Logger logger = LoggerFactory.getLogger(DefaultBulkResponseListener.class);

    @Override
    public void beforeBulk(final long executionId, final BulkRequest request) {
        // NO OP
    }

    @Override
    public void afterBulk(final long executionId, final BulkRequest request, final BulkResponse response) {
        // NO OP
    }

    @Override
    public void afterBulk(final long executionId, final BulkRequest request, final Throwable failure) {
        logger.error("Error executing BulkRequest with ID: "+executionId, failure);
    }
}
