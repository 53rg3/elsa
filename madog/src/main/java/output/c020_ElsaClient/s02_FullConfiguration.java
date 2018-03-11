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

package output.c020_ElsaClient;

import madog.core.Output;
import madog.core.Print;
import madog.core.Ref;
import madog.markdown.List;


public class s02_FullConfiguration extends Output {

    @Override
    public void addMarkDownAsCode() {

        Print.h2("Minimal configuration");

        Print.wrapped("**BAD CONFIGURATION. DO NOT COPY & PASTE**");

        Print.codeBlock("" +
                "private final HttpHost[] httpHosts = {new HttpHost(\"localhost\", 9200, \"http\")};\n" +
                "private final ElsaClient elsa = new ElsaClient(c0 -> c0\n" +
                "        .setClusterNodes(httpHosts) // (1)\n" +
                "        .registerModel(FakerModel.class, CrudDAO.class) // (2)\n" +
                "        .registerModel(TestModel.class, SearchDAO.class)\n" +
                "        .createIndexesAndEnsureMappingConsistency(true) // (3)\n" +
                "        .setDefaultRequestExceptionHandler(new JustLogExceptionHandler()) // (4)\n" +
                "        .stifleThreadUntilClusterIsOnline(false) // (5)\n" +
                "        .setIndexNamePrefix(\"test_\") // (6)\n" +
                "        .configureLowLevelClient(c1 -> c1 // (7)\n" +
                "                .setRequestConfigCallback(c2 -> c2 // (8)\n" +
                "                        .setAuthenticationEnabled(false)\n" +
                "                        .setConnectTimeout(60000))\n" +
                "                .setHttpClientConfigCallback(c3 -> c3 // (9)\n" +
                "                        .setUserAgent(\"your_company\")\n" +
                "                        .setMaxConnTotal(100))\n" +
                "                .setFailureListener(new FailureListener()) // (10)\n" +
                "                .setDefaultHeaders(new Header[]{}) // (11)\n" +
                "                .setMaxRetryTimeoutMillis(10000)) // (12)\n" +
                "        .configureBulkProcessor(c4 -> c4 // (13)\n" +
                "                .setBulkActions(66)\n" +
                "                .setFlushInterval(TimeValue.timeValueSeconds(10)))\n" +
                "        .setBulkResponseListener(new DefaultBulkResponseListener()) // (14)\n" +
                "        .registerSnapshotRepositories(c5 -> c5 // (15)\n" +
                "                .add(new SnapshotRepository(\"daily_backups\", \"/mnt/daily_backups\"))\n" +
                "                .add(new SnapshotRepository(\"monthly_backups\", \"/mnt/monthly_backups\")))\n" +
                "        .configureGson(c6 -> c6 // (16)\n" +
                "                .setDateFormat(\"yyyy-MM-dd\")\n" +
                "                .registerTypeAdapter(Date.class, new GsonUTCDateAdapter()))\n" +
                ");" +
                "");

        List list = new List();
        list.entry("(1) .setClusterNodes()", "" +
                "Array of `HttpHost` with your Elasticsearch nodes. Can't be refreshed. Needs a new client instantiation.");
        list.entry("(2) .registerModel()", "" +
                "The model and the corresponding DAO you want to use with it. Retrieve it via ``elsa.getDAO(FakerModel.class)");
        list.entry("(3) .createIndexesAndEnsureMappingConsistency()", "" +
                "If the indices do not exist, then they will be created. If they exist, their mapping will be updated. " +
                "If the new mapping is invalid, then this fail on startup.");
        list.entry("(4) .setDefaultRequestExceptionHandler()", "" +
                "In some cases the Elasticsearch cluster responds with Exceptions. You can a pass a default exception handler " +
                "which will be invoked if no custom handler was passed as argument to the request methods.");
        list.entry("(5) .stifleThreadUntilClusterIsOnline()", "" +
                "Thread will sleep if cluster is offline and try to reconnect every second. " +
                "It's easier to let the requests throw exceptions and react to them. So it's pretty useless, but YOLO.");
        list.entry("(6) .setIndexNamePrefix()", "" +
                "This will prepend a custom string to the index name of every registered model (**which allows it**) on instantiation. " +
                "This option is meant to be used for unit testing on the same cluster. If you have two instances of Elsa running, " +
                "using the same models for whatever reason, this will change the index names for both clients because they are static." +
                "variables. **THIS CAN SCREW UP YOUR INDEX.**");
        list.entry("(7) .configureLowLevelClient()", "" +
                "Configuration for Elasticsearch's Low Level REST Client. See "
                + Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-low.html", " here")+".");
        list.entry("(8) .setRequestConfigCallback()", "" +
                "Allows to set proxy, authentication, timeouts, compression, etc");
        list.entry("(9) .setHttpClientConfigCallback()", "" +
                "Allows to set SSL config, UserAgent, options for multithreading (number of thread, ThreadFactory), " +
                "max. connections, proxy (again...?), default headers, etc");
        list.entry("(10) .setFailureListener()", "" +
                "\"Set a listener that gets notified every time a node fails, in case actions need to be taken. " +
                "Used internally when sniffing on failure is enabled.\"");
        list.entry("(11) .setDefaultHeaders()", "" +
                "\"Set the default headers that need to be sent with each request, to prevent having to specify them with each single request\"");
        list.entry("(12) .setMaxRetryTimeoutMillis()", "" +
                "\"Set the timeout that should be honoured in case multiple attempts are made for the same request. " +
                "The default value is 30 seconds, same as the default socket timeout.\"");
        list.entry("(13) .configureBulkProcessor()", "" +
                "Here you can configure the Elasticsearch's native BulkProcessor, see\n" +
                Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-bulk-processor.html", " here") + ". "+
                "for more info.");
        list.entry("(14) .setBulkResponseListener()", "" +
                "This will add a custom listener to the BulkProcessor, see\n" +
                Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-bulk-processor.html", " here")+". "+
                "for more info.");
        list.entry("(15) .registerSnapshotRepositories()", "" +
                "Configure repositories for snapshots. Can be retrieved via `elsa.snapshotter.getRepositoryBucket()`. " +
                "It's a simple `Map<String, SnapshotRepository>` with the repository names as keys.");
        list.entry("(16) .configureGson()", "" +
                "Configure the internal JSON mapping library for default response mapping, i.e. models etc. " +
                "Why GSON and no Jackson? Because it's faster for small JSONs. Why? Because the internet says so.");

        Print.wrapped(list.getAsMarkdown());
    }

}
