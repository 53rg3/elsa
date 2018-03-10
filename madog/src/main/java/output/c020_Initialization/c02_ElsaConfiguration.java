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


public class c02_ElsaConfiguration extends Output {

    @Override
    public void addMarkDownAsCode() {
        Print.h2("Elsa Client Configuration");
        Print.wrapped("Simplified `Builder` for Elastic's High Level REST Client with some additional options. Example:");

        Print.codeBlock("" +
                "final HttpHost[] httpHosts = {new HttpHost(\"localhost\", 9200, \"http\")};\n" +
                "final Header[] defaultHeaders = {new BasicHeader(\"name\", \"value\")};\n" +
                "final ElsaClient elsa = new ElsaClient.Builder(httpHosts)\n" +
                "        .setDefaultHeaders(defaultHeaders)                     // (1)\n" +
                "        .setFailureListener(new FailureListener())             // (1)\n" +
                "        .setMaxRetryTimeoutMillis(10000)                       // (1)\n" +
                "        .configureApacheHttpAsyncClientBuilder(config -> config\n" +
                "                        .setMaxConnTotal(1)\n" +
                "                        .setUserAgent(\"MyUserAgent\"))        // (2)\n" +
                "        .configureApacheRequestConfigBuilder(config -> config\n" +
                "                        .setAuthenticationEnabled(true)\n" +
                "                        .setConnectTimeout(1000))              // (3)\n" +
                "        .build();" +
                "");

        List explainer = new List();
        explainer.isNumberedList(true);

        explainer.entry("See "+Ref.outputClass(c01_RestClientConfiguration.class, "REST Client Configuration") + " for details.");
        explainer.entry("This configures Apache's HttpAsyncClientBuilder. See blow for example. See " +
                ""+Ref.externalURL("http://hc.apache.org/httpcomponents-asyncclient-dev/httpasyncclient/apidocs/org/apache/http/impl/nio/client/HttpAsyncClientBuilder.html", "here")+" " +
                "for official documentation.");
        explainer.entry("This configures Apache's RequestConfig.Builder. See blow for example. See " +
                ""+Ref.externalURL("https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/config/RequestConfig.Builder.html", "here")+" " +
                "for official documentation.");
        Print.wrapped(explainer.getAsMarkdown());



        Print.h3("Configuring Apache's HttpAsyncClientBuilder & RequestConfig.Builder");
        Print.wrapped("" +
                "Elastic's REST client gives you the ability to meddle with the config of the underlining Apache HttpAsyncClient by " +
                "providing a functional interface to access the internally used builders. So you either configure them via a Lambda (see first example) or you provide your own method " +
                "with a suited method signature. Latter option is useful if you have more complicated configuration. Example: ");
        Print.codeBlock("" +
                "private ElsaClient createElsaClient() {\n" +
                "    final HttpHost[] httpHosts = {new HttpHost(\"localhost\", 9200, \"http\")};\n" +
                "    return new ElsaClient.Builder(httpHosts)\n" +
                "            .configureApacheHttpAsyncClientBuilder(this::createHttpClientConfigCallback)\n" +
                "            .build();\n" +
                "}\n" +
                "\n" +
                "private HttpAsyncClientBuilder createHttpClientConfigCallback(final HttpAsyncClientBuilder httpAsyncClientBuilder) {\n" +
                "    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();\n" +
                "    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(\"user\", \"password\"));\n" +
                "\n" +
                "    IOReactorConfig ioReactorConfig = IOReactorConfig.custom()\n" +
                "            .setIoThreadCount(1)\n" +
                "            .build();\n" +
                "\n" +
                "    return httpAsyncClientBuilder\n" +
                "            .disableAuthCaching()\n" +
                "            .setDefaultCredentialsProvider(credentialsProvider)\n" +
                "            .setDefaultIOReactorConfig(ioReactorConfig);\n" +
                "}" +
                "");
    }

}
