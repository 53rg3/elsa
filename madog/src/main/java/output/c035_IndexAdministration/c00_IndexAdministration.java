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

package output.c035_IndexAdministration;

import madog.core.Output;
import madog.core.Print;
import madog.core.Ref;
import madog.markdown.Icon;
import madog.markdown.List;


public class c00_IndexAdministration extends Output {

    @Override
    public void addMarkDownAsCode() {
        Print.h1("Index Administration");

        Print.wrapped("The index can be administrated via `ElsaClient.admin`.");

        Print.wrapped("First, create a client:");
        Print.codeBlock("" +
                "private final HttpHost[] httpHosts = {new HttpHost(\"localhost\", 9200, \"http\")};\n" +
                "private final ElsaClient elsa = new ElsaClient.Builder(httpHosts)\n" +
                "        .registerModel(YourModel.class, YourDAO.class)\n" +
                "        .build();");

        Print.h2("Create a new index.");
        Print.wrapped("The mappings are directly create with the provided model.");
        Print.codeBlock("" +
                "CreateIndexResponse response = elsa.admin.createIndex(YourModel.class);");

        Print.h2("Check if an index exists");
        Print.codeBlock("" +
                "boolean result = elsa.admin.indexExists(YourModel.class);\n" +
                "boolean result = elsa.admin.indexExists(\"INDEX_NAME_STRING\");");

        Print.h2("Delete an index");
        Print.codeBlock("" +
                "DeleteIndexResponse response = elsa.admin.deleteIndex(YourModel.class);\n" +
                "DeleteIndexResponse response = elsa.admin.deleteIndex(\"INDEX_NAME_STRING\");");

        String updateLink = "https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-put-mapping.html";
        Print.h2("Update an index mapping. ");
        Print.wrapped(Icon.BANG + "Caution: You can only add fields. Trying to change a field mapping will result in answered with an exception from Elasticsearch. " +
                "See "+Ref.externalURL(updateLink, "official docs")+". Changing existing fields requires reindexing. ");
        Print.codeBlock("" +
                "Response response = elsa.admin.updateMapping(YourExtendedModel.class);");
    }

}
