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

package scroller;

import client.ElsaClient;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import responses.ElsaResponse;

public class Scroller {

    private final ElsaClient elsa;

    public Scroller(final ElsaClient elsa) {
        this.elsa = elsa;
    }

    public ElsaResponse<SearchResponse> initialize(final SearchRequest searchRequest, final ScrollManager scrollManager, final RequestOptions options) {
        try {
            final SearchResponse searchResponse = this.elsa.client.search(searchRequest.scroll(scrollManager.getScroll()), options);
            scrollManager.updateScrollId(searchResponse);
            return ElsaResponse.of(searchResponse);
        } catch (final Exception e) {
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<SearchResponse> initialize(final ScrollManager scrollManager, final SearchRequest searchRequest) {
        return this.initialize(searchRequest, scrollManager, RequestOptions.DEFAULT);
    }

    public boolean hasHits(final SearchResponse searchResponse) {
        return searchResponse.getHits().getHits() != null && searchResponse.getHits().getHits().length > 0;
    }

    public ElsaResponse<SearchResponse> getNext(final ScrollManager scrollManager, final SearchResponse lastSearchResponse, final RequestOptions options) {
        try {
            scrollManager.updateScrollId(lastSearchResponse);
            // todo use .scroll(req, options)
            return ElsaResponse.of(this.elsa.client.searchScroll(new SearchScrollRequest(scrollManager.getScrollId()).scroll(scrollManager.getScroll()), options));
        } catch (final Exception e) {
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<SearchResponse> getNext(final ScrollManager scrollManager, final SearchResponse lastSearchResponse) {
        return this.getNext(scrollManager, lastSearchResponse, RequestOptions.DEFAULT);
    }

    public ElsaResponse<ClearScrollResponse> clearScroll(final ScrollManager scrollManager, final RequestOptions options) {
        try {
            final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollManager.getScrollId());
            return ElsaResponse.of(this.elsa.client.clearScroll(clearScrollRequest, options));
        } catch (final Exception e) {
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<ClearScrollResponse> clearScroll(final ScrollManager scrollManager) {
        return this.clearScroll(scrollManager, RequestOptions.DEFAULT);
    }

}
