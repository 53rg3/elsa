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
import org.elasticsearch.common.Strings;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class XJsonTest {

    @Test
    public void create_xContentBuilderWithNestedObject_pass() {
        final Date date = new Date();

        final XContentBuilder easyXContentBuilder = new XJson()
                .field("field1", "value1")
                .array("field2", 1, 2, 3)
                .field("field3", date)
                .field("field4", new XNested()
                        .field("field1", "value1")
                        .array("field2", 1, 2, 3)
                        .field("field3", date)
                        .end())
                .toXContentBuilder();

        try {
            final XContentBuilder retardedXContentBuilder = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("field1", "value1")
                    .array("field2", 1, 2, 3)
                    .field("field3", date)
                    .field("field4")
                    .startObject()
                    .field("field1", "value1")
                    .array("field2", 1, 2, 3)
                    .field("field3", date)
                    .endObject()
                    .endObject();

            assertThat(Strings.toString(retardedXContentBuilder), is(Strings.toString(easyXContentBuilder)));

        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void queryBuilderTransformation_toJsonEqualsXContentBuilderString_pass() {

        final String expected = "{\"source\":{\"index\":\"twitter\",\"query\":{\"term\":{\"user\":{\"value\":\"kimchy\",\"boost\":1.0}}}},\"dest\":{\"index\":\"new_twitter\"}}";

        final XJson xJson = new XJson()
                .field("source", new XNested()
                        .field("index", "twitter")
                        .query(QueryBuilders.termQuery("user", "kimchy"))
                        .end())
                .field("dest", new XNested()
                        .field("index", "new_twitter")
                        .end());
        final XContentBuilder xContentBuilder = xJson.toXContentBuilder();

        assertThat(xJson.toJson(), is(expected));
        assertThat(Strings.toString(xContentBuilder), is(expected));
    }

    @Test
    public void queryBuilderToJsonObject_withSearchSourceBuilder_pass() {
        final Gson gson = new Gson();
        final QueryBuilder queryBuilder = QueryBuilders.termQuery("user", "kimchy");

        final String expected = "{\"query\":{\"term\":{\"user\":{\"value\":\"kimchy\",\"boost\":1.0}}}}";
        final String resultFromQueryBuilderToJsonObject = "{\"query\":" + gson.toJson(XJson.queryBuilderToJsonObject(queryBuilder)) + "}";
        final String resultFromSearchSourceBuilder = SearchSourceBuilder.searchSource().query(queryBuilder).toString();

        assertThat(resultFromQueryBuilderToJsonObject, is(expected));
        assertThat(resultFromSearchSourceBuilder, is(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_nestedObjectNotClosed_throw() {
        new XJson()
                .field("source", new XNested()
                        .field("index", "twitter")
                        .field("type", "_doc")
                        .query(QueryBuilders.termQuery("user", "kimchy")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_duplicateKey_throw() {
        new XJson().field("qwer", "asdf").field("qwer", "yxcv");
    }
}
