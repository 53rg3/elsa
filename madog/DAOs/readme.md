## Table of Contents
[1. DAOs](#daos)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.1 Using ELSA's DAOs](#using-elsas-daos)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.2 Creating your own DAOs](#creating-your-own-daos)<br>
# DAOs
## Using ELSA's DAOs

Get the via `CrudDAO<YourModel> dao = elsa.getDAO(YourModel.class);`. 


* **CrudDAO**<br>
The [CrudDAO](/src/main/java/dao/CrudDAO.java) offers CRUD operations. All operations can be executed asynchronously, can send individual headers and can use individual `RequestExceptionHandler`. `CrudDAO` extends `SearchDAO` and offers all of its methods. Async methods must implement the object mapping manually in an instance of the native `ActionListener` of Elasticsearch, see [official docs](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-supported-apis.html).
* **SearchDAO**<br>
The [SearchDAO](/src/main/java/dao/SearchDAO.java) handles Elasticsearch's native `SearchRequests`. So it can be used with any kind of [QueryBuilder](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-query-builders.html) and [AggregationBuilder](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-aggregation-builders.html). The SearchDAO itself does not offer CRUD operations. Use `CrudDAO` if you also need these. Async methods must implement the object mapping manually in an instance of the native `ActionListener` of Elasticsearch, see [official docs](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-supported-apis.html).


## Creating your own DAOs

* All DAOs must extend ElsaDAO.
* Use `.getElsa()` to access the ELSA instance in which a DAO is registered, i.e. for the Low Level and High Level REST Client.
* Use `.getJsonMapper()` to map your models `.toJson()` to send it to Elasticsearch or `.fromJson()` to map a Elasticsearch response to your model.
* Use `.getModelClass()` to access the `Class` to which an DAO instance is bound to.
* Use `.getSearchResponseMapper()` to conveniently map Elasticsearch's `SearchResponses` to your model.


