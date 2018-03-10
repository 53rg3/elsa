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

import assets.*;
import client.ElsaClient;
import com.github.javafaker.Faker;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import responses.ElsaResponse;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class AsyncCrudDAOTest {

    private static final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(httpHosts)
            .registerModel(FakerModelAsync.class, CrudDAO.class)
            .createIndexesAndEnsureMappingConsistency(false));
    private static final CrudDAO<FakerModelAsync> crudDAO = elsa.getDAO(FakerModelAsync.class);

    private static final FakerModelAsync model1 = new FakerModelAsync();
    private static final FakerModelAsync model2 = new FakerModelAsync();
    private final Check indexCheck1 = new Check();
    private final Check indexCheck2 = new Check();
    private final Check getCheck = new Check();
    private final Check updateCheck = new Check();
    private final Check deleteCheck = new Check();
    private final Check searchCheck = new Check();
    private final static String newIndexName = "async_test";

    @BeforeClass
    public static void createIndex() {
        model1.getIndexConfig().setIndexName(newIndexName);
        elsa.admin.createIndex(FakerModelAsync.class);

        model1.setId("1");
        model1.setName("Carl Smith");

        model2.setId("2");
        model2.setName("Bob Smith");
    }

    @AfterClass
    public static void deleteIndex() {
        elsa.admin.deleteIndex(newIndexName);
    }

    @Test
    public void asyncChain_indexGetSearchUpdateDelete_pass() {

        // Index
        crudDAO.indexAsync(model1, new AsyncIndexListener(indexCheck1, elsa));
        crudDAO.indexAsync(model2, new AsyncIndexListener(indexCheck2, elsa));
        this.sleep(100);
        assertThat(indexCheck1.wasSuccessful(), is(true));
        assertThat(indexCheck2.wasSuccessful(), is(true));

        // Get
        crudDAO.getAsync("2", new AsyncGetListener(getCheck, elsa));
        this.sleep(100);
        assertThat(getCheck.wasSuccessful(), is(true));

        // Search
        SearchRequest searchRequest = new SearchRequest()
                .indices(model1.getIndexConfig().getIndexName())
                .source(SearchSourceBuilder.searchSource()
                        .query(QueryBuilders.matchAllQuery()));
        this.sleep(1000); // Needs time to make indexation...
        crudDAO.searchAsync(searchRequest, new AsyncSearchListener(searchCheck, elsa));
        this.sleep(100);
        assertThat(searchCheck.wasSuccessful(), is(true));

        // Update
        model1.setName("Jane");
        crudDAO.updateAsync(model1, new AsyncUpdateListener(updateCheck, elsa));
        this.sleep(100);
        ElsaResponse<FakerModelAsync> updatedResult = crudDAO.get("1");
        assertThat(updateCheck.wasSuccessful(), is(true));
        assertThat(updatedResult.get().getName(), is("Jane"));

        // Delete
        crudDAO.deleteAsync(updatedResult.get(), new AsyncDeleteListener(deleteCheck, elsa));
        this.sleep(100);
        assertThat(deleteCheck.wasSuccessful(), is(true));
        ElsaResponse<FakerModelAsync> deletedResult = crudDAO.get("1");
        assertThat(deletedResult.isPresent(), is(false));
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
