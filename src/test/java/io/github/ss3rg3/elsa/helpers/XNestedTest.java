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

import org.elasticsearch.common.Strings;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class XNestedTest {

    @Test
    public void build_WithXContentBuilder_pass() throws Exception {
        final String expectedOutput = "{\"user\":\"kimchy\",\"nestedObject\":{\"field1\":\"value1\",\"field3\":[1,2,3],\"field5\":{\"lat\":0.0,\"lon\":0.0},\"goDeeper\":{\"field6\":\"value6\"}},\"message\":\"trying out Elasticsearch\"}";
        final XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .field("user", "kimchy")
                .field("nestedObject", new XNested()
                        .field("field1", "value1")
                        .array("field3", 1, 2, 3)
                        .field("field5", new GeoPoint())
                        .field("goDeeper", new XNested()
                                .field("field6", "value6")
                                .end())
                        .end())
                .field("message", "trying out Elasticsearch")
                .endObject();
        assertThat(Strings.toString(xContentBuilder), is(expectedOutput));
    }

}
