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

package output.c080_EncounteredProblems;

import madog.core.Output;
import madog.core.Print;
import madog.core.Ref;


public class c00_EncounteredProblems extends Output {

    @Override
    public void addMarkDownAsCode() {

        Print.h1("Encountered Problems");

        Print.h2("NoClassDefFoundError for Log4J");
        Print.wrapped("`java.lang.NoClassDefFoundError: org/apache/logging/log4j/util/MultiFormatStringBuilderFormattable`<br/>" +
                "`log4j-api` must be added to `pom.xml`.");


        Print.h2("Saving Arrays:");
        Print.codeBlock("" +
                "@Field(type = FieldType.Object)\n" +
                "private List<String> arrayField;");
        Print.wrapped("Leads to: `ElasticsearchStatusException[Elasticsearch exception [type=mapper_parsing_exception, " +
                "reason=object mapping for [arrayField] tried to parse field [null] as object, but found a concrete value]`");
        Print.wrapped("Use @Field(type = FieldType.text) instead. \"In Elasticsearch, there is no dedicated|array|type. " +
                "Any field can contain zero or more values by default, however, all values in the array must be of the " +
                "same datatype.\" i.e. a list / array is no object.");


        Print.h2("Using bulk processor leads to TimeoutException");
        Print.codeBlock("private final ElsaClient elsa = new ElsaClient.Builder(httpHosts)\n" +
                "        .registerModel(BulkModel.class, ElsaDAO.class)\n" +
                "        .configureApacheRequestConfigBuilder(config -> config.setConnectionRequestTimeout(0))\n" +
                "        .build();");
        Print.wrapped("More info: "+ Ref.externalURL("https://github.com/elastic/elasticsearch/issues/24069"));


        Print.h2("Gson.toJson causes StackOverflowError");
        Print.wrapped("IndexConfig field must be static. Otherwise GSON tries to serialize it, which seemingly has " +
                "some circular reference in it.");
    }

}
