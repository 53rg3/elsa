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

package scroller;

import assets.FakerModel;
import client.ElsaClient;
import dao.CrudDAO;
import dao.DaoConfig;
import exceptions.ElsaException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ScrollerTest {

    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerDAO(new DaoConfig(CrudDAO.class, FakerModel.indexConfig))
            .createIndexesAndEnsureMappingConsistency(false));
    private static final CrudDAO<FakerModel> dao = elsa.getDAO(FakerModel.class);

    @BeforeClass
    public static void setup() throws ElsaException {
        elsa.admin.createIndex(FakerModel.indexConfig);
        for (int i = 0; i < 100; i++) {
            final FakerModel fakerModel = FakerModel.createModelWithRandomData();
            fakerModel.setId(String.valueOf(i));
            elsa.bulkProcessor.add(dao.buildIndexRequest(fakerModel));
        }
        elsa.bulkProcessor.flush();

        // Wait for indexing
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void teardown() throws ElsaException {
        elsa.admin.deleteIndex(FakerModel.indexConfig);
    }

    @Test
    public void scroller_idsAreRetrievedInExpectedOrderFrom0To99_pass() throws ElsaException {
        final SearchRequest request = new SearchRequest()
                .indices(FakerModel.getIndexName())
                .source(SearchSourceBuilder.searchSource()
                        .query(QueryBuilders.matchAllQuery())
                        .size(1));

        final ScrollManager scrollManager = new ScrollManager(TimeValue.timeValueMinutes(1L));
        SearchResponse searchResponse = elsa.scroller.initialize(scrollManager, request);
        final AtomicInteger expectedId = new AtomicInteger(0);

        while (elsa.scroller.hasHits(searchResponse)) {
            dao.getSearchResponseMapper().mapHitsToStream(searchResponse)
                    .forEach(model -> assertThat(Integer.valueOf(model.getId()), is(expectedId.getAndIncrement())));

            searchResponse = elsa.scroller.getNext(scrollManager, searchResponse);
        }
        elsa.scroller.clearScroll(scrollManager);
    }
}
