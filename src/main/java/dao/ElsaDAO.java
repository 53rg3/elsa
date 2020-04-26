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
import jsonmapper.GsonAdapter;
import jsonmapper.JsonMapper;
import model.ElsaModel;
import model.IndexConfig;

import java.util.Objects;

public class ElsaDAO<T extends ElsaModel> {

    private final Class<T> modelClass;
    private final IndexConfig indexConfig;
    private final ElsaClient elsa;
    private final JsonMapper<T> jsonMapper;
    private final SearchResponseMapper<T> searchResponseMapper;

    @SuppressWarnings("unchecked")
    public ElsaDAO(final DaoConfig daoConfig, final ElsaClient elsa) {
        Objects.requireNonNull(daoConfig, "daoConfig must not be null");
        Objects.requireNonNull(elsa, "elsa must not be null");

        this.modelClass = (Class<T>) daoConfig.getModelClass();
        this.indexConfig = daoConfig.getIndexConfig();
        this.elsa = elsa;

        this.jsonMapper = new GsonAdapter<>(this.modelClass, daoConfig.getGson() == null ? elsa.gson : daoConfig.getGson());
        this.searchResponseMapper = new SearchResponseMapper<>(this);
    }

    public ElsaClient getElsa() {
        return this.elsa;
    }

    public JsonMapper<T> getJsonMapper() {
        return this.jsonMapper;
    }

    public Class<T> getModelClass() {
        return this.modelClass;
    }

    public SearchResponseMapper<T> getSearchResponseMapper() {
        return this.searchResponseMapper;
    }

    public IndexConfig getIndexConfig() {
        return this.indexConfig;
    }
}
