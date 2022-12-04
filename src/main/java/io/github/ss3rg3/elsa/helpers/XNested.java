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

package io.github.ss3rg3.elsa.helpers;

import org.elasticsearch.index.query.QueryBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builder for Map<String,Object> for the usage in the XContentBuilder to fix the auto-format problem with it. <br>
 * Instead of using .startObject() & .endObject() you can simply do:<br>
 * <pre>
 *  new XJson()
 *     .field("nestedObject", new XNested()
 *        .field("field1", "value1")
 *        .toMap()
 * </pre>
 */
public class XNested {

    private Map<String, Object> map = new LinkedHashMap<>();

    public XNested field(String name, Object value) {
        if (this.map.putIfAbsent(name, value) != null) {
            throw new IllegalArgumentException("Key '" + name + "' already exists in map.");
        }
        return this;
    }

    public XNested array(String name, Object... values) {
        if (this.map.putIfAbsent(name, values) != null) {
            throw new IllegalArgumentException("Key '" + name + "' already exists in map.");
        }
        return this;
    }

    /**
     * <b>Experimental.</b> We abuse SearchSourceBuilder to wrangle a QueryBuilder into the JSON result.
     */
    public XNested query(QueryBuilder queryBuilder) {
        if (this.map.putIfAbsent("query", XJson.queryBuilderToJsonObject(queryBuilder)) != null) {
            throw new IllegalArgumentException("Key 'query' already exists in map.");
        }
        return this;
    }

    public Map<String, Object> end() {
        return this.map;
    }

}
