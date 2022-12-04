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

package io.github.ss3rg3.elsa.jsonmapper;

import com.google.gson.Gson;
import io.github.ss3rg3.elsa.model.ElsaModel;

import java.util.Map;

public class GsonAdapter<T extends ElsaModel> implements JsonMapper<T> {

    private final Gson gson;
    private final Class<T> clazz;

    public GsonAdapter(final Class<T> clazz, final Gson gson) {
        this.clazz = clazz;
        this.gson = gson;
    }

    @Override
    public String toJson(final ElsaModel model) {
        return this.gson.toJson(model);
    }

    @Override
    public T fromJson(final String json) {
        return this.gson.fromJson(json, this.clazz);
    }

    @Override
    public T fromJson(final Map<String, Object> map) {
        return this.gson.fromJson(this.gson.toJsonTree(map), this.clazz);
    }

}
