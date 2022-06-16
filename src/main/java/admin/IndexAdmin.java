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
import helpers.RequestBody;
import model.IndexConfig;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.xcontent.XContentFactory;
import org.elasticsearch.xcontent.XContentType;
import responses.ConfirmationResponse;
import responses.ResponseFactory;
import statics.Method;

import java.io.IOException;

public class IndexAdmin {

    private final ElsaClient elsa;
    private final MappingBuilder mappingBuilder;

    public IndexAdmin(final ElsaClient elsa) {
        this.elsa = elsa;
        this.mappingBuilder = new MappingBuilder();
    }


    // ------------------------------------------------------------------------------------------ //
    // CREATE INDEX
    // ------------------------------------------------------------------------------------------ //

    public CreateIndexResponse createIndex(final IndexConfig indexConfig,
                                           final RequestOptions options) throws ElsaException {
        try {
            final String mapping = this.mappingBuilder.createMapping(indexConfig.getMappingClass());
            final CreateIndexRequest request = new CreateIndexRequest(indexConfig.getIndexName());
            request.index();
            final Settings settings = this.createSettings(indexConfig);
            request.settings(settings);
            request.mapping(mapping, XContentType.JSON);
            return this.elsa.client.indices().create(request, options);

        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    private Settings createSettings(final IndexConfig indexConfig) {
        final Settings.Builder settings = Settings.builder()
                .put("index.number_of_shards", indexConfig.getShards())
                .put("index.number_of_replicas", indexConfig.getReplicas())
                .put("index.refresh_interval", indexConfig.getRefreshInterval().toString());

        // Add custom settings
        indexConfig.getSettings().forEach((settingsName, value) -> {

            if (value instanceof Boolean) {
                settings.put(settingsName, (boolean) value);
            } else if (value instanceof Integer) {
                settings.put(settingsName, (int) value);
            } else if (value instanceof Double) {
                settings.put(settingsName, (double) value);
            } else if (value instanceof String) {
                String copy = (String) value;
                copy = copy.trim();
                settings.put(settingsName, copy);
            } else {
                throw new IllegalStateException("Unhandled type: " + value.getClass() + ", for setting " + settingsName);
            }
        });

        return settings.build();
    }

    public CreateIndexResponse createIndex(final IndexConfig indexConfig) throws ElsaException {
        return this.createIndex(indexConfig, RequestOptions.DEFAULT);
    }


    // ------------------------------------------------------------------------------------------ //
    // UPDATE MAPPING
    // ------------------------------------------------------------------------------------------ //

    public ConfirmationResponse updateMapping(final IndexConfig indexConfig,
                                              final RequestOptions options) throws ElsaException {
        try {
            final String indexName = indexConfig.getIndexName();
            final String mapping = this.mappingBuilder.createMapping(indexConfig.getMappingClass());

            final Request request = new Request(Method.PUT, Endpoint.INDEX_MAPPING.update(indexName));
            request.setEntity(RequestBody.asJson(mapping));
            request.setOptions(options);
            final Response response = this.elsa.client.getLowLevelClient().performRequest(request);

            return ResponseFactory.createConfirmationResponse(response);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    public ConfirmationResponse updateMapping(final IndexConfig indexConfig) throws ElsaException {
        return this.updateMapping(indexConfig, RequestOptions.DEFAULT);
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

    public boolean indexExists(final IndexConfig indexConfig, final RequestOptions options) throws ElsaException {
        return this.indexExists(indexConfig.getIndexName(), options);
    }

    public boolean indexExists(final IndexConfig indexConfig) throws ElsaException {
        return this.indexExists(indexConfig.getIndexName(), RequestOptions.DEFAULT);
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

    public AcknowledgedResponse deleteIndex(final IndexConfig indexConfig, final RequestOptions options) throws ElsaException {
        return this.deleteIndex(indexConfig.getIndexName(), options);
    }

    public AcknowledgedResponse deleteIndex(final IndexConfig indexConfig) throws ElsaException {
        return this.deleteIndex(indexConfig.getIndexName());
    }
}
