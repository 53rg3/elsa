# 2022-06-16 Update from Elasticsearch 7.8.0 to 7.17.3

- They moved some classes which we also use into different packages, nothing wild but a lot.
- MappingBuilder had to be rebuilt. The thing got even more complex and just copying classes didn't work anymore. But turns out that it's quite easy to instantiate it from the library. No need for Spring dependency injection.
- There are lots of deprecation warnings now. They're also planning on the removing the High Level REST Client?
- Only difficult changes were in `IndexAdmin`, which made some tests fail but all is good now.



# 2020-07-24 Update from Elasticsearch 6.5.4 to 7.8.0

- @Field annotations for Date fields must now contain "format" setting. This is modeled after this:
  https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-date-format.html
  Value you choose must match with your date format, otherwise Elastic throws
- Some small change accessing `totalHits` in a result set. Self-explanatory.



# 2019-09-01 Update from Elasticsearch 6.3.2 to 6.5.4

- Updated spring-data-elasticsearch 3.0.3 to 3.1.3.
- SpringData changed `FieldType.text` to `FieldType.Text`.
- Elastic changed `DeleteIndexResponse` to `AcknowledgedResponse`. Does it look like our confirmation response? Yes, it does.
- Elastic deprecated `restHighLevelClient.bulk(bulkRequest)`, we now need to use `restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT)`.
  `RequestOptions` is some advanced stuff we don't need, see [here](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-hight-getting-started-request-options.html)  



# 2018-30-07 Update from Elasticsearch 6.2.4 to 6.3.2

* "6.3 XContentBuilder.string() removed" see [https://github.com/elastic/elasticsearch/issues/31326]()<br>
  Instead of `xContentBuilder.string()` we need to use `Strings.toString(xContentBuilder)`.
* They also changed `ConnectException` to `IOException` because reasons or something. We simply check the Exception Message
  additionally.
