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
import jsonmapper.JsonMapperLibrary;
import model.ElsaModel;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import responses.ElsaResponse;

import java.util.List;
import java.util.stream.Stream;

public class SearchDAO<T extends ElsaModel> extends ElsaDAO<T> {

    public SearchDAO(final Class<T> model, final ElsaClient elsa, final JsonMapperLibrary jsonMapperLibrary) {
        super(model, elsa, jsonMapperLibrary);
    }

    public ElsaResponse<SearchResponse> search(final SearchRequest searchRequest, final RequestOptions options) {
        try {
            return ElsaResponse.of(this.getElsa().client.search(searchRequest, options));
        } catch (final Exception e) {
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<SearchResponse> search(final SearchRequest searchRequest) {
        return this.search(searchRequest, RequestOptions.DEFAULT);
    }

    /**
     * Executes a SearchRequest asynchronously. Response is send to Listener.
     */
    public void searchAsync(final SearchRequest searchRequest, final RequestOptions options, final ActionListener<SearchResponse> listener) {
        this.getElsa().client.searchAsync(searchRequest, options, listener);
    }

    /**
     * Makes a search and returns the first hit of the response mapped to your model inclusive the _id field.<br>
     * If you need the meta data of the SearchResponse, then use the regular search method and parse the hits manually
     * with the SearchResponseMapper in this DAO.
     */
    public ElsaResponse<T> searchAndMapFirstHit(final SearchRequest searchRequest, final RequestOptions options) {
        final ElsaResponse<SearchResponse> response = this.search(searchRequest, options);
        if (response.isPresent()) {
            return ElsaResponse.ofNullable(this.getSearchResponseMapper().mapFirstHit(response.get()));
        }
        return ElsaResponse.of(response.getExceptionResponse());
    }

    public ElsaResponse<T> searchAndMapFirstHit(final SearchRequest searchRequest) {
        return this.searchAndMapFirstHit(searchRequest, RequestOptions.DEFAULT);
    }

    /**
     * Makes a search and returns the hits of the response mapped to your model inclusive the _id field.<br>
     * If you need the meta data of the SearchResponse, then use the regular search method and parse the hits manually
     * with the SearchResponseMapper in this DAO.
     * @return Empty list if no results found
     */
    public ElsaResponse<List<T>> searchAndMapToList(final SearchRequest searchRequest, final RequestOptions options) {
        final ElsaResponse<SearchResponse> response = this.search(searchRequest, options);
        if (response.isPresent()) {
            return ElsaResponse.ofNullable(this.getSearchResponseMapper().mapHitsToList(response.get()));
        }
        return ElsaResponse.of(response.getExceptionResponse());
    }

    public ElsaResponse<List<T>> searchAndMapToList(final SearchRequest searchRequest) {
        return this.searchAndMapToList(searchRequest, RequestOptions.DEFAULT);
    }

    /**
     * Makes a search and returns the hits of the response mapped to your model inclusive the _id field as a stream.<br>
     * If you need the meta data of the SearchResponse, then use the regular search method and parse the hits manually
     * with the SearchResponseMapper in this DAO.
     * @return Empty list if no results found
     */
    public ElsaResponse<Stream<T>> searchAndMapToStream(final SearchRequest searchRequest, final RequestOptions options) {

        final ElsaResponse<SearchResponse> response = this.search(searchRequest, options);
        if (response.hasException()) {
            return ElsaResponse.of(response.getExceptionResponse());
        }

        return ElsaResponse.of(Stream.of(response.get().getHits().getHits())
                .map(hit -> {
                    final T model = this.getJsonMapper().fromJson(hit.getSourceAsString());
                    model.setId(hit.getId());
                    return model;
                }));
    }

    public ElsaResponse<Stream<T>> searchAndMapToStream(final SearchRequest searchRequest) {
        return this.searchAndMapToStream(searchRequest, RequestOptions.DEFAULT);
    }

}
