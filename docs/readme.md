<!--- PROJECT_TOC -->



### TODO

- What about Flush and Refresh?!

```
final FlushResponse flushResponse = ELSA.client.indices().flush(new FlushRequest(allTestIndices).force(true));
 final RefreshResponse refreshResponse = ELSA.client.indices().refresh(new RefreshRequest(allTestIndices));
```

- How about deleteByQuery & updateByQuery?

## Elastic 7.0

- MappingBuilder of Spring doesn't work 7.0 (_doc field etc). 
- They're working on it now and it will be implemented in 4.0.0. Today (26.04.2020) they're on RC1. 
- Tests work fine with 6.8.8



## Conditional Builder?

```java
public Initializer(final ActorSystem actorSystem) {
    // EITHER THIS
    new Parcon(defaultConfig());
    
    // OR SIMPLY THIS
    new Parcon(config -> {
        if (true) {
            config.parconConfig(null);
        }
    });
}

private Parcon.Configurator defaultConfig() {
    return c -> c
        .parconConfig(null);
}
```



## ElsaClient, DAOs and the BulkProcessor

- The Elasticsearch client manages thread pools which automatically are configured depending on the underlying machine. You should therefore only use one per application. One client is connected to only one cluster. If you want to work on multiple clusters you need multiple clients. In that case you probably will have to [configure the thread pools manually](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-threadpool.html), if performance is a concern. A work-around would be to open and close clients when switching clusters.
- When initializing the `ElsaClient` you can register DAOs, which can then be retrieved via `ElsaClient.getDao(Model.class)`. If you want to work on multiple indices you can also use `ElsaClient.createDAO(DaoConfig)`. The indices must be in the same cluster.
- You don't need to create multiple `Bulkprocessor` instances. Just use the one which `ElsaClient` creates on initialization and throw your raw requests from the DAOs in there and let it do it's magic. You can work on multiple indices at once. If you, for whatever reason, want more BulkProcessors, then it's no problem, because they will use the same thread pool. If you want prioritize specific BulkRequests then put a PriorityQueue in front of it.

 

## Exception handling

Some discussion on this: [GitHub](https://github.com/elastic/elasticsearch/issues/30334), specially [this comment](https://github.com/elastic/elasticsearch/issues/30334#issuecomment-386090562). And [docs](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-low-usage-responses.html#java-rest-low-usage-responses).

ElasticSearch throws different types of exceptions for requests via the High Level HTTP REST client:

- IOException 
  "in case there is a problem sending the request or parsing back the response". Checked exception and it's catching is mandatory. 

  Can also occur as:

  - "ConnectException: Connection refused" (When the cluster is offline)
  - ResponseException: "a response was returned, but its status code indicated an error (not `2xx`)."

- ElasticsearchException
  An informative exception for many causes what went wrong (see example below). It's an unchecked exception, so catching it isn't enforced. 



  Can also occur as:

  - ElasticsearchStatusException



```json
{
	"error": {
		"root_cause": [{
			"type": "resource_already_exists_exception",
			"reason": "index [elsa_bulk_test/kSDT6LO_Qr6HAO3PULteSA] already exists",
			"index_uuid": "kSDT6LO_Qr6HAO3PULteSA",
			"index": "elsa_bulk_test"
		}],
		"type": "resource_already_exists_exception",
		"reason": "index [elsa_bulk_test/kSDT6LO_Qr6HAO3PULteSA] already exists",
		"index_uuid": "kSDT6LO_Qr6HAO3PULteSA",
		"index": "elsa_bulk_test"
	},
	"status": 400
}
```

```json
{
	"error": {
		"root_cause": [{
			"type": "index_not_found_exception",
			"reason": "no such index",
			"resource.type": "index_expression",
			"resource.id": "sadf",
			"index_uuid": "_na_",
			"index": "sadf"
		}],
		"type": "index_not_found_exception",
		"reason": "no such index",
		"resource.type": "index_expression",
		"resource.id": "sadf",
		"index_uuid": "_na_",
		"index": "sadf"
	},
	"status": 404
}
```

