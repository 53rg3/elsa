<!--- PROJECT_TOC -->

# Ruminations for 0.2

- No pseudo-Optionals
  - They're fake and gay. Throw a shit load of exceptions.
- Don't we have some exception mapping? Was that useful? Why not pass it as it is and let user decide what to do.



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

