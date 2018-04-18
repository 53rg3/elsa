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

package output.c040_DAOs;

import madog.core.Output;
import madog.core.Print;
import madog.core.Ref;
import madog.markdown.List;


public class s00_DAOs extends Output {

    @Override
    public void addMarkDownAsCode() {

        Print.h1("DAOs");

        Print.h2("Using ELSA's DAOs");
        Print.wrapped("Get registered DAO via `CrudDAO<YourModel> dao = elsa.getDAO(YourModel.class);`. ");
        List daos = new List();
        daos.entry("CrudDAO", "" +
                "The "+Ref.internalPath("/src/main/java/dao/CrudDAO.java", "CrudDAO")+ " offers CRUD operations. All operations " +
                "can be executed asynchronously, can send individual headers and can use individual `RequestExceptionHandler`. " +
                "`CrudDAO` extends `SearchDAO` and offers all of its methods. Async methods must implement the object mapping " +
                "manually in an instance of the native `ActionListener` of Elasticsearch, see "+
        Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-supported-apis.html", "official docs")+".");
        daos.entry("SearchDAO", "" +
                "The "+Ref.internalPath("/src/main/java/dao/SearchDAO.java", "SearchDAO")+ " handles Elasticsearch's native `SearchRequests`. " +
                "So it can be used with any kind of "+
                Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-query-builders.html", "QueryBuilder")+" and " +
                Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-aggregation-builders.html", "AggregationBuilder")+ ". " +
                "The SearchDAO itself does not offer CRUD operations. Use `CrudDAO` if you also need these. Async methods must implement the object mapping " +
                "manually in an instance of the native `ActionListener` of Elasticsearch, see "+
                Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-supported-apis.html", "official docs")+".");
        Print.wrapped(daos.getAsMarkdown());


        Print.h2("Creating your own DAOs");
        List list = new List();
        list.entry("All DAOs must extend ElsaDAO.");
        list.entry("Use `.getElsa()` to access the ELSA instance in which a DAO is registered, i.e. for the Low Level and High Level REST Client.");
        list.entry("Use `.getJsonMapper()` to map your models `.toJson()` to send it to Elasticsearch or `.fromJson()` to map a Elasticsearch response " +
                "to your model.");
        list.entry("Use `.getModelClass()` to access the `Class` to which an DAO instance is bound to.");
        list.entry("Use `.getSearchResponseMapper()` to conveniently map Elasticsearch's `SearchResponses` to your model.");
        Print.wrapped(list.getAsMarkdown());

    }

}
