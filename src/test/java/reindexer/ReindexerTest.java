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
import dao.DaoConfig;
import exceptions.ElsaException;
import exceptions.ElsaIOException;
import helpers.XJson;
import model.IndexConfig;
import org.elasticsearch.action.search.SearchRequest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import reindexer.ReindexOptions.ReindexMode;
import reindexer.ReindexSettings.ReindexSettingsBuilder;
import responses.ReindexResponse;

import java.util.List;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.search.builder.SearchSourceBuilder.searchSource;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class ReindexerTest {

    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerDAO(new DaoConfig(FakerModel.class, CrudDAO.class, FakerModel.indexConfig))
    );
    private static final CrudDAO<FakerModel> dao = elsa.getDAO(FakerModel.class);
    private static final FakerModel fakerModel = new FakerModel();
    private static final String oldIndex = fakerModel.getIndexConfig().getIndexName();
    private static final String oldIndexCorrectMapping = oldIndex + "_correct_mapping";
    private static final int totalDocuments = 100;
    private static final String newIndex = "bulk_testing_create_new_index";
    private static final IndexConfig newIndexConfig = new IndexConfig(c -> c
            .indexName(newIndex)
            .mappingClass(FakerModel.class)
            .shards(1)
            .replicas(0));
    private static final IndexConfig oldIndexCorrectMappingConfig = new IndexConfig(c -> c
            .indexName(oldIndexCorrectMapping)
            .mappingClass(FakerModel.class)
            .shards(1)
            .replicas(0));

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
    public static void tearDown() throws ElsaException {
        elsa.admin.deleteIndex(oldIndex);
    }

    @Test
    public void a1_create_modeCreateNewIndex_pass() throws ElsaException {

        final ReindexSettings reindexSettings = new ReindexSettingsBuilder()
                .configureSource(c -> c
                        .fromIndex(FakerModel.indexConfig)
                        .selectFields("name", "age")
                        .whereClause(boolQuery()
                                .should(matchQuery("name", "ms"))
                                .should(matchQuery("name", "mr")))
                        .sortBy(new XJson()
                                .field("age", "asc")))
                .configureDestination(c -> c
                        .intoIndex(newIndexConfig))
                .build();
        final ReindexResponse response = elsa.reindexer.execute(reindexSettings, ReindexMode.CREATE_NEW_INDEX_FROM_MODEL_IN_DESTINATION);
        assertThat(response.getFailures().size(), is(0));
        TestHelpers.sleep(1000);


        final List<FakerModel> list = dao.searchAndMapToList(new SearchRequest()
                .indices(newIndexConfig.getIndexName())
                .source(searchSource()
                        .query(matchAllQuery())));

        int minAge = 0;
        for (final FakerModel fakerModel : list) {
            assertThat("Expected age to be >= " + minAge + ", but was " + fakerModel.getAge(),
                    fakerModel.getAge() >= minAge, is(true));
            assertThat("Expected name to contain 'Ms' or 'Mr', but got: " + fakerModel.getName(),
                    fakerModel.getName().contains("Ms") || fakerModel.getName().contains("Mr"), is(true));
            minAge = fakerModel.getAge();
        }

        elsa.admin.deleteIndex(newIndex);
    }

    @Test
    public void a2_create_modeAbortIfMappingIncorrect_responseHasException() throws ElsaException {
        elsa.admin.createIndex(FakerModel.class, oldIndexCorrectMappingConfig);

        final ReindexSettings reindexSettings = new ReindexSettingsBuilder()
                .configureSource(c -> c
                        .fromIndex(oldIndexCorrectMappingConfig))
                .configureDestination(c -> c
                        .intoIndex(FakerModelInvalidMapping.indexConfig))
                .build();
        try {
            elsa.reindexer.execute(reindexSettings, ReindexMode.ABORT_IF_MAPPING_INCORRECT);
        } catch (final ElsaException e) {
            assertTrue(e instanceof ElsaIOException);
            assertThat(e.getHttpStatus(), is(400));
        }

        elsa.admin.deleteIndex(oldIndexCorrectMapping);
    }

}
