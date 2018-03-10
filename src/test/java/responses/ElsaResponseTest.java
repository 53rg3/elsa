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

package responses;

import assets.FakerModel;
import assets.TestHelpers;
import client.ElsaClient;
import dao.CrudDAO;
import helpers.IndexName;
import helpers.Search;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.NoSuchElementException;

import static helpers.Search.src;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ElsaResponseTest {

    private static final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(httpHosts)
            .registerModel(FakerModel.class, CrudDAO.class)
            .createIndexesAndEnsureMappingConsistency(false));
    private static final CrudDAO<FakerModel> dao = elsa.getDAO(FakerModel.class);

    private final SearchRequest request = Search.req()
            .indices(IndexName.of(FakerModel.class))
            .source(src()
                    .size(3)
                    .query(rangeQuery("age")
                            .gt(2222)
                            .lt(3322)));

    @BeforeClass
    public static void setup() {
        elsa.admin.createIndex(FakerModel.class);
        for (int i = 0; i < 100; i++) {
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

    @Test(expected = NoSuchElementException.class)
    public void of_requestWithExceptions_throw() {
        ElsaResponse<CreateIndexResponse> response = elsa.admin.createIndex(FakerModel.class);
        assertThat(response.getExceptionResponse().getStatus(), is(400));
        assertThat(response.hasException(), is(true));
        assertThat(response.isPresent(), is(false));
        response.get();
    }

    @Test(expected = NoSuchElementException.class)
    public void ofNullable_getSearchResponse_throw() {
        ElsaResponse<FakerModel> response = dao.searchAndMapFirstHit(request);
        assertThat(response.hasException(), is(false));
        assertThat(response.isPresent(), is(false));
        response.get();
    }
    
}
