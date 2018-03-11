## Table of Contents
[1. Helpers](#helpers)<br>
# Helpers

* **IndexName**<br>
`IndexName.of(YourModel.class)` retrieves the index name of a model.
* **ModelClass**<br>
`ModelClass.createEmpty(YourModel.class)` create an empty instance of a model. Which is actually useless, because `new YourModel()` would do the same. Hmm.
* **RequestBody**<br>
Transforms different types into a `NStringEntity` which is needed for a request in the Low Level REST Client. Can be used to transform `XContentBuilder`, `XJson` and `Object`.
* **ResponseParser**<br>
Converts an Elasticsearch `Response` into a string or an `InputStreamReader`, which can be passed to GSON (access via `elsa.gson`).
* **Search**<br>
Offers 2 static methods (i.e. use static import) which provide `SearchRequest` and `SearchSourceBuilder`. Makes `SearchRequests` a bit less verbose. Example:

```java
SearchRequest request = Search.req()
        .indices(IndexName.of(FakerModel.class))
        .source(src()
                .size(3)
                .query(rangeQuery("age")
                        .gt(22)
                        .lt(33)));
```
* **XJson**<br>
Offers salvation from the dreaded `XContentBuilder` to build JSON-strings, `Map<String,Object>` and `XContentBuilder` itself.

```java
Date date = new Date();XContentBuilder easyXContentBuilder = new XJson()
        .field("field1", "value1")
        .array("field2", 1, 2, 3)
        .field("field3", date)
        .field("field4", new XNested()
                .field("field1", "value1")
                .array("field2", 1, 2, 3)
                .field("field3", date)
                .end())
        .toXContentBuilder();
```



