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
import exceptions.ElsaException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class AsyncCrudDAOTest {

    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerDAO(new DaoConfig(FakerModelAsync.class, CrudDAO.class, FakerModelAsync.indexConfig))
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
    public static void createIndex() throws ElsaException {
        model1.getIndexConfig().setIndexName(newIndexName);
        elsa.admin.createIndex(FakerModelAsync.class);

        model1.setId("1");
        model1.setName("Carl Smith");

        model2.setId("2");
        model2.setName("Bob Smith");
    }

    @AfterClass
    public static void deleteIndex() throws ElsaException {
        elsa.admin.deleteIndex(newIndexName);
    }

    @Test
    public void asyncChain_indexGetSearchUpdateDelete_pass() throws ElsaException {

        // Index
        crudDAO.indexAsync(model1, RequestOptions.DEFAULT, new AsyncIndexListener(this.indexCheck1, elsa));
        crudDAO.indexAsync(model2, RequestOptions.DEFAULT, new AsyncIndexListener(this.indexCheck2, elsa));
        this.sleep(100);
        assertThat(this.indexCheck1.wasSuccessful(), is(true));
        assertThat(this.indexCheck2.wasSuccessful(), is(true));

        // Get
        crudDAO.getAsync("2", RequestOptions.DEFAULT, new AsyncGetListener(this.getCheck, elsa));
        this.sleep(100);
        assertThat(this.getCheck.wasSuccessful(), is(true));

        // Search
        final SearchRequest searchRequest = new SearchRequest()
                .indices(model1.getIndexConfig().getIndexName())
                .source(SearchSourceBuilder.searchSource()
                        .query(QueryBuilders.matchAllQuery()));
        this.sleep(1000); // Needs time to make indexation...
        crudDAO.searchAsync(searchRequest, RequestOptions.DEFAULT, new AsyncSearchListener(this.searchCheck, elsa));
        this.sleep(100);
        assertThat(this.searchCheck.wasSuccessful(), is(true));

        // Update
        model1.setName("Jane");
        crudDAO.updateAsync(model1, RequestOptions.DEFAULT, new AsyncUpdateListener(this.updateCheck, elsa));
        this.sleep(100);
        final FakerModelAsync updatedResult = crudDAO.get("1");
        assertThat(this.updateCheck.wasSuccessful(), is(true));
        assertThat(updatedResult.getName(), is("Jane"));

        // Delete
        crudDAO.deleteAsync(updatedResult, RequestOptions.DEFAULT, new AsyncDeleteListener(this.deleteCheck, elsa));
        this.sleep(100);
        assertThat(this.deleteCheck.wasSuccessful(), is(true));
        final FakerModelAsync deletedResult = crudDAO.get("1");
        assertThat(deletedResult, nullValue());
    }

    private void sleep(final int ms) {
        try {
            Thread.sleep(ms);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
}
