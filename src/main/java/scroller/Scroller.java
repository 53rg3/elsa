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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import exceptions.RequestExceptionHandler;
import responses.ElsaResponse;
import statics.Messages.ExceptionMsg;

public class Scroller {

    private final ElsaClient elsa;

    public Scroller(final ElsaClient elsa) {
        this.elsa = elsa;
    }

    public ElsaResponse<SearchResponse> initialize(final SearchRequest searchRequest, final ScrollManager scrollManager, final RequestExceptionHandler handler) {
        try {
            final SearchResponse searchResponse = this.elsa.client.search(searchRequest.scroll(scrollManager.getScroll()));
            scrollManager.updateScrollId(searchResponse);
            return ElsaResponse.of(searchResponse);
        } catch (final Exception e) {
            handler.process(e, ExceptionMsg.REQUEST_FAILED);
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<SearchResponse> initialize(final ScrollManager scrollManager, final SearchRequest searchRequest) {
        return this.initialize(searchRequest, scrollManager, this.elsa.getRequestExceptionHandler());
    }

    public boolean hasHits(final SearchResponse searchResponse) {
        return searchResponse.getHits().getHits() != null && searchResponse.getHits().getHits().length > 0;
    }

    public ElsaResponse<SearchResponse> getNext(final ScrollManager scrollManager, final SearchResponse lastSearchResponse, final RequestExceptionHandler handler) {
        try {
            scrollManager.updateScrollId(lastSearchResponse);
            return ElsaResponse.of(this.elsa.client.searchScroll(new SearchScrollRequest(scrollManager.getScrollId()).scroll(scrollManager.getScroll())));
        } catch (final Exception e) {
            handler.process(e, ExceptionMsg.REQUEST_FAILED);
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<SearchResponse> getNext(final ScrollManager scrollManager, final SearchResponse lastSearchResponse) {
        return this.getNext(scrollManager, lastSearchResponse, this.elsa.getRequestExceptionHandler());
    }

    public ElsaResponse<ClearScrollResponse> clearScroll(final ScrollManager scrollManager, RequestExceptionHandler handler) {
        try {
            final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollManager.getScrollId());
            return ElsaResponse.of(this.elsa.client.clearScroll(clearScrollRequest));
        } catch (final Exception e) {
            handler.process(e, ExceptionMsg.REQUEST_FAILED);
            return ElsaResponse.of(e);
        }
    }

    public ElsaResponse<ClearScrollResponse> clearScroll(final ScrollManager scrollManager) {
        return this.clearScroll(scrollManager, this.elsa.getRequestExceptionHandler());
    }

}
