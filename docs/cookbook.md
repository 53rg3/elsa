{:toc}

# Usage



## How to use Date fields?

To use the Date type you need to configure GSON to handle this type so that the field is serialized in a format which Elasticsearch understands. The `GsonUTCDateAdapter` should be sufficient for most cases. Another implementation can be found in `DummyGsonUTCDateAdapter` which is used for testing.

```java
new ElsaClient(_0 -> _0
    .setClusterNodes(httpHosts)
    .configureGson(d -> d
        .registerTypeAdapter(Date.class, new GsonUTCDateAdapter()))
```



## How to use credentials (user + password)?

You need to configure the "low level" `RestClient` which will then use Basic Auth in requests:

```java
final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "elastic"));
HttpClientConfigCallback httpClientConfigCallback = httpClientBuilder -> httpClientBuilder
        .setDefaultCredentialsProvider(credentialsProvider);

new ElsaClient(_0 -> _0
        .configureLowLevelClient(_1 -> _1
                .setHttpClientConfigCallback(httpClientConfigCallback)));
```





# Development



## Working with the test cluster

- `src/test/resources` contains Docker files

- You can run the cluster via:

  ```bash
  docker-compose up
  ```

- Open a shell into the container:

  ```bash
  docker container exec -it elasticsearch-node1 /bin/bash
  ```

- If tests fail, it can happen that the snapshot repository becomes corrupt. Delete the volume and restart the cluster:

  ```
  docker-compose down
  docker volume rm resources_elasticsearch-data1
  docker-compose up
  ```

  

