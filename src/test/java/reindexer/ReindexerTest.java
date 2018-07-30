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

package reindexer;

import assets.FakerModel;
import assets.FakerModelInvalidMapping;
import assets.TestHelpers;
import client.ElsaClient;
import dao.CrudDAO;
import helpers.IndexName;
import helpers.ResponseParser;
import helpers.XJson;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import reindexer.ReindexOptions.ReindexMode;
import reindexer.ReindexSettings.ReindexSettingsBuilder;
import responses.ElsaResponse;
import responses.ReindexResponse;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.search.builder.SearchSourceBuilder.searchSource;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class ReindexerTest {

    private static final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(httpHosts)
            .registerModel(FakerModel.class, CrudDAO.class)
            .stifleThreadUntilClusterIsOnline(true));
    private static final CrudDAO<FakerModel> dao = elsa.getDAO(FakerModel.class);
    private static final FakerModel fakerModel = new FakerModel();
    private static final String oldIndex = fakerModel.getIndexConfig().getIndexName();
    private static final int totalDocuments = 100;
    private static final String newIndex = "bulk_testing_create_new_index";

    @BeforeClass
    public static void setup() {
        for (int i = 0; i < totalDocuments; i++) {
            elsa.bulkProcessor.add(dao.buildIndexRequest(FakerModel.createModelWithRandomData()));
        }
        elsa.bulkProcessor.flush();

        // We need wait for indexing
        TestHelpers.sleep(1000);
    }

    @AfterClass
    public static void tearDown() {
        elsa.admin.deleteIndex(oldIndex);
    }

    @Test
    public void create_modeCreateNewIndex_pass() {
        fakerModel.getIndexConfig().setIndexName(newIndex);

        final ReindexSettings reindexSettings = new ReindexSettingsBuilder()
                .configureSource(c -> c
                        .fromIndex(oldIndex)
                        .selectFields("name", "age")
                        .whereClause(boolQuery()
                                .should(matchQuery("name", "ms"))
                                .should(matchQuery("name", "mr")))
                        .sortBy(new XJson()
                                .field("age", "asc")))
                .configureDestination(c -> c
                        .intoIndex(FakerModel.class))
                .build();
        ElsaResponse<ReindexResponse> response = elsa.reindexer.execute(reindexSettings, ReindexMode.CREATE_NEW_INDEX_FROM_MODEL_IN_DESTINATION);
        assertThat(response.isPresent(), is(true));
        TestHelpers.sleep(1000);


        final ElsaResponse<List<FakerModel>> list = dao.searchAndMapToList(new SearchRequest()
                .indices(FakerModel.getIndexName())
                .source(searchSource()
                        .query(matchAllQuery())));

        int minAge = 0;
        for (final FakerModel fakerModel : list.get()) {
            assertTrue(fakerModel.getAge() >= minAge);
            assertTrue(fakerModel.getName().contains("Ms") || fakerModel.getName().contains("Mr"));
            minAge = fakerModel.getAge();
        }

        fakerModel.getIndexConfig().setIndexName(oldIndex);
        elsa.admin.deleteIndex(newIndex);
    }

    @Test
    public void create_modeAbortIfMappingIncorrect_responseHasException() {
        fakerModel.getIndexConfig().setIndexName(newIndex);

        final ReindexSettings reindexSettings = new ReindexSettingsBuilder()
                .configureSource(c -> c
                        .fromIndex("bulk_testing"))
                .configureDestination(c -> c
                        .intoIndex(FakerModelInvalidMapping.class))
                .build();
        ElsaResponse<ReindexResponse> response = elsa.reindexer.execute(reindexSettings, ReindexMode.ABORT_IF_MAPPING_INCORRECT);
        assertThat(response.isPresent(), is(false));
        assertThat(response.hasException(), is(true));
        fakerModel.getIndexConfig().setIndexName(oldIndex);
    }

}
