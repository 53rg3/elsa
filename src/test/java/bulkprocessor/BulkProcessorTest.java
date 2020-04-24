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

package bulkprocessor;

import assets.FakerModel;
import assets.TestBulkResponseListener;
import client.ElsaClient;
import dao.CrudDAO;
import dao.DaoConfig;
import exceptions.ElsaException;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BulkProcessorTest {

    private final int bulkSize = 66;
    private final AtomicInteger totalRequests = new AtomicInteger();
    private final AtomicInteger totalResponses = new AtomicInteger();
    private final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerDAO(new DaoConfig(CrudDAO.class, FakerModel.indexConfig))
            .configureBulkProcessor(config -> config
                    .setBulkActions(66)
                    .setFlushInterval(TimeValue.timeValueSeconds(10)))
            .setBulkResponseListener(
                    new TestBulkResponseListener(this.bulkSize, this.totalRequests, this.totalResponses),
                    RequestOptions.DEFAULT)
    );
    private final CrudDAO<FakerModel> crudDAO = this.elsa.getDAO(FakerModel.class);

    @Test
    public void bulkProcessor_10x66Requests_noErrorsCountsAreCorrect() throws ElsaException {

        for (int i = 0; i < this.bulkSize * 10; i++) {
            this.elsa.bulkProcessor.add(this.crudDAO.buildIndexRequest(FakerModel.createModelWithRandomData()));
        }

        // We need wait for the responses
        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        assertThat(this.totalRequests.get(), is(this.bulkSize * 10));
        assertThat(this.totalResponses.get(), is(this.bulkSize * 10));

        this.elsa.admin.deleteIndex(FakerModel.class);
    }

}
