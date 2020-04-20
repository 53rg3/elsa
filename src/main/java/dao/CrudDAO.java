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

package dao;

import client.ElsaClient;
import exceptions.ElsaElasticsearchException;
import exceptions.ElsaException;
import exceptions.ElsaIOException;
import model.ElsaModel;
import model.IndexConfig;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import responses.ElsaResponse;
import statics.ElsaStatics;

import java.io.IOException;
import java.util.Objects;

public class CrudDAO<T extends ElsaModel> extends SearchDAO<T> {

    private final IndexConfig indexConfig;

    private static final Logger logger = LoggerFactory.getLogger(CrudDAO.class);

    public CrudDAO(final Class<T> model, final ElsaClient elsa) {
        super(model, elsa);
        this.indexConfig = this.setIndexConfig(model);
    }


    // ------------------------------------------------------------------------------------------ //
    // INDEX
    // ------------------------------------------------------------------------------------------ //

    /**
     * Indexes the model object.
     * If the model has an ID, then it will be used for the _id field, i.e. it sets a custom ID.
     * Otherwise Elasticsearch set the ID automatically. <br>
     * If the ID exists then this will update the document.
     */
    public IndexResponse index(final T model) throws ElsaException {
        return this.index(model, RequestOptions.DEFAULT);
    }

    public IndexResponse index(final T model, final RequestOptions options) throws ElsaException {
        Objects.requireNonNull(model, "Model must not be NULL.");
        try {
            return this.getElsa().client.index(this.buildIndexRequest(model), options);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    /**
     * Indexes the model object. Async. Response will be send to the Listener.
     * If the model has an ID, then it will be used for the _id field, i.e. it sets a custom ID.
     * Otherwise Elasticsearch set the ID automatically. <br>
     * If the ID exists then this will update the document.
     */
    public void indexAsync(final T model, final RequestOptions options, final ActionListener<IndexResponse> listener) {
        this.getElsa().client.indexAsync(this.buildIndexRequest(model), options, listener);
    }

    public IndexRequest buildIndexRequest(final T model) {
        if (model.getId() == null) {
            return new IndexRequest(this.indexConfig.getIndexName(), ElsaStatics.DUMMY_TYPE)
                    .source(this.getJsonMapper().toJson(model), XContentType.JSON);
        } else {
            return new IndexRequest(this.indexConfig.getIndexName(), ElsaStatics.DUMMY_TYPE, this.getIdOrThrow(model.getId()))
                    .source(this.getJsonMapper().toJson(model), XContentType.JSON);
        }
    }


    // ------------------------------------------------------------------------------------------ //
    // GET
    // ------------------------------------------------------------------------------------------ //

    /**
     * This retrieves the document and maps it onto your model.
     * @return sub-type of ElsaModel or empty ElsaResponse
     */
    public T get(final String id) throws ElsaException {
        return this.get(id, RequestOptions.DEFAULT);
    }

    public T get(final String id, final RequestOptions options) throws ElsaException {
        try {
            final GetResponse response = this.getElsa().client.get(
                    new GetRequest(this.indexConfig.getIndexName(), ElsaStatics.DUMMY_TYPE, id),
                    options
            );
            T model = null;
            if (response.isExists()) {
                model = this.getJsonMapper().fromJson(new String(response.getSourceAsBytes(), ElsaStatics.UTF_8)); // todo delete & replace
                model.setId(response.getId());
            }
            return model; // todo throw or return null?
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    /**
     * This return the native GetResponse result of the GetRequest, which has additional fields and methods.
     * Object mapping must be done manually.
     * */
    public GetResponse getRawResponse(final String id) throws ElsaException {
        return this.getRawResponse(id, RequestOptions.DEFAULT);
    }

    public GetResponse getRawResponse(final String id, final RequestOptions options) throws ElsaException {
        final GetRequest request = new GetRequest(this.indexConfig.getIndexName(), ElsaStatics.DUMMY_TYPE, id);
        try {
            return this.getElsa().client.get(request, options);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    /** This retrieves the document asynchronously. GetResponse will be send to the Listener and needs to be mapped there. */
    public void getAsync(final String id, final RequestOptions options, final ActionListener<GetResponse> listener) {
        this.getElsa().client.getAsync(
                new GetRequest(this.indexConfig.getIndexName(), ElsaStatics.DUMMY_TYPE, id),
                options,
                listener
        );
    }


    // ------------------------------------------------------------------------------------------ //
    // DELETE
    // ------------------------------------------------------------------------------------------ //

    public DeleteResponse delete(final T model) throws ElsaException {
        return this.delete(model, RequestOptions.DEFAULT);
    }

    public DeleteResponse delete(final T model, final RequestOptions requestOptions) throws ElsaException {
        final DeleteRequest request = this.buildDeleteRequest(model);
        try {
            return this.getElsa().client.delete(request, requestOptions);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }


    /** This deletes the document asynchronously. DeleteResponse will be send to the Listener. */
    public void deleteAsync(final T model, final RequestOptions requestOptions, final ActionListener<DeleteResponse> listener) {
        this.getElsa().client.deleteAsync(this.buildDeleteRequest(model), requestOptions, listener);
    }
    /** This deletes the document asynchronously. DeleteResponse will be send to the Listener. Uses RequestOptions.DEFAULT */
    public void deleteAsync(final T model, final ActionListener<DeleteResponse> listener) {
        this.deleteAsync(model, RequestOptions.DEFAULT, listener);
    }

    public DeleteRequest buildDeleteRequest(final T model) {
        return new DeleteRequest(this.indexConfig.getIndexName(), ElsaStatics.DUMMY_TYPE, this.getIdOrThrow(model.getId()));
    }


    // ------------------------------------------------------------------------------------------ //
    // UPDATE
    // ------------------------------------------------------------------------------------------ //

    /** Updating a non-existing documents causes an exception. */
    public ElsaResponse<UpdateResponse> update(final T model) {
        return this.update(model, RequestOptions.DEFAULT);
    }

    public ElsaResponse<UpdateResponse> update(final T model, final RequestOptions requestOptions) {
        final UpdateRequest request = this.buildUpdateRequest(model);
        try {
            return ElsaResponse.of(this.getElsa().client.update(request, requestOptions));
        } catch (final Exception e) {
            return ElsaResponse.of(e);
        }
    }

    public void updateAsync(final T model, final RequestOptions options, final ActionListener<UpdateResponse> listener) {
        this.getElsa().client.updateAsync(this.buildUpdateRequest(model), options, listener);
    }

    public UpdateRequest buildUpdateRequest(final T model) {
        return new UpdateRequest(this.indexConfig.getIndexName(), ElsaStatics.DUMMY_TYPE, this.getIdOrThrow(model.getId()))
                .doc(this.getJsonMapper().toJson(model), XContentType.JSON);
    }


    // ------------------------------------------------------------------------------------------ //
    // PRIVATE METHODS
    // ------------------------------------------------------------------------------------------ //

    private IndexConfig setIndexConfig(final Class<? extends ElsaModel> model) {
        try {
            return model.newInstance().getIndexConfig();
        } catch (final InstantiationException | IllegalAccessException e) {
            logger.error("", e);
        }
        throw new IllegalStateException("Can't build object for model: " + model.getName());
    }

    private String getIdOrThrow(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Action not possible. ID not set in model object.");
        } else {
            return id;
        }
    }

}
