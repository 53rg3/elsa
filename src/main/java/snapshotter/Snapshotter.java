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

import client.ElsaClient;
import endpoints.Endpoint;
import helpers.RequestBody;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import responses.*;
import statics.Method;
import statics.UrlParams;

import java.util.List;

public class Snapshotter {

    private final ElsaClient elsa;
    private final RepositoryBucket repositoryBucket;

    public Snapshotter(final ElsaClient elsa, final RepositoryBucket repositoryBucket) {
        this.elsa = elsa;
        this.repositoryBucket = repositoryBucket;
    }


    // ------------------------------------------------------------------------------------------ //
    // INFO
    // ------------------------------------------------------------------------------------------ //
    public ElsaResponse<List<RepositoryInfoResponse>> getRepositories(final RequestOptions options) {
        try {
            final Response response = this.elsa.client.getLowLevelClient().performRequest( // todo use Request
                    Method.GET,
                    Endpoint.SNAPSHOT.INFO.getRepositories(),
                    UrlParams.NONE);
            return ElsaResponse.of(ResponseFactory.createRepositoryInfoListResponse(response));
        } catch (final Exception e) {
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<List<RepositoryInfoResponse>> getRepositories() {
        return this.getRepositories(RequestOptions.DEFAULT);
    }


    public ElsaResponse<RepositoryInfoResponse> getRepositoryByName(final String repositoryName, final RequestOptions options) {
        try {
            final Response response = this.elsa.client.getLowLevelClient().performRequest( // todo use Request
                    Method.GET,
                    Endpoint.SNAPSHOT.INFO.getRepositoryByName(repositoryName),
                    UrlParams.NONE);
            return ElsaResponse.of(ResponseFactory.createRepositoryInfoResponse(response));
        } catch (final Exception e) {
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<RepositoryInfoResponse> getRepositoryByName(final String repositoryName) {
        return this.getRepositoryByName(repositoryName, RequestOptions.DEFAULT);
    }


    public ElsaResponse<List<SnapshotInfoResponse>> getSnapshots(final String repositoryName, final RequestOptions options) {
        try {
            final Response response = this.elsa.client.getLowLevelClient().performRequest( // todo use Request
                    Method.GET,
                    Endpoint.SNAPSHOT.INFO.getSnapshots(repositoryName),
                    UrlParams.NONE);
            return ElsaResponse.of(ResponseFactory.createSnapshotInfoListResponse(response));
        } catch (final Exception e) {
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<List<SnapshotInfoResponse>> getSnapshots(final String repositoryName) {
        return this.getSnapshots(repositoryName, RequestOptions.DEFAULT);
    }

    public ElsaResponse<SnapshotInfoResponse> getSnapshotByName(final String repositoryName, final String snapshotName, final RequestOptions options) {
        try {
            final Response response = this.elsa.client.getLowLevelClient().performRequest( // todo use Request
                    Method.GET,
                    Endpoint.SNAPSHOT.INFO.getSnapshotByName(repositoryName, snapshotName),
                    UrlParams.NONE);
            return ElsaResponse.of(ResponseFactory.createSnapshotInfoResponse(response));
        } catch (final Exception e) {
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<SnapshotInfoResponse> getSnapshotByName(final String repositoryName, final String snapshotName) {
        return this.getSnapshotByName(repositoryName, snapshotName, RequestOptions.DEFAULT);
    }


    // ------------------------------------------------------------------------------------------ //
    // CREATE
    // ------------------------------------------------------------------------------------------ //

    public ElsaResponse<ConfirmationResponse> createRepository(final CreateRepositoryRequest request, final RequestOptions options) {
        try {
            final Response response = this.elsa.client.getLowLevelClient().performRequest( // todo use Request
                    Method.PUT,
                    Endpoint.SNAPSHOT.CREATE.createRepository(request.getRepositoryName()),
                    UrlParams.NONE,
                    RequestBody.asJson(request));
            return ElsaResponse.of(ResponseFactory.createConfirmationResponse(response));
        } catch (final Exception e) {
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<ConfirmationResponse> createRepository(final CreateRepositoryRequest request) {
        return this.createRepository(request, RequestOptions.DEFAULT);
    }

    public ElsaResponse<ConfirmationResponse> createSnapshot(final CreateSnapshotRequest request, final RequestOptions options) {
        try {
            final Response response = this.elsa.client.getLowLevelClient().performRequest( // todo use Request
                    Method.PUT,
                    Endpoint.SNAPSHOT.CREATE.createSnapshot(request.getRepositoryName(), request.getSnapshotName()),
                    UrlParams.NONE,
                    RequestBody.asJson(request));
            return ElsaResponse.of(ResponseFactory.createConfirmationResponse(response));
        } catch (final Exception e) {
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<ConfirmationResponse> createSnapshot(final CreateSnapshotRequest request) {
        return this.createSnapshot(request, RequestOptions.DEFAULT);
    }


    // ------------------------------------------------------------------------------------------ //
    // RESTORE
    // ------------------------------------------------------------------------------------------ //

    public ElsaResponse<ConfirmationResponse> restoreSnapshot(final RestoreSnapshotRequest request, final RequestOptions options) {
        try {
            final Response response = this.elsa.client.getLowLevelClient().performRequest( // todo use Request
                    Method.POST,
                    Endpoint.SNAPSHOT.RESTORE.restoreSnapshot(request.getRepositoryName(), request.getSnapshotName()),
                    UrlParams.NONE,
                    RequestBody.asJson(request.getXJson()));
            return ElsaResponse.of(ResponseFactory.createConfirmationResponse(response));
        } catch (final Exception e) {
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<ConfirmationResponse> restoreSnapshot(final RestoreSnapshotRequest request) {
        return this.restoreSnapshot(request, RequestOptions.DEFAULT);
    }


    // ------------------------------------------------------------------------------------------ //
    // DELETE
    // ------------------------------------------------------------------------------------------ //

    public ElsaResponse<ConfirmationResponse> deleteSnapshot(final String repositoryName, final String snapshotName, final RequestOptions options) {
        try {
            final Response response = this.elsa.client.getLowLevelClient().performRequest( // todo use Request
                    Method.DELETE,
                    Endpoint.SNAPSHOT.DELETE.deleteSnapshot(repositoryName, snapshotName),
                    UrlParams.NONE);
            return ElsaResponse.of(ResponseFactory.createConfirmationResponse(response));
        } catch (final Exception e) {
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<ConfirmationResponse> deleteSnapshot(final String repositoryName, final String snapshotName) {
        return this.deleteSnapshot(repositoryName, snapshotName, RequestOptions.DEFAULT);
    }

    public ElsaResponse<ConfirmationResponse> deleteRepository(final String repositoryName, final RequestOptions options) {
        try {
            final Response response =  this.elsa.client.getLowLevelClient().performRequest( // todo use Request
                    Method.DELETE,
                    Endpoint.SNAPSHOT.DELETE.deleteRepository(repositoryName),
                    UrlParams.NONE);
            return ElsaResponse.of(ResponseFactory.createConfirmationResponse(response));
        } catch (final Exception e) {
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<ConfirmationResponse> deleteRepository(final String repositoryName) {
        return this.deleteRepository(repositoryName, RequestOptions.DEFAULT);
    }


    // ------------------------------------------------------------------------------------------ //
    // GETTER
    // ------------------------------------------------------------------------------------------ //

    public RepositoryBucket getRepositoryBucket() {
        return this.repositoryBucket;
    }
}
