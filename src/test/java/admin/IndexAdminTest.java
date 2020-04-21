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

import assets.TestDAO;
import assets.TestModel;
import assets.TestModelWithAddedMappings;
import assets.TestModelWithInvalidlyModifiedMappings;
import client.ElsaClient;
import exceptions.ElsaException;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import responses.ConfirmationResponse;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IndexAdminTest {

    private final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerModel(TestModel.class, TestDAO.class)
            .createIndexesAndEnsureMappingConsistency(false));

    @Test
    public void createIndex_indexDoesNotExist_pass() throws ElsaException {
        final CreateIndexResponse response = this.elsa.admin.createIndex(TestModel.class);
        assertThat(response.isAcknowledged(), is(true));
        assertThat(response.isShardsAcknowledged(), is(true));
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(true));

        this.elsa.admin.deleteIndex(TestModel.class);
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(false));
    }

    @Test
    public void updateMapping_validMappingWithNestedObject_pass() throws ElsaException {
        this.elsa.admin.createIndex(TestModel.class);
        final ConfirmationResponse response = this.elsa.admin.updateMapping(TestModelWithAddedMappings.class);
        assertThat(response.hasSucceeded(), is(true));

        this.elsa.admin.deleteIndex(TestModel.class);
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(false));
    }

    @Test
    public void updateMapping_tryingToOverrideExistingMapping_throw() throws ElsaException {
        this.elsa.admin.createIndex(TestModel.class);

        try {
            this.elsa.admin.updateMapping(TestModelWithInvalidlyModifiedMappings.class);
        } catch (final ElsaException e) {
            assertThat(e.getHttpStatus(), is(400));
        }

        this.elsa.admin.deleteIndex(TestModel.class);
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(false));
    }

    @Test
    public void indexExists_createCheckDeleteCheck_pass() throws ElsaException {
        this.elsa.admin.createIndex(TestModel.class);
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(true));

        this.elsa.admin.deleteIndex(TestModel.class);
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(false));
    }

    @Test
    public void deleteIndexViaClass_indexNewlyCreated_pass() throws ElsaException {
        this.elsa.admin.createIndex(TestModel.class);

        this.elsa.admin.deleteIndex(TestModel.class);
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(false));
    }

    @Test
    @Ignore("fix after ElsaResponse for deleteIndex")
    public void deleteIndexViaClass_indexDoesNotExist_throw() {
        // todo fix test
//        final ExceptionHandlerWithExtractor handler = new ExceptionHandlerWithExtractor();
//        this.elsa.admin.deleteIndex(TestModel.class, handler);
//        assertTrue(handler.getException() instanceof ElasticsearchStatusException);
    }

    @Test
    public void deleteIndexViaString_indexNewlyCreated_pass() throws ElsaException {
        final TestModel testModel = new TestModel();
        this.elsa.admin.createIndex(TestModel.class);

        this.elsa.admin.deleteIndex(testModel.getIndexConfig().getIndexName());
        assertThat(this.elsa.admin.indexExists(TestModel.class), is(false));
    }

    @Test
    @Ignore("fix after ElsaResponse for deleteIndex")
    public void deleteIndexViaString_indexDoesNotExist_throw() {
        // todo fix test
//        final ExceptionHandlerWithExtractor handler = new ExceptionHandlerWithExtractor();
//        final TestModel testModel = new TestModel();
//        this.elsa.admin.deleteIndex(testModel.getIndexConfig().getIndexName(), handler);
//        assertTrue(handler.getException() instanceof ElasticsearchStatusException);
    }

    @Test
    public void createIndex_withDynamicNaming_pass() throws ElsaException {
        final TestModel testModel1 = new TestModel();
        final TestModel testModel2 = new TestModel();
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
        final TestModel testModel1 = new TestModel();
        final TestModel testModel2 = new TestModel();
        assertThat(testModel1.getIndexConfig().getIndexName(), is("elsa_test_index"));

        testModel2.getIndexConfig().setIndexName("new_name");
        assertThat(testModel1.getIndexConfig().getIndexName(), is("new_name"));

        testModel2.getIndexConfig().setIndexName("elsa_test_index");
        assertThat(testModel1.getIndexConfig().getIndexName(), is("elsa_test_index"));
    }
}
