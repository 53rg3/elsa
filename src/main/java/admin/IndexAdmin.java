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

import client.ElsaClient;
import endpoints.Endpoint;
import exceptions.ElsaElasticsearchException;
import exceptions.ElsaException;
import exceptions.ElsaIOException;
import helpers.IndexName;
import helpers.RequestBody;
import model.ElsaModel;
import model.IndexConfig;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import responses.ConfirmationResponse;
import responses.ResponseFactory;
import statics.ElsaStatics;
import statics.Method;

import java.io.IOException;

public class IndexAdmin {

    private final ElsaClient elsa;

    public IndexAdmin(final ElsaClient elsa) {
        this.elsa = elsa;
    }


    // ------------------------------------------------------------------------------------------ //
    // CREATE INDEX
    // ------------------------------------------------------------------------------------------ //

    public CreateIndexResponse createIndex(final Class<? extends ElsaModel> modelClass,
                                           final IndexConfig indexConfig,
                                           final RequestOptions options) throws ElsaException {
        try {
            final XContentBuilder mapping = MappingBuilder.buildMapping(modelClass, ElsaStatics.DUMMY_TYPE, ElsaStatics.DEFAULT_ID_FIELD_NAME, "");
            final CreateIndexRequest request = new CreateIndexRequest();
            request.index(indexConfig.getIndexName());
            final Settings settings = Settings.builder()
                    .put("index.number_of_shards", indexConfig.getShards())
                    .put("index.number_of_replicas", indexConfig.getReplicas())
                    .put("index.refresh_interval", indexConfig.getRefreshInterval().toString())
                    .build();
            request.settings(settings);
            request.mapping(ElsaStatics.DUMMY_TYPE, mapping);
            return this.elsa.client.indices().create(request, options);

        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    public CreateIndexResponse createIndex(final Class<? extends ElsaModel> modelClass,
                                           final IndexConfig indexConfig) throws ElsaException {
        return this.createIndex(modelClass, indexConfig, RequestOptions.DEFAULT);
    }


    // ------------------------------------------------------------------------------------------ //
    // UPDATE MAPPING
    // ------------------------------------------------------------------------------------------ //

    public ConfirmationResponse updateMapping(final Class<? extends ElsaModel> modelClass,
                                              final RequestOptions options) throws ElsaException {
        try {
            final String indexName = IndexName.of(modelClass);
            final XContentBuilder xContentBuilder = MappingBuilder.buildMapping(modelClass, "_doc", "", "");

            final Request request = new Request(Method.PUT, Endpoint.INDEX_MAPPING.update(indexName));
            request.setEntity(RequestBody.asJson(xContentBuilder));
            request.setOptions(options);
            final Response response = this.elsa.client.getLowLevelClient().performRequest(request);

            return ResponseFactory.createConfirmationResponse(response);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    public ConfirmationResponse updateMapping(final Class<? extends ElsaModel> modelClass) throws ElsaException {
        return this.updateMapping(modelClass, RequestOptions.DEFAULT);
    }


    public boolean indexExists(final String indexName, final RequestOptions options) throws ElsaException {
        try {
            final Request request = new Request(Method.HEAD, indexName);
            request.setOptions(options);

            final Response response = this.elsa.client.getLowLevelClient().performRequest(request);
            return response.getStatusLine().getStatusCode() == 200;
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    public boolean indexExists(final String indexName) throws ElsaException {
        return this.indexExists(indexName, RequestOptions.DEFAULT);
    }

    public boolean indexExists(final Class<? extends ElsaModel> modelClass, final RequestOptions options) throws ElsaException {
        return this.indexExists(IndexName.of(modelClass), options);
    }

    public boolean indexExists(final Class<? extends ElsaModel> modelClass) throws ElsaException {
        return this.indexExists(IndexName.of(modelClass), RequestOptions.DEFAULT);
    }


    public AcknowledgedResponse deleteIndex(final String indexName, final RequestOptions options) throws ElsaException {
        try {
            return this.elsa.client.indices().delete(new DeleteIndexRequest(indexName), options);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    public AcknowledgedResponse deleteIndex(final String indexName) throws ElsaException {
        return this.deleteIndex(indexName, RequestOptions.DEFAULT);
    }

    public AcknowledgedResponse deleteIndex(final Class<? extends ElsaModel> modelClass, final RequestOptions options) throws ElsaException {
        return this.deleteIndex(IndexName.of(modelClass), options);
    }

    public AcknowledgedResponse deleteIndex(final Class<? extends ElsaModel> modelClass) throws ElsaException {
        return this.deleteIndex(IndexName.of(modelClass));
    }
}
