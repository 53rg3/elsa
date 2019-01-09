# 2018-30-07 Update from Elasticsearch 6.2.4 to 6.3.2

Last valid commit: https://github.com/53rg3/elsa/commit/b3f3286bc020338bd1804bff7e2fb57c96d28aaa

**Changes:**

* "6.3 XContentBuilder.string() removed" see [https://github.com/elastic/elasticsearch/issues/31326]()<br>
  Instead of `xContentBuilder.string()` we need to use `Strings.toString(xContentBuilder)`.
* They also changed `ConnectException` to `IOException` because reasons or something. We simply check the Exception Message
  additionally.


# 2019-09-01 Update from Elasticsearch 6.3.2 to 6.5.4

Last valid commit: https://github.com/53rg3/elsa/commit/15c7b82f82451b11553a3b7c24de23dbcac06251

**Changes:**

* Updated spring-data-elasticsearch 3.0.3 to 3.1.3.
* SpringData changed `FieldType.text` to `FieldType.Text`.
* Elastic changed `DeleteIndexResponse` to `AcknowledgedResponse`. Does it look like our confirmation response? Yes, it does.
* Elastic deprecated `restHighLevelClient.bulk(bulkRequest)`, we now need to use `restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT)`.
  `RequestOptions` is some advanced stuff we don't need, see [here](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-hight-getting-started-request-options.html)  
