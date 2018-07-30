# 2018-30-07 Update from Elasticsearch 6.2.4 to 6.3.2

Last valid commit: https://github.com/53rg3/elsa/commit/b3f3286bc020338bd1804bff7e2fb57c96d28aaa

**Problems:**

* "6.3 XContentBuilder.string() removed" see [https://github.com/elastic/elasticsearch/issues/31326]()<br>
  Instead of `xContentBuilder.string()` we need to use `Strings.toString(xContentBuilder)`.
* They also changed `ConnectException` to `IOException` because reasons or something. We simply check the Exception Message
  additionally.


