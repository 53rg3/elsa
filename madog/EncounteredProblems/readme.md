## Table of Contents
[1. Encountered Problems](#encountered-problems)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.1 NoClassDefFoundError for Log4J](#noclassdeffounderror-for-log4j)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.2 Saving Arrays:](#saving-arrays)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.3 Using bulk processor leads to TimeoutException](#using-bulk-processor-leads-to-timeoutexception)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.4 Gson.toJson causes StackOverflowError](#gsontojson-causes-stackoverflowerror)<br>
# Encountered Problems
## NoClassDefFoundError for Log4J

`java.lang.NoClassDefFoundError: org/apache/logging/log4j/util/MultiFormatStringBuilderFormattable`<br/>`log4j-api` must be added to `pom.xml`.

## Saving Arrays:

```JAVA
@Field(type = FieldType.Object)
private List<String> arrayField;
```


Leads to: `ElasticsearchStatusException[Elasticsearch exception [type=mapper_parsing_exception, reason=object mapping for [arrayField] tried to parse field [null] as object, but found a concrete value]`


Use @Field(type = FieldType.text) instead. "In Elasticsearch, there is no dedicated|array|type. Any field can contain zero or more values by default, however, all values in the array must be of the same datatype." i.e. a list / array is no object.

## Using bulk processor leads to TimeoutException

```JAVA
private final ElsaClient elsa = new ElsaClient.Builder(httpHosts)
        .registerModel(BulkModel.class, ElsaDAO.class)
        .configureApacheRequestConfigBuilder(config -> config.setConnectionRequestTimeout(0))
        .build();
```


More info: [https://github.com/elastic/elasticsearch/issues/24069](https://github.com/elastic/elasticsearch/issues/24069)

## Gson.toJson causes StackOverflowError

IndexConfig field must be static. Otherwise GSON tries to serialize it, which seemingly has some circular reference in it.

