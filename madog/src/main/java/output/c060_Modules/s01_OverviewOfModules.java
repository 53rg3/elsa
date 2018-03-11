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

package output.c060_Modules;

import com.sun.deploy.ref.Helpers;
import madog.core.Output;
import madog.core.Print;
import madog.core.Ref;
import madog.markdown.List;
import output.c070_Helpers.s00_Helpers;


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
                "Exposes Elasticsearch's native BulkProcessor (see" +
                Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-bulk-processor.html", " here")+".) " +
                "For an usage example see "+
        Ref.internalPath("/src/test/java/bulkprocessor/BulkProcessorTest.java", "BulkProcessorTest.java"));
        list.entry("Scroller", "" +
                "Abstraction for Elasticsearch's scrolling functionality (see "+
                Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html", " here")+".)"+"), " +
                "i.e. retrieval of `SearchRequest` with big result sets. " +
                "For example you can scroll over the whole index with the `QueryBuilders.matchAllQuery()`. For a usage example see "+
                Ref.internalPath("/src/test/java/scroller/ScrollerTest.java", "ScrollerTest.java"));
        list.entry("Snapshotter", "" +
                "Abstraction of Elasticsearch's 'snapshot and restore' functionality (see " +
                Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-snapshots.html", " here")+".), i.e. backups. " +
                "For a usage example see " +
                Ref.internalPath("/src/test/java/snapshotter/SnapshotterTest.java", "SnapshotterTest.java"));
        list.entry("Reindexer", "" +
                "Abstraction of Elasticsearch's Reindex API, i.e. rebuild an index again. Can be used to change index mappings,  " +
                "remove, rename and edit fields, create an index with a subset of another index and updating document which " +
                "match a particular query. For a usage example see "+
                Ref.internalPath("/src/test/java/reindexer/ReindexerTest.java", "ReindexerTest.java"));
        list.entry("GSON", "" +
                "Exposes the internal JSON mapper library with the settings made in the ElsaClient instantiation. Can be used to parse raw Elasticsearch " +
                "responses from the Low Level REST Client. See also section "+Ref.outputClass(s00_Helpers.class));

        Print.wrapped(list.getAsMarkdown());

    }

}
