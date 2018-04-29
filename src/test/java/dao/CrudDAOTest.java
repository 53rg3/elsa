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

import assets.ExceptionHandlerWithExtractor;
import assets.TestDAO;
import assets.TestHelpers;
import assets.TestModel;
import client.ElsaClient;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.junit.*;
import org.junit.runners.MethodSorters;
import responses.ElsaResponse;

import java.util.Arrays;

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

    private static final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(httpHosts)
            .registerModel(TestModel.class, TestDAO.class)
            .createIndexesAndEnsureMappingConsistency(false));
    private final TestDAO testDAO = elsa.getDAO(TestModel.class);
    private static TestModel testModelWithoutId = new TestModel();
    private static final TestModel testModelWithId = new TestModel();
    private static final String id = "someId123";

    @BeforeClass
    public static void createIndex() {
        if(elsa.admin.indexExists(TestModel.class)) {
            elsa.admin.deleteIndex(TestModel.class);
        }
        elsa.admin.createIndex(TestModel.class);
        testModelWithoutId.setStringField("modelWithoutId");
        testModelWithoutId.setArrayField(Arrays.asList("qwer", "asdf", "yxcv"));
        testModelWithoutId.setIntegerField(1234);

        refreshTestModelWithId();
    }

    @AfterClass
    public static void deleteIndex() {
        elsa.admin.deleteIndex(TestModel.class);
    }

    @After
    public void deleteTestModelWithCustomId(){
        final ElsaResponse<DeleteResponse> deleteResponse = this.testDAO.delete(testModelWithId);
        assertThat(deleteResponse.get().getResult().name(), either(is("DELETED")).or(is("NOT_FOUND")));
    }

    private ElsaResponse<IndexResponse> indexTestModelWithCustomId() {
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
    public void index_withoutId_pass() {
        final ElsaResponse<IndexResponse> indexResponse = this.testDAO.index(testModelWithoutId);
        assertThat(indexResponse.get().getResult().name(), is("CREATED"));
    }

    @Test
    public void index_withTransientField_fieldIsNullAfterGet() {
        testModelWithoutId.setTransientField("This should be null");
        final ElsaResponse<IndexResponse> indexResponse = this.testDAO.index(testModelWithoutId);
        assertThat(indexResponse.get().getResult().name(), is("CREATED"));

        final ElsaResponse<TestModel> testModel = this.testDAO.get(indexResponse.get().getId());
        assertThat(testModel.get().getTransientField(), nullValue());
    }

    @Test
    public void indexAndGet_customId_pass() {
        assertThat(this.indexTestModelWithCustomId().get().getResult().name(), is("CREATED"));

        final ElsaResponse<TestModel> newDoc = this.testDAO.get(id);
        assertThat(newDoc.get().getId(), is(id));
    }

    @Test
    public void indexAndUpdate_idFieldsAreNotPassedToSource_pass() {
        // Index document without ID and get assigned auto-ID
        final ElsaResponse<IndexResponse> response1 = this.testDAO.index(testModelWithoutId);
        final String id = response1.get().getId();

        // Assert that document was inserted
        ElsaResponse<TestModel> model1 = this.testDAO.get(response1.get().getId());
        assertThat(model1, notNullValue());

        // Index the model again and assert that ID hasn't been indexed in _source field, i.e. search returns null
        // (Indexing with an existing ID overrides the document.)
        this.testDAO.index(model1.get());
        TestHelpers.sleep(500);
        final ElsaResponse<TestModel> model2 = this.testDAO.searchAndMapFirstHit(
                new SearchRequest()
                        .indices(model1.get().getIndexConfig().getIndexName())
                        .source(searchSource()
                                .query(matchQuery("id", id))));
        assertThat(model2.isPresent(), is(false));

        // Update the model and assert that ID hasn't been indexed in _source field, i.e. search returns null
        model1 = this.testDAO.get(response1.get().getId());
        this.testDAO.update(model1.get());
        TestHelpers.sleep(500);
        final ElsaResponse<TestModel> model3 = this.testDAO.searchAndMapFirstHit(
                new SearchRequest()
                        .indices(model1.get().getIndexConfig().getIndexName())
                        .source(searchSource()
                                .query(matchQuery("id", id))));
        assertThat(model3.isPresent(), is(false));
    }

    @Test
    public void get_asModel_pass() {
        // see indexAndGet_customId_pass
    }

    @Test
    public void get_getAsGetResponse_pass() {
        assertThat(this.indexTestModelWithCustomId().get().getResult().name(), is("CREATED"));
        final ElsaResponse<GetResponse> response = this.testDAO.getRawResponse(id);

        assertThat(response, notNullValue());
        assertThat(response.get().getId(), is(id));
    }

    @Test
    public void get_nonExistingDocument_nullResult() {
        final ElsaResponse<TestModel> newDoc = this.testDAO.get("nonExistingId");
        assertThat(newDoc.hasResult(), is(false));
        assertThat(newDoc.isPresent(), is(false));
    }

    @Test
    public void delete_newDocument_pass() {
        assertThat(this.indexTestModelWithCustomId().get().getResult().name(), is("CREATED"));
        final ElsaResponse<TestModel> newDoc = this.testDAO.get(id);
        assertThat(newDoc, notNullValue());
        assertThat(newDoc.get().getId(), is(id));

        final ElsaResponse<DeleteResponse> deleteResponse = this.testDAO.delete(testModelWithId);
        assertThat(deleteResponse.get().getResult().name(), is("DELETED"));
    }

    @Test
    public void update_singleValueInDoc_pass() {
        this.indexTestModelWithCustomId();

        ElsaResponse<TestModel> newDoc = this.testDAO.get(id);
        assertThat(newDoc, notNullValue());

        newDoc.get().setStringField("updatedValue");
        final ElsaResponse<UpdateResponse> updateResponse = this.testDAO.update(newDoc.get());
        assertThat(updateResponse.get().getResult().name(), is("UPDATED"));

        newDoc = this.testDAO.get(id);
        assertThat(newDoc.get().getStringField(), is("updatedValue"));
    }

    @Test
    public void update_partialValue_pass() {
        this.indexTestModelWithCustomId();

        TestModel testModel = new TestModel();
        testModel.setId(id);
        testModel.setStringField("partial update");

        this.testDAO.update(testModel);
        ElsaResponse<TestModel> testModel1 = this.testDAO.get(id);
        assertThat(testModel1.get().getStringField(), is("partial update"));
    }

    @Test
    public void update_nonExistingDocument_throw() {
        final ExceptionHandlerWithExtractor handler = new ExceptionHandlerWithExtractor();
        this.testDAO.update(testModelWithId, handler);
        ElsaResponse<TestModel> model = this.testDAO.get(id);
        assertThat(model.isPresent(), is(false));
        assertTrue(handler.getException() instanceof ElasticsearchStatusException);
    }

}
