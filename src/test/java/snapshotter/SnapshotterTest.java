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

package snapshotter;

import assets.FakerModel;
import assets.TestHelpers;
import client.ElsaClient;
import dao.CrudDAO;
import exceptions.ElsaException;
import helpers.IndexName;
import helpers.XJson;
import org.apache.http.HttpHost;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import responses.ConfirmationResponse;
import responses.ElsaResponse;
import responses.RepositoryInfoResponse;
import responses.SnapshotInfoResponse;

import java.util.List;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class SnapshotterTest {

    // ------------------------------------------------------------------------------------------ //
    // VARIABLES
    // ------------------------------------------------------------------------------------------ //
    private static final String snapshot1 = "snapshot1";
    private static final String snapshot2 = "snapshot2";
    private static final String repository1 = "repository1";
    private static final String repository2 = "repository2";
    private static final String repository3 = "repository3";
    private static final String repository_DOES_NOT_EXIST = "repository_non_existing";
    private static final String repositoryLocation_EXISTS = "/mnt/esbackup";
    private static final String repositoryLocation_DOES_NOT_EXIST = "/mnt/does_not_exist";
    private static final String restoredIndexName = "restored_index";

    // ------------------------------------------------------------------------------------------ //
    // CLIENT
    // ------------------------------------------------------------------------------------------ //
    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerModel(FakerModel.class, CrudDAO.class)
            .registerSnapshotRepositories(d -> d
                    .add(new SnapshotRepository(repository3, repositoryLocation_EXISTS))));
    private static final CrudDAO<FakerModel> crudDAO = elsa.getDAO(FakerModel.class);


    @BeforeClass
    public static void setup() {
        final int bulkSize = 100;
        for (int i = 0; i < bulkSize; i++) {
            elsa.bulkProcessor.add(crudDAO.buildIndexRequest(FakerModel.createModelWithRandomData()));
        }
        elsa.bulkProcessor.flush();
        TestHelpers.sleep(1000);
    }

    @AfterClass
    public static void tearDown() throws ElsaException {
//        // Delete indices
        AcknowledgedResponse deleteIndexResponse = elsa.admin.deleteIndex(FakerModel.getIndexName());
        assertThat(deleteIndexResponse.isAcknowledged(), is(true));
        deleteIndexResponse = elsa.admin.deleteIndex(restoredIndexName);
        assertThat(deleteIndexResponse.isAcknowledged(), is(true));

        final ElsaResponse<ConfirmationResponse> deleteRepository = elsa.snapshotter.deleteRepository(repository3);
        assertThat(deleteRepository.get().hasSucceeded(), is(true));
    }


    @Test
    public void completeProcedure_pass() throws ElsaException {

        // ------------------------------------------------------------------------------------------ //
        // CREATE REPOSITORIES
        // ------------------------------------------------------------------------------------------ //
        // repository1
        ElsaResponse<ConfirmationResponse> createRepository = elsa.snapshotter.createRepository(
                new CreateRepositoryRequest(c -> c
                        .repositoryName(repository1)
                        .pathToLocation(repositoryLocation_EXISTS)));
        assertThat(createRepository.get().hasSucceeded(), is(true));

        // repository2
        createRepository = elsa.snapshotter.createRepository(
                new CreateRepositoryRequest(c -> c
                        .repositoryName(repository2)
                        .pathToLocation(repositoryLocation_EXISTS)));
        assertThat(createRepository.get().hasSucceeded(), is(true));
        TestHelpers.sleep(500);

        // non-existing repository
        createRepository = elsa.snapshotter.createRepository(
                new CreateRepositoryRequest(c -> c
                        .repositoryName(repository2)
                        .pathToLocation(repositoryLocation_DOES_NOT_EXIST)));
        assertThat(createRepository.hasException(), is(true));
        assertThat(createRepository.getExceptionResponse().getStatus(), is(500));


        // Get repository1
        final ElsaResponse<RepositoryInfoResponse> getRepositoryByName = elsa.snapshotter.getRepositoryByName(repository1);
        assertThat(getRepositoryByName.get().getName(), is(repository1));
        assertThat(getRepositoryByName.get().getType(), is("fs"));
        assertThat(getRepositoryByName.get().getSettings().getLocation(), is(repositoryLocation_EXISTS));
        assertThat(getRepositoryByName.get().getSettings().getCompress(), is(true));

        // Get all repositories
        final ElsaResponse<List<RepositoryInfoResponse>> getRepositories = elsa.snapshotter.getRepositories();
        assertThat(getRepositories.get().size(), is(3));


        // ------------------------------------------------------------------------------------------ //
        // CREATE SNAPSHOTS
        // ------------------------------------------------------------------------------------------ //
        // snapshot1
        ElsaResponse<ConfirmationResponse> createSnapshot = elsa.snapshotter.createSnapshot(
                new CreateSnapshotRequest(c -> c
                        .indices(IndexName.of(FakerModel.class))
                        .repositoryName(repository1)
                        .snapshotName(snapshot1)
                        .partial(true)));
        TestHelpers.sleep(1000);
        assertThat(createSnapshot.get().hasSucceeded(), is(true));

        // snapshot2
        createSnapshot  = elsa.snapshotter.createSnapshot(
                new CreateSnapshotRequest(c -> c
                        .indices(IndexName.of(FakerModel.class))
                        .repositoryName(repository1)
                        .snapshotName(snapshot2)
                        .partial(true)));
        assertThat(createSnapshot.get().hasSucceeded(), is(true));

        // snapshot in non-existing repository
        createSnapshot  = elsa.snapshotter.createSnapshot(
                new CreateSnapshotRequest(c -> c
                        .indices(IndexName.of(FakerModel.class))
                        .repositoryName(repository_DOES_NOT_EXIST)
                        .snapshotName(snapshot2)
                        .partial(true)));
        assertThat(createSnapshot.hasException(), is(true));
        assertThat(createSnapshot.getExceptionResponse().getStatus(), is(404));


        ElsaResponse<SnapshotInfoResponse> getSnapshotByName = elsa.snapshotter.getSnapshotByName(repository1, snapshot1);
        assertThat(getSnapshotByName.get().getName(), is(snapshot1));
        assertThat(getSnapshotByName.get().getEndTime(), notNullValue());
        assertThat(getSnapshotByName.get().getShards().getFailed(), is(0));
        assertThat(getSnapshotByName.get().getEndTimeInMillis(), notNullValue());

        final ElsaResponse<List<SnapshotInfoResponse>> getSnapshots = elsa.snapshotter.getSnapshots(repository1);
        assertThat(getSnapshots.get().size(), is(2));

        getSnapshotByName = elsa.snapshotter.getSnapshotByName(repository1, snapshot1+"does_not_exist");
        assertThat(getSnapshotByName.hasException(), is(true));
        assertThat(getSnapshotByName.getExceptionResponse().getStatus(), is(404));


        // ------------------------------------------------------------------------------------------ //
        // RESTORE
        // ------------------------------------------------------------------------------------------ //
        // Restore snapshot
        final ElsaResponse<ConfirmationResponse> restoreSnapshot = elsa.snapshotter.restoreSnapshot(
                new RestoreSnapshotRequest(c -> c
                        .indices(FakerModel.getIndexName())
                        .repositoryName(repository1)
                        .snapshotName(snapshot1)
                        .includeAliases(true)
                        .ignoreUnavailable(false)
                        .overrideDynamicIndexSettings(new XJson()
                                .field("index.refresh_interval", "1s"))
                        .includeGlobalState(false)
                        .renamePattern(FakerModel.getIndexName())
                        .renameReplacement(restoredIndexName)));
        assertThat(restoreSnapshot.get().hasSucceeded(), is(true));
        TestHelpers.sleep(1000);
        assertTrue(elsa.admin.indexExists(restoredIndexName));


        // ------------------------------------------------------------------------------------------ //
        // DELETE EVERYTHING
        // ------------------------------------------------------------------------------------------ //
        // Delete snapshots
        ElsaResponse<ConfirmationResponse> deleteSnapshot = elsa.snapshotter.deleteSnapshot(repository1, snapshot1);
        assertThat(deleteSnapshot.get().hasSucceeded(), is(true));
        deleteSnapshot = elsa.snapshotter.deleteSnapshot(repository1, snapshot2);
        assertThat(deleteSnapshot.get().hasSucceeded(), is(true));
        // Non existing snapshot
        deleteSnapshot = elsa.snapshotter.deleteSnapshot(repository1, snapshot2+"non_existing");
        assertThat(deleteSnapshot.hasException(), is(true));
        assertThat(deleteSnapshot.getExceptionResponse().getStatus(), is(404));

        // Delete repositories
        ElsaResponse<ConfirmationResponse> deleteRepository = elsa.snapshotter.deleteRepository(repository1);
        assertThat(deleteRepository.get().hasSucceeded(), is(true));
        deleteRepository = elsa.snapshotter.deleteRepository(repository2);
        assertThat(deleteRepository.get().hasSucceeded(), is(true));
        // Non-existing repository
        deleteRepository = elsa.snapshotter.deleteRepository(repository2+"non_existing");
        assertThat(deleteRepository.hasException(), is(true));
        assertThat(deleteRepository.getExceptionResponse().getStatus(), is(404));

    }

}
