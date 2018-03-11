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

package output.c030_Models;

import madog.core.Output;
import madog.core.Print;
import madog.core.Ref;
import madog.markdown.List;


public class s00_Models extends Output {

    @Override
    public void addMarkDownAsCode() {

        Print.h1("Models");

        Print.wrapped("For a usage example see "+Ref.internalPath("/src/test/java/assets/TestModelWithAddedMappings.java", "TestModel.java"));

        Print.wrapped("**Requirements**");
        List list = new List();
        list.entry("Models must implement the `ElsaModel` interface.");
        list.entry("All methods must be implemented properly. Otherwise ELSA will throw exceptions on startup.");
        list.entry("Models must set up the `IndexConfig` instance properly. Otherwise ELSA will throw exceptions on startup.");
        list.entry("The ID field should be marked as `transient`. Otherwise Elasticsearch will save them in `_source`.");
        list.entry("Dynamic index naming must be allowed explicitly. `IndexConfig` is static, so changing the index name " +
                "in one instance of the model will also change it in all other instances of the same model. The `IndexConfig` " +
                "is accessible via `.getIndexConfig()`.");
        list.entry("All index mappings can be created via Spring Data Elasticsearch annotations. If a field is not marked as " +
                "`transient` and dynamic mapping in Elasticsearch is not disabled, then it will be indexed by Elasticsearch.");
        list.entry("Mappings can't be changed once created. You can only add fields. Changing a mapping, e.g. configuring a " +
                "different analyzer, requires reindexing of the whole index.");

        Print.wrapped(list.getAsMarkdown());

    }

}
