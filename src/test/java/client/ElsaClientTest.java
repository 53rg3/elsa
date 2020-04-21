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
import exceptions.ElsaException;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient.FailureListener;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import responses.ConfirmationResponse;
import snapshotter.SnapshotRepository;
import statics.Headers;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;

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
    public void builder_ensureIndexMappingConsistencyWithValidUpdate_pass() throws ElsaException {
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

    @Test
    public void builder_ensureIndexMappingConsistencyWithInValidUpdate_throw() throws ElsaException {
        final ElsaClient elsa = new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .registerModel(TestModel.class, TestDAO.class));
        assertThat(elsa.admin.indexExists(TestModel.class), is(true));

        try {
            new ElsaClient(c -> c
                    .setClusterNodes(TEST_CLUSTER_HOSTS)
                    .registerModel(TestModelWithInvalidlyModifiedMappings.class, TestDAO.class));
        } catch (final Exception e) {
            assertTrue(e instanceof IllegalStateException);
            assertThat(e.getMessage(), is("Couldn't create indices or update mapping."));
        }

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

    @Test(timeout = 1000)
    public void createIndexesAndMapping_butClusterIsOffline_fail() {
        final HttpHost[] DOES_NOT_EXIST = {new HttpHost("127.0.0.1", 8888, "http")};

        try {
            new ElsaClient(c -> c
                    .setClusterNodes(DOES_NOT_EXIST)
                    .registerModel(TestModel.class, TestDAO.class)
                    .createIndexesAndEnsureMappingConsistency(true));
        } catch (final Exception e) {
            assertTrue(e instanceof IllegalStateException);
            assertThat(e.getMessage(), is("Couldn't create indices or update mapping."));
        }
    }

    /**
     * If we don't use createIndexesAndEnsureMappingConsistency then it doesn't matter if cluster is offline
     */
    @Test(timeout = 1000)
    public void do_not_createIndexesAndMapping_butClusterIsOffline_pass() {
        final HttpHost[] DOES_NOT_EXIST = {new HttpHost("127.0.0.1", 8888, "http")};
        new ElsaClient(c -> c
                .setClusterNodes(DOES_NOT_EXIST)
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
    public void constructWithSnapshotRepository_repoIsDuplicate_throw() throws ElsaException {
        final ElsaClient elsa = new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .registerModel(TestModel.class, TestDAO.class)
                .createIndexesAndEnsureMappingConsistency(false)
                .registerSnapshotRepositories(d -> d
                        .add(new SnapshotRepository("daily_backups", "/mnt/esbackup1"))
                        .add(new SnapshotRepository("daily_backups", "/mnt/esbackup2"))));
        TestHelpers.sleep(100);
        final ConfirmationResponse response = elsa.snapshotter.deleteRepository("daily_backups");
        assertThat(response.hasSucceeded(), is(true));
    }

    @Test(expected = IllegalStateException.class) // throws RuntimeException because program should break, if client misconfigured
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
        final CrudDAO<TestModel> dao = elsa.getDAO(TestModel.class); // DON'T DELETE THE VARIABLE DECLARATION
    }
}
