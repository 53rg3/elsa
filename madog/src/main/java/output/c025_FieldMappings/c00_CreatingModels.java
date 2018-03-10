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

package output.c025_FieldMappings;

import madog.core.Output;
import madog.core.Print;
import madog.core.Ref;
import madog.markdown.Icon;
import madog.markdown.List;


public class c00_CreatingModels extends Output {

    @Override
    public void addMarkDownAsCode() {
        Print.h1("Creating Models");


        Print.h2("Creating an ElsaModel");
        List elsaModel = new List();
        elsaModel.entry("For a simple example of a valid ElsaModel see "+ Ref.classFile("ElsaModelExample.java", "Example Model")+". ");
        elsaModel.entry("A model class must implement `ElsaModel` and return an instance of `ElsaIndexData` with the overridden `getIndexData()` method.");
        elsaModel.entry("Use the `ElsaIndexData.Builder()` to create the `ElsaIndexData` field.");
        elsaModel.entry("Create the `ElsaIndexData` field as `private static final` so all models of this type will share the same instance.");
        elsaModel.entry("IndexName is mutable, changing is thread-safe. All other settings are immutable.");
        elsaModel.entry("If there are other constructors than the default one, then an empty constructor needs to be provided.");
        Print.wrapped(elsaModel.getAsMarkdown());


        Print.h3("Using the ElsaIndexData builder");
        Print.wrapped(Icon.MAG_GLASS + " The settings for indexName, type, amount of shards, amount of replicas are mandatory. \n\n" +
                "Example: ");
        Print.codeBlock("" +
                "private static final ElsaIndexData indexData = new ElsaIndexData.Builder()\n" +
                "        .setIndexName(\"youtube\")\n" +
                "        .setType(\"video\")\n" +
                "        .setShards(1)\n" +
                "        .setReplicas(1)\n" +
                "        .build();" +
                "");


        Print.h2("Field Mappings");
        List fieldMappings = new List();

        String javaDocField = "https://docs.spring.io/spring-data/elasticsearch/docs/current/api/org/springframework/data/elasticsearch/annotations/Field.html";
        String javaDocFieldData = "https://docs.spring.io/spring-data/elasticsearch/docs/current/api/org/springframework/data/elasticsearch/annotations/FieldType.html";
        String springLink = "https://github.com/spring-projects/spring-data-elasticsearch/blob/master/src/test/java/org/springframework/data/elasticsearch/entities/SampleDateMappingEntity.java";
        fieldMappings.entry("" +
                "We can use any mapping annotations from Spring Data Elasticsearch as defined in "+Ref.externalURL(javaDocField, "Field") + ", " +
                "with all defined field types as defined in "+Ref.externalURL(javaDocFieldData, "FieldData") + ".");
        fieldMappings.entry("We used Spring Data Elasticsearch version **3.0.3** for this.");
        fieldMappings.entry("For an example check "+Ref.externalURL(springLink, "SampleDateMappingEntity")+".");
        Print.wrapped(fieldMappings.getAsMarkdown());
    }

}
