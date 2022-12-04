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

package io.github.ss3rg3.elsa.dao;

import assets.FakerModel;
import assets.TestHelpers;
import io.github.ss3rg3.elsa.ElsaClient;
import io.github.ss3rg3.elsa.exceptions.ElsaException;
import io.github.ss3rg3.elsa.helpers.Search;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static io.github.ss3rg3.elsa.helpers.Search.src;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class SearchDAOTest {

    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerDAO(new DaoConfig(CrudDAO.class, FakerModel.indexConfig))
            .createIndexesAndEnsureMappingConsistency(false));
    private static final CrudDAO<FakerModel> dao = elsa.getDAO(FakerModel.class);

    private final SearchRequest request = Search.req()
            .indices(FakerModel.indexConfig.getIndexName())
            .source(src()
                    .size(3)
                    .query(QueryBuilders.rangeQuery("age")
                            .gt(22)
                            .lt(33)));

    @BeforeClass
    public static void setup() throws ElsaException {
        elsa.admin.createIndex(FakerModel.indexConfig);
        for (int i = 0; i < 1000; i++) {
            elsa.bulkProcessor.add(dao.buildIndexRequest(FakerModel.createModelWithRandomData()));
        }
        elsa.bulkProcessor.flush();

        // Wait for indexing
        TestHelpers.sleep(1000);
    }

    @AfterClass
    public static void teardown() throws ElsaException {
        elsa.admin.deleteIndex(FakerModel.indexConfig);
    }

    @Test
    public void search() throws ElsaException {
        final SearchResponse response = dao.search(this.request);
        assertThat(response.getHits().getHits().length, greaterThan(1));
    }

    @Test
    public void searchAndMapFirstHit() throws ElsaException {
        final FakerModel fakerModel = dao.searchAndMapFirstHit(this.request);
        assertThat(fakerModel.getId(), notNullValue());
    }

    @Test
    public void searchAndMapToList() throws ElsaException {
        final List<FakerModel> list = dao.searchAndMapToList(this.request);
        assertThat(list.size(), greaterThan(1));
        for (final FakerModel fakerModel : list) {
            assertThat(fakerModel.getId(), notNullValue());
        }
    }

    @Test
    public void searchHasNoResults() throws ElsaException {
        final SearchRequest ageHasNoResultsRequest = Search.req()
                .indices(FakerModel.indexConfig.getIndexName())
                .source(src()
                        .size(3)
                        .query(QueryBuilders.rangeQuery("age")
                                .gt(111)));
        final FakerModel response1 = dao.searchAndMapFirstHit(ageHasNoResultsRequest);
        assertThat(response1, nullValue());
        final List<FakerModel> response2 = dao.searchAndMapToList(ageHasNoResultsRequest);
        assertThat(response2, notNullValue());
        assertThat(response2.size(), is(0));
    }

    @Test
    public void searchAndMapToStream() throws ElsaException {
        dao.searchAndMapToStream(this.request)
                .forEach(model -> assertThat(model.getId(), notNullValue()));

        assertThat(dao.searchAndMapToStream(this.request)
                .count(), greaterThan(0L));
    }
}
