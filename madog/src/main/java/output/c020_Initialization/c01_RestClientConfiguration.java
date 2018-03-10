/*
 * Copyright 2018 Sergej Schaefer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package output.c020_Initialization;

import madog.core.Output;
import madog.core.Print;
import madog.core.Ref;
import madog.markdown.List;


public class c01_RestClientConfiguration extends Output {

    @Override
    public void addMarkDownAsCode() {
        Print.h2("REST Client Configuration");
        Print.wrapped("Elasticsearch's REST client consists of the Low Level Client (LLC) and " +
                "the High Level Client (HLC). The HLC is just a bunch of simplified methods decorated with a LLC. " +
                "Elsa wraps the Elasticsearch's REST client builder with its own builder. The REST client is build with Apache's `HttpAsyncClient`. " +
                "We can therefore use any configuration capabilities `HttpAsyncClientBuilder` and  `RequestConfig.Builder` provide. " +
                "See "+ Ref.externalURL("https://hc.apache.org/httpcomponents-asyncclient-dev/index.html", "here")+". " +
                "");

        Print.h3("Required settings");
        final List required = new List();
        required.entry("Nodes", "" +
                "An array of `HttpHost` objects with the host, port and schema of a node.");
        Print.wrapped(required.getAsMarkdown());



        Print.h3("Optional settings (native)");
        Print.wrapped("See "+Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-low-usage-initialization.html",  "docs")+" for more details about implementation.");
        final List optionalNative = new List();
        optionalNative.entry("Default Headers", "" +
                "An array of `Header` objects consisting of sub-types like `BasicHeader`. These are default headers that need to be sent " +
                "with each request, to prevent having to specify them with each single request");
        optionalNative.entry("setMaxRetryTimeoutMillis", "" +
                "Set the timeout that should be honoured in case multiple attempts are made for the same request. The default value is 30 seconds, " +
                "same as the default socket timeout. In case the socket timeout is customized, the maximum retry timeout should be adjusted accordingly. ");
        optionalNative.entry("setFailureListener", "" +
                "Set a listener that gets notified every time a node fails, in case actions need to be taken. Used internally when sniffing on failure is enabled.");
        Print.wrapped(optionalNative.getAsMarkdown());



        Print.h3("Optional settings (Apache's RequestConfig.Builder)");
        Print.wrapped("Elastic's `RestClientBuilder.setRequestConfigCallback` takes Apache's `RequestConfig.Builder` as argument. We can therefore " +
                "use any settings it provides, see " +
                ""+Ref.externalURL("https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/config/RequestConfig.Builder.html" , "Apache Docs")+". " +
                "You simply create a `RequestConfig.Builder` externally and pass it to `setRequestConfigCallback`. " +
                "For implementation details check: "+Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/_common_configuration.html", "Elastic Docs"));
        Print.wrapped("#### Common configurations");
        final List optionalRequestConfig = new List();
        optionalRequestConfig.entry("Timeouts");
        Print.wrapped(optionalRequestConfig.getAsMarkdown());



        Print.h3("Optional settings (Apache's HttpAsyncClientBuilder)");
        Print.wrapped("Same as with the `RequestConfig.Builder`. For all settings check " +
                Ref.externalURL("http://hc.apache.org/httpcomponents-asyncclient-dev/httpasyncclient/apidocs/org/apache/http/impl/nio/client/HttpAsyncClientBuilder.html", "Apache Docs") + ". " +
                "<br>For implementation details check: " +
                Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/_common_configuration.html", "Elastic Docs"));
        final List optionalHttpAsyncClientBuilder = new List();
        Print.wrapped("#### Common configurations");
        optionalHttpAsyncClientBuilder.entry("Number of threads");
        optionalHttpAsyncClientBuilder.entry("Basic authentication");
        optionalHttpAsyncClientBuilder.entry("Encrypted communication");
        optionalHttpAsyncClientBuilder.entry("Number of threads");
        Print.wrapped(optionalHttpAsyncClientBuilder.getAsMarkdown());





    }

}
