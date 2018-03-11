## Table of Contents
[1. Create an ElsaClient instance](#create-an-elsaclient-instance)<br>
[2. Minimal configuration](#minimal-configuration)<br>
[3. Minimal configuration](#minimal-configuration)<br>
# Create an ElsaClient instance
# Minimal configuration

```JAVA
private final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
private final ElsaClient elsa = new ElsaClient(c -> c
        .setClusterNodes(httpHosts)
        .registerModel(YourModel.class, CrudDAO.class));
```

# Minimal configuration

**BAD CONFIGURATION. DO NOT COPY & PASTE**


```JAVA
private final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
private final ElsaClient elsa = new ElsaClient(c0 -> c0
        .setClusterNodes(httpHosts) // (1)
        .registerModel(FakerModel.class, CrudDAO.class) // (2)
        .registerModel(TestModel.class, SearchDAO.class)
        .createIndexesAndEnsureMappingConsistency(true) // (3)
        .setDefaultRequestExceptionHandler(new JustLogExceptionHandler()) // (4)
        .stifleThreadUntilClusterIsOnline(false) // (5)
        .setIndexNamePrefix("test_") // (6)
        .configureLowLevelClient(c1 -> c1 // (7)
                .setRequestConfigCallback(c2 -> c2 // (8)
                        .setAuthenticationEnabled(false)
                        .setConnectTimeout(60000))
                .setHttpClientConfigCallback(c3 -> c3 // (9)
                        .setUserAgent("your_company")
                        .setMaxConnTotal(100))
                .setFailureListener(new FailureListener()) // (10)
                .setDefaultHeaders(new Header[]{}) // (11)
                .setMaxRetryTimeoutMillis(10000)) // (12)
        .configureBulkProcessor(c4 -> c4 // (13)
                .setBulkActions(66)
                .setFlushInterval(TimeValue.timeValueSeconds(10)))
        .setBulkResponseListener(new DefaultBulkResponseListener()) // (14)
        .registerSnapshotRepositories(c5 -> c5 // (15)
                .add(new SnapshotRepository("daily_backups", "/mnt/daily_backups"))
                .add(new SnapshotRepository("monthly_backups", "/mnt/monthly_backups")))
        .configureGson(c6 -> c6 // (16)
                .setDateFormat("yyyy-MM-dd")
                .registerTypeAdapter(Date.class, new GsonUTCDateAdapter()))
);
```


* **(1) .setClusterNodes()**<br>
Array of `HttpHost` with your Elasticsearch nodes. Can't be refreshed. Needs a new client instantiation.
* **(2) .registerModel()**<br>
The model and the corresponding DAO you want to use with it. Retrieve it via ``elsa.getDAO(FakerModel.class)
* **(3) .createIndexesAndEnsureMappingConsistency()**<br>
If the indices do not exist, then they will be created. If they exist, their mapping will be updated. If the new mapping is invalid, then this fail on startup.
* **(4) .setDefaultRequestExceptionHandler()**<br>
In some cases the Elasticsearch cluster responds with Exceptions. You can a pass a default exception handler which will be invoked if no custom handler was passed as argument to the request methods.
* **(5) .stifleThreadUntilClusterIsOnline()**<br>
Thread will sleep if cluster is offline and try to reconnect every second. It's easier to let the requests throw exceptions and react to them. So it's pretty useless, but YOLO.
* **(6) .setIndexNamePrefix()**<br>
This will prepend a custom string to the index name of every registered model (**which allows it**) on instantiation. This option is meant to be used for unit testing on the same cluster. If you have two instances of Elsa running, using the same models for whatever reason, this will change the index names for both clients because they are static.variables. **THIS CAN SCREW UP YOUR INDEX.**
* **(7) .configureLowLevelClient()**<br>
Configuration for Elasticsearch's Low Level REST Client. See [ here](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-low.html).
* **(8) .setRequestConfigCallback()**<br>
Allows to set proxy, authentication, timeouts, compression, etc
* **(9) .setHttpClientConfigCallback()**<br>
Allows to set SSL config, UserAgent, options for multithreading (number of thread, ThreadFactory), max. connections, proxy (again...?), default headers, etc
* **(10) .setFailureListener()**<br>
"Set a listener that gets notified every time a node fails, in case actions need to be taken. Used internally when sniffing on failure is enabled."
* **(11) .setDefaultHeaders()**<br>
"Set the default headers that need to be sent with each request, to prevent having to specify them with each single request"
* **(12) .setMaxRetryTimeoutMillis()**<br>
"Set the timeout that should be honoured in case multiple attempts are made for the same request. The default value is 30 seconds, same as the default socket timeout."
* **(13) .configureBulkProcessor()**<br>
Here you can configure the Elasticsearch's native BulkProcessor, see
[ here](https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-bulk-processor.html). for more info.
* **(14) .setBulkResponseListener()**<br>
This will add a custom listener to the BulkProcessor, see
[ here](https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-bulk-processor.html). for more info.
* **(15) .registerSnapshotRepositories()**<br>
Configure repositories for snapshots. Can be retrieved via `elsa.snapshotter.getRepositoryBucket()`. It's a simple `Map<String, SnapshotRepository>` with the repository names as keys.
* **(16) .configureGson()**<br>
Configure the internal JSON mapping library for default response mapping, i.e. models etc. Why GSON and no Jackson? Because it's faster for small JSONs. Why? Because the internet says so.


