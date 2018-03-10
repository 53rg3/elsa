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

package assets;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class TestBulkResponseListener implements BulkProcessor.Listener {

    private static final Logger logger = LoggerFactory.getLogger(TestBulkResponseListener.class);
    private final int bulkSize;
    private final AtomicInteger totalRequestCount;
    private final AtomicInteger totalResponseCount;

    public TestBulkResponseListener(int bulkSize, AtomicInteger totalRequestCount, AtomicInteger totalResponseCount) {
        this.bulkSize = bulkSize;
        this.totalRequestCount = totalRequestCount;
        this.totalResponseCount = totalResponseCount;
    }

    @Override
    public void beforeBulk(final long executionId, final BulkRequest request) {
        totalRequestCount.addAndGet(request.requests().size());
        if(request.requests().size() % bulkSize != 0) {
            throw new IllegalStateException("BulkResponse hasn't the expected size. Expected "+bulkSize+", got "+request.requests().size()+".");
        }
    }

    @Override
    public void afterBulk(final long executionId, final BulkRequest request, final BulkResponse response) {
        totalResponseCount.addAndGet(response.getItems().length);

        if(response.hasFailures()) {
            throw new IllegalStateException("BulkResponse in returned with failures");
        }

        if(response.getItems().length % bulkSize != 0) {
            throw new IllegalStateException("BulkResponse hasn't the expected size. Expected "+bulkSize+", got "+response.getItems().length+".");
        }
    }

    @Override
    public void afterBulk(final long executionId, final BulkRequest request, final Throwable failure) {
        throw new IllegalStateException("BulkResponse returned with Throwable.");
    }

}
