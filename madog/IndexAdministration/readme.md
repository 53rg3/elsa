## Table of Contents
[1. Index Administration](#index-administration)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.1 Create a new index.](#create-a-new-index)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.2 Check if an index exists](#check-if-an-index-exists)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.3 Delete an index](#delete-an-index)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.4 Update an index mapping. ](#update-an-index-mapping-)<br>
# Index Administration

The index can be administrated via `ElsaClient.admin`.


First, create a client:


```JAVA
private final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
private final ElsaClient elsa = new ElsaClient.Builder(httpHosts)
        .registerModel(YourModel.class, YourDAO.class)
        .build();
```

## Create a new index.

The mappings are directly create with the provided model.


```JAVA
CreateIndexResponse response = elsa.admin.createIndex(YourModel.class);
```

## Check if an index exists

```JAVA
boolean result = elsa.admin.indexExists(YourModel.class);
boolean result = elsa.admin.indexExists("INDEX_NAME_STRING");
```

## Delete an index

```JAVA
DeleteIndexResponse response = elsa.admin.deleteIndex(YourModel.class);
DeleteIndexResponse response = elsa.admin.deleteIndex("INDEX_NAME_STRING");
```

## Update an index mapping. 

:heavy_exclamation_mark: Caution: You can only add fields. Trying to change a field mapping will result in answered with an exception from Elasticsearch. See [official docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-put-mapping.html). Changing existing fields requires reindexing. 


```JAVA
Response response = elsa.admin.updateMapping(YourExtendedModel.class);
```

