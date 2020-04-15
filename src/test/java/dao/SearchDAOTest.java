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

package dao;

import assets.FakerModel;
import assets.TestHelpers;
import client.ElsaClient;
import helpers.IndexName;
import helpers.Search;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import responses.ElsaResponse;

import java.util.List;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static helpers.Search.src;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SearchDAOTest {

    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerModel(FakerModel.class, CrudDAO.class)
            .createIndexesAndEnsureMappingConsistency(false));
    private static final CrudDAO<FakerModel> dao = elsa.getDAO(FakerModel.class);

    private final SearchRequest request = Search.req()
            .indices(IndexName.of(FakerModel.class))
            .source(src()
                    .size(3)
                    .query(QueryBuilders.rangeQuery("age")
                            .gt(22)
                            .lt(33)));

    @BeforeClass
    public static void setup() {
        elsa.admin.createIndex(FakerModel.class);
        for (int i = 0; i < 1000; i++) {
            elsa.bulkProcessor.add(dao.buildIndexRequest(FakerModel.createModelWithRandomData()));
        }
        elsa.bulkProcessor.flush();

        // Wait for indexing
        TestHelpers.sleep(1000);
    }

    @AfterClass
    public static void teardown() {
        elsa.admin.deleteIndex(FakerModel.class);
    }

    @Test
    public void search() {
        final ElsaResponse<SearchResponse> response = dao.search(this.request);
        assertThat(response.hasResult(), is(true));
        assertThat(response.get().getHits().getHits().length, greaterThan(1));
    }

    @Test
    public void searchAndMapFirstHit() {
        final ElsaResponse<FakerModel> fakerModel = dao.searchAndMapFirstHit(this.request);
        assertThat(fakerModel.hasResult(), is(true));
        assertThat(fakerModel.get().getId(), notNullValue());
    }

    @Test
    public void searchAndMapToList() {
        final ElsaResponse<List<FakerModel>> list = dao.searchAndMapToList(this.request);
        assertThat(list.hasResult(), is(true));
        assertThat(list.get().size(), greaterThan(1));
        for (final FakerModel fakerModel : list.get()) {
            assertThat(fakerModel.getId(), notNullValue());
        }
    }

    @Test
    public void searchHasNoResults() {
        final SearchRequest ageHasNoResultsRequest = Search.req()
                .indices(IndexName.of(FakerModel.class))
                .source(src()
                        .size(3)
                        .query(QueryBuilders.rangeQuery("age")
                                .gt(111)));
        final ElsaResponse<FakerModel> response1 = dao.searchAndMapFirstHit(ageHasNoResultsRequest);
        assertThat(response1.hasResult(), is(false));
        final ElsaResponse<List<FakerModel>> response2 = dao.searchAndMapToList(ageHasNoResultsRequest);
        assertThat(response2.hasResult(), is(false));
    }

    @Test
    public void searchAndMapToStream() {
        dao.searchAndMapToStream(this.request).get()
                .forEach(model -> assertThat(model.getId(), notNullValue()));

        assertThat(dao.searchAndMapToStream(this.request).get()
                .count(), greaterThan(0L));
    }
}
