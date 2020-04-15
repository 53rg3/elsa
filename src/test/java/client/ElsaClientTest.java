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

package client;

import assets.*;
import dao.CrudDAO;
import dao.ElsaDAO;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient.FailureListener;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.junit.runners.model.TestTimedOutException;
import responses.ConfirmationResponse;
import responses.ElsaResponse;
import snapshotter.SnapshotRepository;
import statics.Headers;

import java.util.NoSuchElementException;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ElsaClientTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test(expected = NullPointerException.class)
    public void builder_HostsIsNull_throw() {
        new ElsaClient(c -> c
                .setClusterNodes(null));
    }

    @Test(expected = NullPointerException.class)
    public void builder_HostsIsEmpty_throw() {
        final HttpHost[] httpHosts = {};
        new ElsaClient(c -> c
                .setClusterNodes(httpHosts));
    }

    @Test
    public void builder_fullClient_noError() {
        final Header[] defaultHeaders = {new BasicHeader("name", "value")};
        new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .configureLowLevelClient(d -> d
                        .setDefaultHeaders(defaultHeaders)
                        .setFailureListener(new FailureListener())
                        .setMaxRetryTimeoutMillis(10000)
                        .setHttpClientConfigCallback(config -> config
                                .setMaxConnTotal(1)
                                .setUserAgent("MyUserAgent"))
                        .setRequestConfigCallback(config -> config
                                .setAuthenticationEnabled(true)
                                .setConnectTimeout(1000)))
                .registerModel(TestModel.class, TestDAO.class)
                .createIndexesAndEnsureMappingConsistency(false));
    }

    @Test
    public void builder_withIndexNamePrefix_pass() {
        final TestModel model1 = new TestModel();
        final TestModelWithAddedMappings model2 = new TestModelWithAddedMappings();
        final TestModelWithInvalidlyModifiedMappings model3 = new TestModelWithInvalidlyModifiedMappings();
        final String indexNameWithoutPrefix = model1.getIndexConfig().getIndexName();
        final String prefix = "test_";

        new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .configureLowLevelClient(d -> d
                        .setDefaultHeaders(Headers.EMPTY))
                .registerModel(TestModel.class, TestDAO.class)
                .registerModel(TestModelWithAddedMappings.class, TestDAO.class)
                .registerModel(TestModelWithInvalidlyModifiedMappings.class, TestDAO.class)
                .createIndexesAndEnsureMappingConsistency(false)
                .setIndexNamePrefix(prefix));

        // Dynamic indexNames allowed, prefix applied
        assertThat(model1.getIndexConfig().getIndexName(), is(prefix + indexNameWithoutPrefix));
        assertThat(model3.getIndexConfig().getIndexName(), is(prefix + indexNameWithoutPrefix));

        // Dynamic indexNames NOT allowed, no prefix added
        assertThat(model2.getIndexConfig().getIndexName(), is(indexNameWithoutPrefix));

        // Reset indexName
        model1.getIndexConfig().setIndexName(indexNameWithoutPrefix);
        model3.getIndexConfig().setIndexName(indexNameWithoutPrefix);
    }

    @Test
    public void builder_ensureIndexMappingConsistencyWithValidUpdate_pass() {
        final ElsaClient elsa = new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .registerModel(TestModel.class, TestDAO.class));
        assertThat(elsa.admin.indexExists(TestModel.class), is(true));

        new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .registerModel(TestModelWithAddedMappings.class, TestDAO.class));
        // Throws if invalid, no assertion

        elsa.admin.deleteIndex(TestModel.class);
        assertThat(elsa.admin.indexExists(TestModel.class), is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void builder_ensureIndexMappingConsistencyWithInValidUpdate_throw() {
        final ElsaClient elsa = new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .registerModel(TestModel.class, TestDAO.class));
        assertThat(elsa.admin.indexExists(TestModel.class), is(true));

        new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .registerModel(TestModelWithInvalidlyModifiedMappings.class, TestDAO.class));

        elsa.admin.deleteIndex(TestModel.class);
        assertThat(elsa.admin.indexExists(TestModel.class), is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void builder_duplicateModel_throw() {
        new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .configureLowLevelClient(d -> d
                        .setDefaultHeaders(Headers.EMPTY))
                .registerModel(TestModel.class, TestDAO.class)
                .registerModel(TestModel.class, TestDAO.class)
                .createIndexesAndEnsureMappingConsistency(false));
    }

    @Test(expected = IllegalStateException.class)
    public void builder_elsaIndexDataIsNullInModel_throw() {
        new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .registerModel(InvalidModelIndexDataIsNull.class, ElsaDAO.class)
                .createIndexesAndEnsureMappingConsistency(false));
    }

    @Test(expected = IllegalStateException.class)
    public void builder_modelHasInvalidIdGetterOrSetter_throw() {
        new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .registerModel(InvalidModelIdAccessorsWrong.class, ElsaDAO.class)
                .createIndexesAndEnsureMappingConsistency(false));
    }

    // todo delete. stifleThreadUntilClusterIsOnline is gay.
    @Test(timeout = 1000)
    @Ignore("In ElsaClientTest: We need to mock ping()")
    public void waitTillClusterIsOnline_clusterIsOnline_pass() {
//        final ElsaClient elsa = new ElsaClient.Builder(httpHosts)
//                .stifleThreadUntilClusterIsOnline(true)
//                .registerModel(TestModel.class, TestDAO.class)
//                .createIndexesAndEnsureMappingConsistency(false)
//                .build();
//        // Dirty, but if it ever reaches this, then the cluster is online
//        assertThat(elsa, is(notNullValue()));
    }


    @Test(timeout = 1000)
    public void waitTillClusterIsOnline_clusterIsOffline_timeoutException() {
        // If it can't connect in 1sec then then we can assume that the cluster is offline
        // Non-existing cluster
        thrown.expect(TestTimedOutException.class);
        final HttpHost[] httpHosts = {new HttpHost("localhost", 9201, "http")};
        new ElsaClient(c -> c
                .setClusterNodes(httpHosts)
                .stifleThreadUntilClusterIsOnline(true)
                .registerModel(TestModel.class, TestDAO.class)
                .createIndexesAndEnsureMappingConsistency(false));
    }

    @Test(expected = NullPointerException.class)
    public void construct_noModelsRegistered_throw() {
        final HttpHost[] httpHosts = {new HttpHost("localhost", 9201, "http")};
        new ElsaClient(c -> c
                .setClusterNodes(httpHosts));
    }

    @Test
    public void construct_ensureMappingConsistency_pass() {
        new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .registerModel(TestModel.class, TestDAO.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructWithSnapshotRepository_repoIsDuplicate_throw() {
        final ElsaClient elsa = new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .registerModel(TestModel.class, TestDAO.class)
                .createIndexesAndEnsureMappingConsistency(false)
                .registerSnapshotRepositories(d -> d
                        .add(new SnapshotRepository("daily_backups", "/mnt/esbackup1"))
                        .add(new SnapshotRepository("daily_backups", "/mnt/esbackup2"))));
        TestHelpers.sleep(100);
        final ElsaResponse<ConfirmationResponse> response = elsa.snapshotter.deleteRepository("daily_backups");
        assertThat(response.get().hasSucceeded(), is(true));
    }

    @Test(expected = NoSuchElementException.class)
    public void withSnapshotRepository_repositoryNotConfiguredExternally_throw() {
        new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .registerModel(TestModel.class, TestDAO.class)
                .createIndexesAndEnsureMappingConsistency(false)
                .registerSnapshotRepositories(d -> d
                        .add(new SnapshotRepository("daily_backups", "/not_in_elasticsearch_yml"))));
    }

    @Test(expected = NullPointerException.class)
    public void getDao_modelNotRegistered_throw() {
        final ElsaClient elsa = new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .registerModel(TestModel.class, TestDAO.class)
                .createIndexesAndEnsureMappingConsistency(false));
        elsa.getDAO(FakerModel.class);
    }

    @Test(expected = ClassCastException.class)
    public void getDao_daoTypeIsNotCompatible_throw() {
        final ElsaClient elsa = new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .registerModel(TestModel.class, ElsaDAO.class)
                .createIndexesAndEnsureMappingConsistency(false));
        final CrudDAO<TestModel> dao = elsa.getDAO(TestModel.class);
    }
}
