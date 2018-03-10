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

import statics.ElsaStatics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class KeysToValuesConverter {
    private KeysToValuesConverter() {}

    /**
     * Converts keys to values for objects like the following. Nested objects are not converted.
     * <pre>
     * {
     *    "key1": {"field": "value"}
     * }
     * </pre>
     * Gets converted to:
     * <pre>
     * {
     *     "someName": "key1",
     *     "field": "value"
     * }
     * </pre>
     */
    public static String convertSingleObject(final Map<String, Object> map, final String fieldNameForKey) {
        final XJson xJson = new XJson();
        map.forEach((key, value) -> {
            xJson.field(fieldNameForKey, key);
            getAsMap(ElsaStatics.GSON.toJson(value)).forEach(xJson::field);
        });
        return ElsaStatics.GSON.toJson(xJson.toMap());
    }

    /**
     * Converts keys to values for objects like the following. Nested objects are not converted.
     * <pre>
     * [{
     *    "key1": {"field": "value"},
     *    "key2": {"field": "value"}
     * }]
     * </pre>
     * Gets converted to:
     * <pre>
     * [{
     *     "someName": "key1",
     *     "field": "value"
     * },
     * {
     *     "someName": "key2",
     *     "field": "value"
     * }]
     * </pre>
     */
    public static String convertArrayOfObjects(final Map<String, Object>[] outerList, final String fieldNameForKey) {
        final List<Map<String, Object>> list = new ArrayList<>();
        Stream.of(outerList).forEach(innerMap ->
            reassembleJsonObject(innerMap, list, fieldNameForKey));
        return ElsaStatics.GSON.toJson(list);
    }


    /**
     * Converts keys to values for objects like the following. Nested objects are not converted.
     * <pre>
     * {
     *    "key1": {"field": "value"},
     *    "key2": {"field": "value"}
     * }
     * </pre>
     * Gets converted to:
     * <pre>
     * [{
     *     "someName": "key1",
     *     "field": "value"
     * },
     * {
     *     "someName": "key2",
     *     "field": "value"
     * }]
     * </pre>
     */
    public static String convertObjectOfObjects(final Map<String, Object> map, final String fieldNameForKey) {
        final List<Object> list = new ArrayList<>();
        reassembleJsonObject(map, list, fieldNameForKey);
        return ElsaStatics.GSON.toJson(list);
    }

    private static <T> void reassembleJsonObject(final Map<String, Object> map, final List<T> list, final String fieldNameForKey) {
        map.forEach((key, value) -> {
            final XJson xJson = new XJson();
            xJson.field(fieldNameForKey, key);
            getAsMap(ElsaStatics.GSON.toJson(value)).forEach(xJson::field);
            list.add((T) xJson.toMap());
        });
    }

    private static Map<String, Object> getAsMap(final String json) {
        return ElsaStatics.GSON.fromJson(json, XJsonType.mapStringObjectType);
    }

}
