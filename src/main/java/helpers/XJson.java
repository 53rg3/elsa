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

package helpers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
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
public class XJson {

    private static Gson gson = new Gson();
    private static JsonParser jsonParser = new JsonParser();
    private Map<String,Object> map = new LinkedHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(XJson.class);

    public XJson field(String name, Object value) {
        ensureXNestedWasClosed(value);
        if(this.map.putIfAbsent(name, value) != null) {
            throw new IllegalArgumentException("Key '"+name+"' already exists in JSON.");
        }
        return this;
    }

    public XJson overrideField(String name, Object value) {
        ensureXNestedWasClosed(value);
        this.map.put(name, value);
        return this;
    }

    public XJson array(String name, Object... values) {
        ensureXNestedWasClosed(values);
        if(this.map.putIfAbsent(name, values) != null) {
            throw new IllegalArgumentException("Key '"+name+"' already exists in JSON.");
        }
        return this;
    }

    public XJson overrideArray(String name, Object... values) {
        ensureXNestedWasClosed(values);
        this.map.put(name, values);
        return this;
    }

    /**
     * <b>Experimental.</b> We abuse SearchSourceBuilder to wrangle a QueryBuilder into the JSON result.
     */
    public XJson query(QueryBuilder queryBuilder) {
        if(this.map.putIfAbsent("query", queryBuilderToJsonObject(queryBuilder)) != null) {
            throw new IllegalArgumentException("Key 'query' already exists in map.");
        }
        return this;
    }

    public String toJson() {
        return gson.toJson(this.map);
    }

    public XContentBuilder toXContentBuilder() {
        try {
            return XContentFactory.jsonBuilder().value(this.map);
        } catch (IOException e) {
            logger.error("", e);
        }
        throw new IllegalStateException("Couldn't build XContentBuilder with JSON string: "+this.toJson());
    }

    public Map<String,Object> toMap() {
        return this.map;
    }



    /** Check if a field exists in collection, throw if null */
    public void throwIfFieldNotExists(String name, String errorMessage) {
        if(!this.map.containsKey(name)) {
            throw new IllegalStateException(errorMessage);
        }
    }

    /** Helper function to extract the JSON body from the QueryBuilder without the key.*/
    public static Map<String,Object> queryBuilderToJsonObject(QueryBuilder queryBuilder) {
        JsonObject jsonObject = jsonParser.parse(SearchSourceBuilder.searchSource().query(queryBuilder).toString()).getAsJsonObject().getAsJsonObject("query");
        return gson.fromJson(jsonObject, XJsonType.mapStringObjectType);
    }

    private static void ensureXNestedWasClosed(Object object) {
        if(object instanceof XNested) {
            throw new IllegalArgumentException("XNested wasn't closed with .end()");
        }
    }

}
