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

package io.github.ss3rg3.elsa.admin;

import assets.*;
import io.github.ss3rg3.elsa.ElsaClient;
import com.google.common.io.ByteStreams;
import io.github.ss3rg3.elsa.dao.DaoConfig;
import io.github.ss3rg3.elsa.exceptions.ElsaException;
import io.github.ss3rg3.elsa.model.IndexConfig;

import org.elasticsearch.client.indices.CreateIndexResponse;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import io.github.ss3rg3.elsa.responses.ConfirmationResponse;

import java.net.URL;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IndexAdminTest {

    private final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerDAO(new DaoConfig(TestDAO.class, TestModel.indexConfig))
            .createIndexesAndEnsureMappingConsistency(false));

    @Test
    public void createIndex_indexDoesNotExist_pass() throws ElsaException {
        final CreateIndexResponse response = this.elsa.admin.createIndex(TestModel.indexConfig);
        assertThat(response.isAcknowledged(), is(true));
        assertThat(response.isShardsAcknowledged(), is(true));
        assertThat(this.elsa.admin.indexExists(TestModel.indexConfig), is(true));

        this.elsa.admin.deleteIndex(TestModel.indexConfig);
        assertThat(this.elsa.admin.indexExists(TestModel.indexConfig), is(false));
    }

    @Test
    public void updateMapping_validMappingWithNestedObject_pass() throws ElsaException {
        this.elsa.admin.createIndex(TestModel.indexConfig);
        final ConfirmationResponse response = this.elsa.admin.updateMapping(TestModelWithAddedMappings.indexConfig);
        assertThat(response.hasSucceeded(), is(true));

        this.elsa.admin.deleteIndex(TestModel.indexConfig);
        assertThat(this.elsa.admin.indexExists(TestModel.indexConfig), is(false));
    }

    @Test
    public void updateMapping_tryingToOverrideExistingMapping_throw() throws ElsaException {
        this.elsa.admin.createIndex(TestModel.indexConfig);

        try {
            this.elsa.admin.updateMapping(TestModelWithInvalidlyModifiedMappings.indexConfig);
        } catch (final ElsaException e) {
            assertThat(e.getHttpStatus(), is(400));
        } finally {
            this.elsa.admin.deleteIndex(TestModel.indexConfig);
            assertThat(this.elsa.admin.indexExists(TestModel.indexConfig), is(false));
        }
    }

    @Test
    public void indexExists_createCheckDeleteCheck_pass() throws ElsaException {
        this.elsa.admin.createIndex(TestModel.indexConfig);
        assertThat(this.elsa.admin.indexExists(TestModel.indexConfig), is(true));

        this.elsa.admin.deleteIndex(TestModel.indexConfig);
        assertThat(this.elsa.admin.indexExists(TestModel.indexConfig), is(false));
    }

    @Test
    public void deleteIndexViaClass_indexNewlyCreated_pass() throws ElsaException {
        this.elsa.admin.createIndex(TestModel.indexConfig);

        this.elsa.admin.deleteIndex(TestModel.indexConfig);
        assertThat(this.elsa.admin.indexExists(TestModel.indexConfig), is(false));
    }

    @Test
    public void deleteIndexViaClass_indexDoesNotExist_throw() {
        try {
            this.elsa.admin.deleteIndex(TestModel.indexConfig);
        } catch (final ElsaException e) {
            assertThat(e.getHttpStatus(), is(404));
        }
    }

    @Test
    public void deleteIndexViaString_indexNewlyCreated_pass() throws ElsaException {
        this.elsa.admin.createIndex(TestModel.indexConfig);

        this.elsa.admin.deleteIndex(TestModel.indexConfig);
        assertThat(this.elsa.admin.indexExists(TestModel.indexConfig), is(false));
    }

    @Test
    public void deleteIndexViaString_indexDoesNotExist_throw() {
        try {
            this.elsa.admin.deleteIndex(TestModel.indexConfig);
        } catch (final ElsaException e) {
            assertThat(e.getHttpStatus(), is(404));
        }
    }

    @Test
    public void createIndex_withDynamicNaming_pass() throws ElsaException {
        final String elsa_test_index = "elsa_test_index";
        final String new_name = "new_name";
        final IndexConfig elsaTestIndexConfig = new IndexConfig(c -> c
                .indexName(elsa_test_index)
                .mappingClass(TestModel.class)
                .shards(1)
                .replicas(0));
        final IndexConfig newNameConfig = new IndexConfig(c -> c
                .indexName(new_name)
                .mappingClass(TestModel.class)
                .shards(1)
                .replicas(0));
        assertThat(elsaTestIndexConfig.getIndexName(), is(elsa_test_index));

        this.elsa.admin.createIndex(elsaTestIndexConfig);
        assertThat(this.elsa.admin.indexExists(elsa_test_index), is(true));

        this.elsa.admin.createIndex(newNameConfig);
        assertThat(this.elsa.admin.indexExists(new_name), is(true));

        this.elsa.admin.deleteIndex(elsa_test_index);
        this.elsa.admin.deleteIndex(new_name);
    }

    @Test
    public void customIndexSettings() throws Exception {
        final String indexName = "test_index";
        try {
            this.elsa.admin.deleteIndex(indexName);
        } catch (ElsaException e) {
            // NO OP
        }

        this.elsa.admin.createIndex(new IndexConfig(c -> c
                .mappingClass(TestModel.class)
                .indexName(indexName)
                .replicas(0)
                .shards(1)
                .addIndexSetting("index.codec", "best_compression")));

        final URL url = new URL("http://127.0.0.1:9200/" + indexName + "/_settings");
        final byte[] bytes = ByteStreams.toByteArray(url.openConnection().getInputStream());
        assertThat("Expected to find \"codec\":\"best_compression\" in index settings info. See " + url,
                new String(bytes).contains("\"codec\":\"best_compression\""), is(true));

        this.elsa.admin.deleteIndex(indexName);
    }

}
