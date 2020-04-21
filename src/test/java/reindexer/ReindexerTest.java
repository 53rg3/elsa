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
import exceptions.ElsaException;
import helpers.XJson;
import org.elasticsearch.action.search.SearchRequest;
import org.junit.*;
import org.junit.runners.MethodSorters;
import reindexer.ReindexOptions.ReindexMode;
import reindexer.ReindexSettings.ReindexSettingsBuilder;
import responses.ElsaResponse;
import responses.ReindexResponse;

import java.util.List;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.search.builder.SearchSourceBuilder.searchSource;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class ReindexerTest {

    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerModel(FakerModel.class, CrudDAO.class)
    );
    private static final CrudDAO<FakerModel> dao = elsa.getDAO(FakerModel.class);
    private static final FakerModel fakerModel = new FakerModel();
    private static final String oldIndex = fakerModel.getIndexConfig().getIndexName();
    private static final String oldIndexCorrectMapping = oldIndex+"_correct_mapping";
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
    public static void tearDown() throws ElsaException {
        elsa.admin.deleteIndex(oldIndex);
    }

    @Test
    public void a1_create_modeCreateNewIndex_pass() throws ElsaException {
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
        final ReindexResponse response = elsa.reindexer.execute(reindexSettings, ReindexMode.CREATE_NEW_INDEX_FROM_MODEL_IN_DESTINATION);
        assertThat(response.getFailures().size(), is(0));
        TestHelpers.sleep(1000);


        final List<FakerModel> list = dao.searchAndMapToList(new SearchRequest()
                .indices(FakerModel.getIndexName())
                .source(searchSource()
                        .query(matchAllQuery())));

        int minAge = 0;
        for (final FakerModel fakerModel : list) {
            assertTrue(fakerModel.getAge() >= minAge);
            assertTrue(fakerModel.getName().contains("Ms") || fakerModel.getName().contains("Mr"));
            minAge = fakerModel.getAge();
        }

        elsa.admin.deleteIndex(newIndex);
        fakerModel.getIndexConfig().setIndexName(oldIndex);
    }

    @Test
    @Ignore("fix after this.elsa.admin.updateMapping doesn't use ElsaResponse. Exception is hidden and not propagated till here.")
    public void a2_create_modeAbortIfMappingIncorrect_responseHasException() throws ElsaException {
        fakerModel.getIndexConfig().setIndexName(oldIndexCorrectMapping);
        elsa.admin.createIndex(FakerModel.class);

        final ReindexSettings reindexSettings = new ReindexSettingsBuilder()
                .configureSource(c -> c
                        .fromIndex(FakerModel.class))
                .configureDestination(c -> c
                        .intoIndex(FakerModelInvalidMapping.class))
                .build();
        final ReindexResponse response = elsa.reindexer.execute(reindexSettings, ReindexMode.ABORT_IF_MAPPING_INCORRECT);
        assertThat(response.getFailures().size(), not(0));
        fakerModel.getIndexConfig().setIndexName(oldIndex);
        elsa.admin.deleteIndex(oldIndexCorrectMapping);
    }

}
