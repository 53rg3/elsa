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

package output.c070_Helpers;

import madog.core.Output;
import madog.core.Print;
import madog.markdown.List;


public class s00_Helpers extends Output {

    @Override
    public void addMarkDownAsCode() {

        Print.h1("Helpers");
        List list = new List();

        list.entry("IndexName", "" +
                "`IndexName.of(YourModel.class)` retrieves the index name of a model.");
        list.entry("ModelClass", "" +
                "`ModelClass.createEmpty(YourModel.class)` create an empty instance of a model. Which is actually useless, " +
                "because `new YourModel()` would do the same. Hmm.");
        list.entry("RequestBody", "" +
                "Transforms different types into a `NStringEntity` which is needed for a request in the Low Level REST Client. " +
                "Can be used to transform `XContentBuilder`, `XJson` and `Object`.");
        list.entry("ResponseParser", "" +
                "Converts an Elasticsearch `Response` into a string or an `InputStreamReader`, which can be passed to GSON (access via `elsa.gson`).");
        list.entry("Search", "" +
                "Offers 2 static methods (i.e. use static import) which provide `SearchRequest` and `SearchSourceBuilder`. " +
                "Makes `SearchRequests` a bit less verbose. Example:\n\n" +
                "```java\n" +
                "SearchRequest request = Search.req()\n" +
                "        .indices(IndexName.of(FakerModel.class))\n" +
                "        .source(src()\n" +
                "                .size(3)\n" +
                "                .query(rangeQuery(\"age\")\n" +
                "                        .gt(22)\n" +
                "                        .lt(33)));\n" +
                "```");
        list.entry("XJson", "" +
                "Offers salvation from the dreaded `XContentBuilder` to build JSON-strings, `Map<String,Object>` " +
                "and `XContentBuilder` itself.\n\n" +
                "```java\n" +
                "XContentBuilder easyXContentBuilder = new XJson()\n" +
                "        .field(\"field1\", \"value1\")\n" +
                "        .array(\"field2\", 1, 2, 3)\n" +
                "        .field(\"field3\", date)\n" +
                "        .field(\"field4\", new XNested()\n" +
                "                .field(\"field1\", \"value1\")\n" +
                "                .array(\"field2\", 1, 2, 3)\n" +
                "                .field(\"field3\", date)\n" +
                "                .end())\n" +
                "        .toXContentBuilder();\n" +
                "```\n");


        Print.wrapped(list.getAsMarkdown());



    }

}
