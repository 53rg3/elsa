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

import org.junit.Test;
import statics.ElsaStatics;

import java.util.Map;

import static helpers.KeysToValuesConverter.convertArrayOfObjects;
import static helpers.KeysToValuesConverter.convertObjectOfObjects;
import static helpers.KeysToValuesConverter.convertSingleObject;
import static helpers.XJsonType.arrayType;
import static helpers.XJsonType.mapStringObjectType;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static statics.ElsaStatics.GSON;

public class KeysToValuesConverterTest {

    @Test
    public void convertSingleObject_pass() {
        String jsonSingleObject = "{\"my_repo\":{\"type\":\"fs\",\"settings\":{\"compress\":\"true\",\"location\":\"/mnt/esbackup\"}}}";
        String expected = "{\"name\":\"my_repo\",\"type\":\"fs\",\"settings\":{\"compress\":\"true\",\"location\":\"/mnt/esbackup\"}}";
        assertThat(convertSingleObject(GSON.fromJson(jsonSingleObject, mapStringObjectType), "name"), is(expected));
    }

    @Test
    public void convertObjectOfObjects_pass() {
        String jsonObjectOfObjects = "{\"my_repo\":{\"type\":\"fs\",\"settings\":{\"compress\":\"true\",\"location\":\"/mnt/esbackup\"}},\"asdf3\":{\"type\":\"fs\",\"settings\":{\"compress\":\"true\",\"location\":\"/mnt/esbackup\"}}}";
        String expected = "[{\"name\":\"my_repo\",\"type\":\"fs\",\"settings\":{\"compress\":\"true\",\"location\":\"/mnt/esbackup\"}},{\"name\":\"asdf3\",\"type\":\"fs\",\"settings\":{\"compress\":\"true\",\"location\":\"/mnt/esbackup\"}}]";
        assertThat(convertObjectOfObjects(GSON.fromJson(jsonObjectOfObjects, mapStringObjectType), "name"), is(expected));
    }

    @Test
    public void convertArrayOfObjects_pass() {
        String jsonArrayOfObjects = "[{\"my_repo\":{\"type\":\"fs\",\"settings\":{\"compress\":\"true\",\"location\":\"/mnt/esbackup\"}},\"asdf3\":{\"type\":\"fs\",\"settings\":{\"compress\":\"true\",\"location\":\"/mnt/esbackup\"}}}]";
        String expected = "[{\"name\":\"my_repo\",\"type\":\"fs\",\"settings\":{\"compress\":\"true\",\"location\":\"/mnt/esbackup\"}},{\"name\":\"asdf3\",\"type\":\"fs\",\"settings\":{\"compress\":\"true\",\"location\":\"/mnt/esbackup\"}}]";
        assertThat(convertArrayOfObjects(GSON.fromJson(jsonArrayOfObjects, arrayType), "name"), is(expected));
    }

}
