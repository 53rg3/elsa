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

package admin;

import assets.*;
import client.ElsaClient;
import helpers.ResponseParser;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.Response;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import responses.ConfirmationResponse;
import responses.ElsaResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IndexAdminTest {

    /* ------------------------------------------------------------------------ */
    /* --------------------------------ATTENTION------------------------------- */
    /* ------------------------------------------------------------------------ */
    // Tests need to be executed sequentially, we use @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    // Once tests failed nonetheless, can't replicate it. Simply repeat the test if this happens again.

    private final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
    private final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(httpHosts)
            .registerModel(TestModel.class, TestDAO.class)
            .createIndexesAndEnsureMappingConsistency(false));

    @Test
    public void createIndex_indexDoesNotExist_pass() {
        final ElsaResponse<CreateIndexResponse> response = this.elsa.admin.createIndex(TestModel.class);
        assertThat(response.get().isAcknowledged(), is(true));
        assertThat(response.get().isShardsAcknowledged(), is(true));
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(true));

        this.elsa.admin.deleteIndex(TestModel.class);
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(false));
    }

    @Test
    public void updateMapping_validMappingWithNestedObject_pass() {
        this.elsa.admin.createIndex(TestModel.class);
        final ElsaResponse<ConfirmationResponse> response = this.elsa.admin.updateMapping(TestModelWithAddedMappings.class);
        assertThat(response.get().hasSucceeded(), is(true));

        this.elsa.admin.deleteIndex(TestModel.class);
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(false));
    }

    @Test
    public void updateMapping_tryingToOverrideExistingMapping_throw() {
        this.elsa.admin.createIndex(TestModel.class);
        final ElsaResponse<ConfirmationResponse> response = this.elsa.admin.updateMapping(TestModelWithInvalidlyModifiedMappings.class);
        assertThat(response.hasException(), is(true));
        assertThat(response.getExceptionResponse().getStatus(), is(400));

        this.elsa.admin.deleteIndex(TestModel.class);
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(false));
    }

    @Test
    public void indexExists_createCheckDeleteCheck_pass() {
        this.elsa.admin.createIndex(TestModel.class);
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(true));

        this.elsa.admin.deleteIndex(TestModel.class);
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(false));
    }

    @Test
    public void deleteIndexViaClass_indexNewlyCreated_pass() {
        this.elsa.admin.createIndex(TestModel.class);

        this.elsa.admin.deleteIndex(TestModel.class);
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(false));
    }

    @Test
    public void deleteIndexViaClass_indexDoesNotExist_throw() {
        ExceptionHandlerWithExtractor handler = new ExceptionHandlerWithExtractor();
        this.elsa.admin.deleteIndex(TestModel.class, handler);
        assertTrue(handler.getException() instanceof ElasticsearchStatusException);
    }

    @Test
    public void deleteIndexViaString_indexNewlyCreated_pass() {
        final TestModel testModel = new TestModel();
        this.elsa.admin.createIndex(TestModel.class);

        this.elsa.admin.deleteIndex(testModel.getIndexConfig().getIndexName());
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(false));
    }

    @Test
    public void deleteIndexViaString_indexDoesNotExist_throw() {
        ExceptionHandlerWithExtractor handler = new ExceptionHandlerWithExtractor();
        final TestModel testModel = new TestModel();
        this.elsa.admin.deleteIndex(testModel.getIndexConfig().getIndexName(), handler);
        assertTrue(handler.getException() instanceof ElasticsearchStatusException);
    }

    @Test
    public void createIndex_withDynamicNaming_pass() {
        TestModel testModel1 = new TestModel();
        TestModel testModel2 = new TestModel();
        assertThat(testModel1.getIndexConfig().getIndexName(), is("elsa_test_index"));

        this.elsa.admin.createIndex(TestModel.class);
        assertThat(this.elsa.admin.indexExists("elsa_test_index"), is(true));

        testModel2.getIndexConfig().setIndexName("new_name");
        this.elsa.admin.createIndex(TestModel.class);
        assertThat(this.elsa.admin.indexExists("new_name"), is(true));
        assertThat(testModel1.getIndexConfig().getIndexName(), is("new_name"));

        testModel2.getIndexConfig().setIndexName("elsa_test_index");
        this.elsa.admin.deleteIndex("elsa_test_index");
        this.elsa.admin.deleteIndex("new_name");
    }

    @Test
    public void changeIndexName_appliesToInstancesOfModel_pass() {
        TestModel testModel1 = new TestModel();
        TestModel testModel2 = new TestModel();
        assertThat(testModel1.getIndexConfig().getIndexName(), is("elsa_test_index"));

        testModel2.getIndexConfig().setIndexName("new_name");
        assertThat(testModel1.getIndexConfig().getIndexName(), is("new_name"));

        testModel2.getIndexConfig().setIndexName("elsa_test_index");
        assertThat(testModel1.getIndexConfig().getIndexName(), is("elsa_test_index"));
    }
}
