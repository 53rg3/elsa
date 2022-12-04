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

package io.github.ss3rg3.elsa.dao;

import io.github.ss3rg3.elsa.ElsaClient;
import io.github.ss3rg3.elsa.exceptions.ElsaElasticsearchException;
import io.github.ss3rg3.elsa.exceptions.ElsaException;
import io.github.ss3rg3.elsa.exceptions.ElsaIOException;
import io.github.ss3rg3.elsa.model.ElsaModel;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class SearchDAO<T extends ElsaModel> extends ElsaDAO<T> {

    public SearchDAO(final DaoConfig daoConfig, final ElsaClient elsa) {
        super(daoConfig, elsa);
    }

    public SearchResponse search(final SearchRequest searchRequest, final RequestOptions options) throws ElsaException {
        try {
            return this.getElsa().client.search(searchRequest, options);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }

    public SearchResponse search(final SearchRequest searchRequest) throws ElsaException {
        return this.search(searchRequest, RequestOptions.DEFAULT);
    }

    /**
     * Executes a SearchRequest asynchronously. Response is send to Listener.
     */
    public void searchAsync(final SearchRequest searchRequest, final RequestOptions options, final ActionListener<SearchResponse> listener) {
        this.getElsa().client.searchAsync(searchRequest, options, listener);
    }

    /**
     * Makes a search and returns the first hit of the response mapped to your io.github.ss3rg3.elsa.model inclusive the _id field.<br>
     * If you need the meta data of the SearchResponse, then use the regular search method and parse the hits manually
     * with the SearchResponseMapper in this DAO.
     */
    public T searchAndMapFirstHit(final SearchRequest searchRequest, final RequestOptions options) throws ElsaException {
        final SearchResponse response = this.search(searchRequest, options);
        return this.getSearchResponseMapper().mapFirstHit(response);
    }

    public T searchAndMapFirstHit(final SearchRequest searchRequest) throws ElsaException {
        return this.searchAndMapFirstHit(searchRequest, RequestOptions.DEFAULT);
    }

    /**
     * Makes a search and returns the hits of the response mapped to your io.github.ss3rg3.elsa.model inclusive the _id field.<br>
     * If you need the meta data of the SearchResponse, then use the regular search method and parse the hits manually
     * with the SearchResponseMapper in this DAO.
     *
     * @return Empty list if no results found
     */
    public List<T> searchAndMapToList(final SearchRequest searchRequest, final RequestOptions options) throws ElsaException {
        final SearchResponse response = this.search(searchRequest, options);
        return this.getSearchResponseMapper().mapHitsToList(response);
    }

    public List<T> searchAndMapToList(final SearchRequest searchRequest) throws ElsaException {
        return this.searchAndMapToList(searchRequest, RequestOptions.DEFAULT);
    }

    /**
     * Makes a search and returns the hits of the response mapped to your io.github.ss3rg3.elsa.model inclusive the _id field as a stream.<br>
     * If you need the meta data of the SearchResponse, then use the regular search method and parse the hits manually
     * with the SearchResponseMapper in this DAO.
     *
     * @return Empty list if no results found
     */
    public Stream<T> searchAndMapToStream(final SearchRequest searchRequest, final RequestOptions options) throws ElsaException {

        final SearchResponse response = this.search(searchRequest, options);

        return Stream.of(response.getHits().getHits())
                .map(hit -> {
                    final T model = this.getJsonMapper().fromJson(hit.getSourceAsString());
                    model.setId(hit.getId());
                    return model;
                });
    }

    public Stream<T> searchAndMapToStream(final SearchRequest searchRequest) throws ElsaException {
        return this.searchAndMapToStream(searchRequest, RequestOptions.DEFAULT);
    }

}
