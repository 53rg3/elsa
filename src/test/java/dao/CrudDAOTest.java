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

import assets.TestDAO;
import assets.TestHelpers;
import assets.TestModel;
import client.ElsaClient;
import exceptions.ElsaElasticsearchException;
import exceptions.ElsaException;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.util.Arrays;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.search.builder.SearchSourceBuilder.searchSource;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class CrudDAOTest {

    /* ------------------------------------------------------------------------- */
    // SETUP
    /* ------------------------------------------------------------------------- */

    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerDAO(new DaoConfig(TestDAO.class, TestModel.indexConfig))
            .createIndexesAndEnsureMappingConsistency(false));
    private final TestDAO testDAO = elsa.getDAO(TestModel.class);
    private static final TestModel testModelWithoutId = new TestModel();
    private static final TestModel testModelWithId = new TestModel();
    private static final String id = "someId123";

    @BeforeClass
    public static void createIndex() throws ElsaException {
        if (elsa.admin.indexExists(TestModel.class)) {
            elsa.admin.deleteIndex(TestModel.class);
        }
        elsa.admin.createIndex(TestModel.class, TestModel.indexConfig);
        testModelWithoutId.setStringField("modelWithoutId");
        testModelWithoutId.setArrayField(Arrays.asList("qwer", "asdf", "yxcv"));
        testModelWithoutId.setIntegerField(1234);

        refreshTestModelWithId();
    }

    @AfterClass
    public static void deleteIndex() throws ElsaException {
        elsa.admin.deleteIndex(TestModel.class);
    }

    @After
    public void deleteTestModelWithCustomId() throws ElsaException {
        final DeleteResponse deleteResponse = this.testDAO.delete(testModelWithId);
        assertThat(deleteResponse.getResult().name(), either(is("DELETED")).or(is("NOT_FOUND")));
    }

    private IndexResponse indexTestModelWithCustomId() throws ElsaException {
        return this.testDAO.index(testModelWithId);
    }

    private static void refreshTestModelWithId() {
        testModelWithId.setId(id);
        testModelWithId.setStringField("modelWithId");
        testModelWithId.setArrayField(Arrays.asList("qwer", "asdf", "yxcv"));
        testModelWithId.setIntegerField(1234);
    }

    /* ------------------------------------------------------------------------- */
    // TESTS
    /* ------------------------------------------------------------------------- */

    @Test
    public void index_withoutId_pass() throws ElsaException {
        final IndexResponse indexResponse = this.testDAO.index(testModelWithoutId);
        assertThat(indexResponse.getResult().name(), is("CREATED"));
    }

    @Test
    public void index_withTransientField_fieldIsNullAfterGet() throws ElsaException {
        testModelWithoutId.setTransientField("This should be null");
        final IndexResponse indexResponse = this.testDAO.index(testModelWithoutId);
        assertThat(indexResponse.getResult().name(), is("CREATED"));

        final TestModel testModel = this.testDAO.get(indexResponse.getId());
        assertThat(testModel.getTransientField(), nullValue());
    }

    @Test
    public void indexAndGet_customId_pass() throws ElsaException {
        assertThat(this.indexTestModelWithCustomId().getResult().name(), is("CREATED"));

        final TestModel newDoc = this.testDAO.get(id);
        assertThat(newDoc.getId(), is(id));
    }

    @Test
    public void indexAndUpdate_idFieldsAreNotPassedToSource_pass() throws ElsaException {
        // Index document without ID and get assigned auto-ID
        final IndexResponse response1 = this.testDAO.index(testModelWithoutId);
        final String id = response1.getId();

        // Assert that document was inserted
        TestModel model1 = this.testDAO.get(response1.getId());
        assertThat(model1, notNullValue());

        // Index the model again and assert that ID hasn't been indexed in _source field, i.e. search returns null
        // (Indexing with an existing ID overrides the document.)
        this.testDAO.index(model1);
        TestHelpers.sleep(500);
        final TestModel model2 = this.testDAO.searchAndMapFirstHit(
                new SearchRequest()
                        .indices(model1.getIndexConfig().getIndexName())
                        .source(searchSource()
                                .query(matchQuery("id", id))));
        assertThat(model2, nullValue());

        // Update the model and assert that ID hasn't been indexed in _source field, i.e. search returns null
        model1 = this.testDAO.get(response1.getId());
        this.testDAO.update(model1);
        TestHelpers.sleep(500);
        final TestModel model3 = this.testDAO.searchAndMapFirstHit(
                new SearchRequest()
                        .indices(model1.getIndexConfig().getIndexName())
                        .source(searchSource()
                                .query(matchQuery("id", id))));
        assertThat(model3, nullValue());
    }

    @Test
    public void get_asModel_pass() {
        // see indexAndGet_customId_pass
    }

    @Test
    public void get_getAsGetResponse_pass() throws ElsaException {
        assertThat(this.indexTestModelWithCustomId().getResult().name(), is("CREATED"));
        final GetResponse response = this.testDAO.getRawResponse(id);

        assertThat(response, notNullValue());
        assertThat(response.getId(), is(id));
    }

    @Test
    public void get_nonExistingDocument_nullResult() throws ElsaException {
        final TestModel newDoc = this.testDAO.get("nonExistingId");
        assertThat(newDoc, nullValue());
    }

    @Test
    public void delete_newDocument_pass() throws ElsaException {
        assertThat(this.indexTestModelWithCustomId().getResult().name(), is("CREATED"));
        final TestModel newDoc = this.testDAO.get(id);
        assertThat(newDoc, notNullValue());
        assertThat(newDoc.getId(), is(id));

        final DeleteResponse deleteResponse = this.testDAO.delete(testModelWithId);
        assertThat(deleteResponse.getResult().name(), is("DELETED"));
    }

    @Test
    public void update_singleValueInDoc_pass() throws ElsaException {
        this.indexTestModelWithCustomId();

        TestModel newDoc = this.testDAO.get(id);
        assertThat(newDoc, notNullValue());

        newDoc.setStringField("updatedValue");
        final UpdateResponse updateResponse = this.testDAO.update(newDoc);
        assertThat(updateResponse.getResult().name(), is("UPDATED"));

        newDoc = this.testDAO.get(id);
        assertThat(newDoc.getStringField(), is("updatedValue"));
    }

    @Test
    public void update_partialValue_pass() throws ElsaException {
        this.indexTestModelWithCustomId();

        final TestModel testModel = new TestModel();
        testModel.setId(id);
        testModel.setStringField("partial update");

        this.testDAO.update(testModel);
        final TestModel testModel1 = this.testDAO.get(id);
        assertThat(testModel1.getStringField(), is("partial update"));
    }

    @Test
    public void update_nonExistingDocument_throw() throws ElsaException {
        try {
            this.testDAO.update(testModelWithId);
        } catch (final ElsaException e) {
            assertTrue(e instanceof ElsaElasticsearchException);
            assertThat(e.getHttpStatus(), is(404));
        }

        final TestModel model = this.testDAO.get(id);
        assertThat(model, nullValue());
    }

}
