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

package output;

import madog.markdown.Icon;
import madog.core.Output;
import madog.core.Print;
import madog.core.Ref;
import madog.markdown.List;


public class c00_Index extends Output {

    @Override
    public void addMarkDownAsCode() {
        Print.accessPrinter().displayCompleteTableOfContentOfAllPagesOnThisPage(true);
        Print.h1("ELSA - Elasticsearch Simplified API");

        Print.h2("Motivation");
        Print.wrapped("" +
                "Elasticsearch's Native API is too complicated and Spring Data Elasticsearch is slow, bloated and always one major version behind. " +
                "It also uses Spring. Ewww.");

        Print.h2("Goals - 5.2.18");
        List goals = new List();
        goals.entry("Easier index mapping", "" +
                "For index mappings in Elasticsearch you either need to use the clunky `XContentBuilder` or keep the mappings as JSON in separate text files - " +
                "both are shitty solutions. We need some easy-peasy JPA-styleisy annotated fields for sheezy. So we would have everything inside the model class and " +
                "would be able to modify it easily.");

        goals.entry("Standard CRUD", "" +
                "Each simple CRUD operation for every model needs its own implementation. We can easily provide those to a model via some abstract class.");

        goals.entry("Automatic ORM", "" +
                "We could easily provide functionality to map Elasticsearch responses onto the corresponding model objects.");

        goals.entry("Dynamic indices", "" +
                "Spring Data made it difficult to change indices dynamically. We could simply provide some getter inside the model to provide whatever index we want.");

        goals.entry("Easier configuration", "" +
                "The native Elasticsearch REST client uses Apache's HttpAsyncClients `RequestConfig.Builder` and `HttpAsyncClientBuilder` for its configuration. " +
                "This can be simplified. We can also provide different contexts for clients - e.g. a client for `PROD` and one for `TEST` context.");

        goals.entry("Index Management", "" +
                "If we want to change the index mapping, we need to reindex the whole thing. It looks that this can be simplified too. Same for backups.");

        Print.wrapped(goals.getAsMarkdown());


    }

}
