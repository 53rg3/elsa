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
import org.apache.http.HttpHost;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BulkProcessorTest {

    private final int bulkSize = 66;
    private final AtomicInteger totalRequests = new AtomicInteger();
    private final AtomicInteger totalResponses = new AtomicInteger();
    private final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
    private final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(httpHosts)
            .registerModel(FakerModel.class, CrudDAO.class)
            .configureBulkProcessor(config -> config
                    .setBulkActions(66)
                    .setFlushInterval(TimeValue.timeValueSeconds(10)))
            .setBulkResponseListener(new TestBulkResponseListener(bulkSize, totalRequests, totalResponses))
            .stifleThreadUntilClusterIsOnline(true));
    private final CrudDAO<FakerModel> crudDAO = elsa.getDAO(FakerModel.class);

    @Test
    public void bulkProcessor_10x66Requests_noErrorsCountsAreCorrect() {

        for (int i = 0; i < bulkSize*10; i++) {
            this.elsa.bulkProcessor.add(crudDAO.buildIndexRequest(FakerModel.createModelWithRandomData()));
        }

        // We need wait for the responses
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat(this.totalRequests.get(), is(bulkSize*10));
        assertThat(this.totalResponses.get(), is(bulkSize*10));

        this.elsa.admin.deleteIndex(FakerModel.class);
    }

}
