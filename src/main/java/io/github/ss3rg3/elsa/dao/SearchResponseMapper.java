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

import io.github.ss3rg3.elsa.jsonmapper.JsonMapper;
import io.github.ss3rg3.elsa.model.ElsaModel;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SearchResponseMapper<T extends ElsaModel> {

    private final JsonMapper<T> jsonMapper;

    public SearchResponseMapper(final ElsaDAO<T> elsaDAO) {
        this.jsonMapper = elsaDAO.getJsonMapper();
    }

    public T mapHit(final SearchHit searchHit) {
        final T model = jsonMapper.fromJson(searchHit.getSourceAsString());
        model.setId(searchHit.getId());
        return model;
    }

    @Nullable
    public T mapFirstHit(final SearchResponse searchResponse) {
        if(searchResponse.getHits().getHits().length != 0) {
            final T model = jsonMapper.fromJson(searchResponse.getHits().getHits()[0].getSourceAsString());
            model.setId(searchResponse.getHits().getHits()[0].getId());
            return model;
        } else {
            return null;
        }
    }

    public List<T> mapHitsToList(final SearchResponse searchResponse) {
        final List<T> list = new ArrayList<>();
        for(final SearchHit hit : searchResponse.getHits().getHits()) {
            final T model = jsonMapper.fromJson(hit.getSourceAsString());
            model.setId(hit.getId());
            list.add(model);
        }
        return list;
    }

    public Stream<T> mapHitsToStream(final SearchResponse searchResponse) {
        return Stream.of(searchResponse.getHits().getHits())
                .map(hit -> {
                    final T model = jsonMapper.fromJson(hit.getSourceAsString());
                    model.setId(hit.getId());
                    return model;
                });
    }

    public static long getTotalHits(final SearchResponse searchResponse) {
        return searchResponse.getHits().getTotalHits().value;
    }

    public static long getHitsCount(final SearchResponse searchResponse) {
        return searchResponse.getHits().getHits().length;
    }




}
