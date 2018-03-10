## Table of Contents
[1. Initialization](#initialization)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.1 REST Client Configuration](#rest-client-configuration)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.1.1 Required settings](#required-settings)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.1.2 Optional settings (native)](#optional-settings-native)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.1.3 Optional settings (Apache's RequestConfig.Builder)](#optional-settings-apaches-requestconfigbuilder)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.1.4 Optional settings (Apache's HttpAsyncClientBuilder)](#optional-settings-apaches-httpasyncclientbuilder)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.2 Elsa Client Configuration](#elsa-client-configuration)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.2.1 Configuring Apache's HttpAsyncClientBuilder & RequestConfig.Builder](#configuring-apaches-httpasyncclientbuilder--requestconfigbuilder)<br>
# Initialization

See [Elastic Docs](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-low-usage-initialization.html) for current version and more details.

## REST Client Configuration

Elasticsearch's REST client consists of the Low Level Client (LLC) and the High Level Client (HLC). The HLC is just a bunch of simplified methods decorated with a LLC. Elsa wraps the Elasticsearch's REST client builder with its own builder. The REST client is build with Apache's `HttpAsyncClient`. We can therefore use any configuration capabilities `HttpAsyncClientBuilder` and  `RequestConfig.Builder` provide. See [here](https://hc.apache.org/httpcomponents-asyncclient-dev/index.html). 

### Required settings

* **Nodes**<br>
An array of `HttpHost` objects with the host, port and schema of a node.


### Optional settings (native)

See [docs](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-low-usage-initialization.html) for more details about implementation.


* **Default Headers**<br>
An array of `Header` objects consisting of sub-types like `BasicHeader`. These are default headers that need to be sent with each request, to prevent having to specify them with each single request
* **setMaxRetryTimeoutMillis**<br>
Set the timeout that should be honoured in case multiple attempts are made for the same request. The default value is 30 seconds, same as the default socket timeout. In case the socket timeout is customized, the maximum retry timeout should be adjusted accordingly. 
* **setFailureListener**<br>
Set a listener that gets notified every time a node fails, in case actions need to be taken. Used internally when sniffing on failure is enabled.


### Optional settings (Apache's RequestConfig.Builder)

Elastic's `RestClientBuilder.setRequestConfigCallback` takes Apache's `RequestConfig.Builder` as argument. We can therefore use any settings it provides, see [Apache Docs](https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/config/RequestConfig.Builder.html). You simply create a `RequestConfig.Builder` externally and pass it to `setRequestConfigCallback`. For implementation details check: [Elastic Docs](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/_common_configuration.html)


#### Common configurations


* Timeouts


### Optional settings (Apache's HttpAsyncClientBuilder)

Same as with the `RequestConfig.Builder`. For all settings check [Apache Docs](http://hc.apache.org/httpcomponents-asyncclient-dev/httpasyncclient/apidocs/org/apache/http/impl/nio/client/HttpAsyncClientBuilder.html). <br>For implementation details check: [Elastic Docs](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/_common_configuration.html)


#### Common configurations


* Number of threads
* Basic authentication
* Encrypted communication
* Number of threads


## Elsa Client Configuration

Simplified `Builder` for Elastic's High Level REST Client with some additional options. Example:


```JAVA
final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
final Header[] defaultHeaders = {new BasicHeader("name", "value")};
final ElsaClient elsa = new ElsaClient.Builder(httpHosts)
        .setDefaultHeaders(defaultHeaders)                     // (1)
        .setFailureListener(new FailureListener())             // (1)
        .setMaxRetryTimeoutMillis(10000)                       // (1)
        .configureApacheHttpAsyncClientBuilder(config -> config
                        .setMaxConnTotal(1)
                        .setUserAgent("MyUserAgent"))        // (2)
        .configureApacheRequestConfigBuilder(config -> config
                        .setAuthenticationEnabled(true)
                        .setConnectTimeout(1000))              // (3)
        .build();
```


1. See [REST Client Configuration](/madog/Initialization/readme.md) for details.
2. This configures Apache's HttpAsyncClientBuilder. See blow for example. See [here](http://hc.apache.org/httpcomponents-asyncclient-dev/httpasyncclient/apidocs/org/apache/http/impl/nio/client/HttpAsyncClientBuilder.html) for official documentation.
3. This configures Apache's RequestConfig.Builder. See blow for example. See [here](https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/config/RequestConfig.Builder.html) for official documentation.


### Configuring Apache's HttpAsyncClientBuilder & RequestConfig.Builder

Elastic's REST client gives you the ability to meddle with the config of the underlining Apache HttpAsyncClient by providing a functional interface to access the internally used builders. So you either configure them via a Lambda (see first example) or you provide your own method with a suited method signature. Latter option is useful if you have more complicated configuration. Example: 


```JAVA
private ElsaClient createElsaClient() {
    final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
    return new ElsaClient.Builder(httpHosts)
            .configureApacheHttpAsyncClientBuilder(this::createHttpClientConfigCallback)
            .build();
}

private HttpAsyncClientBuilder createHttpClientConfigCallback(final HttpAsyncClientBuilder httpAsyncClientBuilder) {
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("user", "password"));

    IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
            .setIoThreadCount(1)
            .build();

    return httpAsyncClientBuilder
            .disableAuthCaching()
            .setDefaultCredentialsProvider(credentialsProvider)
            .setDefaultIOReactorConfig(ioReactorConfig);
}
```

