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

package output.c030_Modules;

import madog.core.Output;
import madog.core.Print;
import madog.core.Ref;
import madog.markdown.List;


public class s01_OverviewOfModules extends Output {

    @Override
    public void addMarkDownAsCode() {

        Print.h1("Modules");

        Print.wrapped("All modules can be directly accessed via the ElsaClient instance, e.g. `elsa.admin`, `elsa.scroller`.");

        Print.h2("Overview");
        List list = new List();
        list.entry("Client", "" +
                "Instance of Elasticsearch native High Level REST Client. This gives you also access to the Low Level REST Client.");
        list.entry("Admin", "" +
                "CRUD operations for indices, e.g. `.deleteIndex()`, `.createIndex()`, `.indexExists()`, `.updateMapping()`.");
        list.entry("BulkProcessor", "" +
                "Exposes Elasticsearch's native BulkProcessor. See" +
                Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-bulk-processor.html", " here")+". " +
                "For an usage example see "+
        Ref.internalPath("/src/test/java/bulkprocessor/BulkProcessorTest.java", "BulkProcessorTest.java"));
        list.entry("Scroller", "" +
                "Abstraction for Elasticsearch's scrolling functionality, i.e. retrieval of `SearchRequest` with big result sets. " +
                "For example you can scroll over the whole index with the `QueryBuilders.matchAllQuery()`. See"+
                Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html", " here")+".");
        list.entry("Snapshotter", "" +
                "Abstraction of Elasticsearch's 'snapshot and restore' functionality, i.e. backups. See " +
                Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-snapshots.html", " here")+".");
        list.entry("Reindexer", "" +
                "Abstraction of Elasticsearch's Reindex API, i.e. rebuild an index again. Can be used to change index mappings,  " +
                "remove, rename and edit fields, create an index with a subset of another index and updating document which " +
                "match a particular query.");
        list.entry("GSON", "" +
                "Exposes the internal JSON mapper library with the settings made in the ElsaClient instantiation.");

        Print.wrapped(list.getAsMarkdown());


    }

}
