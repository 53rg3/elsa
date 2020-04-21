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
import exceptions.ElsaElasticsearchException;
import exceptions.ElsaException;
import exceptions.ElsaIOException;
import helpers.RequestBody;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import responses.ConfirmationResponse;
import responses.RepositoryInfoResponse;
import responses.ResponseFactory;
import responses.SnapshotInfoResponse;
import statics.Method;

import java.io.IOException;
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
    public List<RepositoryInfoResponse> getRepositories(final RequestOptions options) throws ElsaException {
        try {
            final Request request = new Request(Method.GET, Endpoint.SNAPSHOT.INFO.getRepositories());
            request.setOptions(options);

            final Response response = this.elsa.client.getLowLevelClient().performRequest(request);

            return ResponseFactory.createRepositoryInfoListResponse(response);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    public List<RepositoryInfoResponse> getRepositories() throws ElsaException {
        return this.getRepositories(RequestOptions.DEFAULT);
    }


    public RepositoryInfoResponse getRepositoryByName(final String repositoryName,
                                                      final RequestOptions options) throws ElsaException {
        try {
            final Request request = new Request(Method.GET, Endpoint.SNAPSHOT.INFO.getRepositoryByName(repositoryName));
            request.setOptions(options);

            final Response response = this.elsa.client.getLowLevelClient().performRequest(request);

            return ResponseFactory.createRepositoryInfoResponse(response);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    public RepositoryInfoResponse getRepositoryByName(final String repositoryName) throws ElsaException {
        return this.getRepositoryByName(repositoryName, RequestOptions.DEFAULT);
    }


    public List<SnapshotInfoResponse> getSnapshots(final String repositoryName,
                                                   final RequestOptions options) throws ElsaException {
        try {
            final Request request = new Request(Method.GET, Endpoint.SNAPSHOT.INFO.getSnapshots(repositoryName));
            request.setOptions(options);

            final Response response = this.elsa.client.getLowLevelClient().performRequest(request);

            return ResponseFactory.createSnapshotInfoListResponse(response);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    public List<SnapshotInfoResponse> getSnapshots(final String repositoryName) throws ElsaException {
        return this.getSnapshots(repositoryName, RequestOptions.DEFAULT);
    }

    public SnapshotInfoResponse getSnapshotByName(final String repositoryName, final String snapshotName,
                                                  final RequestOptions options) throws ElsaException {
        try {
            final Request request = new Request(Method.GET, Endpoint.SNAPSHOT.INFO.getSnapshotByName(repositoryName, snapshotName));
            request.setOptions(options);

            final Response response = this.elsa.client.getLowLevelClient().performRequest(request);

            return ResponseFactory.createSnapshotInfoResponse(response);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    public SnapshotInfoResponse getSnapshotByName(final String repositoryName,
                                                  final String snapshotName) throws ElsaException {
        return this.getSnapshotByName(repositoryName, snapshotName, RequestOptions.DEFAULT);
    }


    // ------------------------------------------------------------------------------------------ //
    // CREATE
    // ------------------------------------------------------------------------------------------ //

    public ConfirmationResponse createRepository(final CreateRepositoryRequest createRepositoryRequest,
                                                 final RequestOptions options) throws ElsaException {
        try {
            final Request request = new Request(Method.PUT,
                    Endpoint.SNAPSHOT.CREATE.createRepository(createRepositoryRequest.getRepositoryName()));
            request.setOptions(options);
            request.setEntity(RequestBody.asJson(createRepositoryRequest));

            final Response response = this.elsa.client.getLowLevelClient().performRequest(request);

            return ResponseFactory.createConfirmationResponse(response);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    public ConfirmationResponse createRepository(final CreateRepositoryRequest request) throws ElsaException {
        return this.createRepository(request, RequestOptions.DEFAULT);
    }

    public ConfirmationResponse createSnapshot(final CreateSnapshotRequest createSnapshotRequest,
                                               final RequestOptions options) throws ElsaException {
        try {
            final Request request = new Request(Method.PUT,
                    Endpoint.SNAPSHOT.CREATE.createSnapshot(createSnapshotRequest.getRepositoryName(), createSnapshotRequest.getSnapshotName()));
            request.setOptions(options);
            request.setEntity(RequestBody.asJson(createSnapshotRequest));

            final Response response = this.elsa.client.getLowLevelClient().performRequest(request);

            return ResponseFactory.createConfirmationResponse(response);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    public ConfirmationResponse createSnapshot(final CreateSnapshotRequest request) throws ElsaException {
        return this.createSnapshot(request, RequestOptions.DEFAULT);
    }


    // ------------------------------------------------------------------------------------------ //
    // RESTORE
    // ------------------------------------------------------------------------------------------ //

    public ConfirmationResponse restoreSnapshot(final RestoreSnapshotRequest restoreSnapshotRequest,
                                                final RequestOptions options) throws ElsaException {
        try {
            final Request request = new Request(Method.POST,
                    Endpoint.SNAPSHOT.RESTORE.restoreSnapshot(restoreSnapshotRequest.getRepositoryName(), restoreSnapshotRequest.getSnapshotName()));
            request.setOptions(options);
            request.setEntity(RequestBody.asJson(restoreSnapshotRequest.getXJson()));

            final Response response = this.elsa.client.getLowLevelClient().performRequest(request);

            return ResponseFactory.createConfirmationResponse(response);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    public ConfirmationResponse restoreSnapshot(final RestoreSnapshotRequest request) throws ElsaException {
        return this.restoreSnapshot(request, RequestOptions.DEFAULT);
    }


    // ------------------------------------------------------------------------------------------ //
    // DELETE
    // ------------------------------------------------------------------------------------------ //

    public ConfirmationResponse deleteSnapshot(final String repositoryName, final String snapshotName,
                                               final RequestOptions options) throws ElsaException {
        try {
            final Request request = new Request(Method.DELETE,
                    Endpoint.SNAPSHOT.DELETE.deleteSnapshot(repositoryName, snapshotName));
            request.setOptions(options);

            final Response response = this.elsa.client.getLowLevelClient().performRequest(request);

            return ResponseFactory.createConfirmationResponse(response);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    public ConfirmationResponse deleteSnapshot(final String repositoryName, final String snapshotName) throws ElsaException {
        return this.deleteSnapshot(repositoryName, snapshotName, RequestOptions.DEFAULT);
    }

    public ConfirmationResponse deleteRepository(final String repositoryName, final RequestOptions options) throws ElsaException {
        try {
            final Request request = new Request(Method.DELETE, Endpoint.SNAPSHOT.DELETE.deleteRepository(repositoryName));
            request.setOptions(options);

            final Response response = this.elsa.client.getLowLevelClient().performRequest(request);

            return ResponseFactory.createConfirmationResponse(response);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    public ConfirmationResponse deleteRepository(final String repositoryName) throws ElsaException {
        return this.deleteRepository(repositoryName, RequestOptions.DEFAULT);
    }


    // ------------------------------------------------------------------------------------------ //
    // GETTER
    // ------------------------------------------------------------------------------------------ //

    public RepositoryBucket getRepositoryBucket() {
        return this.repositoryBucket;
    }
}
