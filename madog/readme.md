[1. ELSA - Elasticsearch Simplified API](/madog/readme.md#elsa---elasticsearch-simplified-api)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.1 Motivation](/madog/readme.md#motivation)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.2 Goals - 5.2.18](/madog/readme.md#goals---5218)<br>
[2. Initialization](/madog/Initialization/readme.md#initialization)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[2.1 REST Client Configuration](/madog/Initialization/readme.md#rest-client-configuration)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[2.1.1 Required settings](/madog/Initialization/readme.md#required-settings)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[2.1.2 Optional settings (native)](/madog/Initialization/readme.md#optional-settings-native)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[2.1.3 Optional settings (Apache's RequestConfig.Builder)](/madog/Initialization/readme.md#optional-settings-apaches-requestconfigbuilder)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[2.1.4 Optional settings (Apache's HttpAsyncClientBuilder)](/madog/Initialization/readme.md#optional-settings-apaches-httpasyncclientbuilder)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[2.2 Elsa Client Configuration](/madog/Initialization/readme.md#elsa-client-configuration)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[2.2.1 Configuring Apache's HttpAsyncClientBuilder & RequestConfig.Builder](/madog/Initialization/readme.md#configuring-apaches-httpasyncclientbuilder--requestconfigbuilder)<br>
[3. Creating Models](/madog/FieldMappings/readme.md#creating-models)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[3.1 Creating an ElsaModel](/madog/FieldMappings/readme.md#creating-an-elsamodel)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[3.1.1 Using the ElsaIndexData builder](/madog/FieldMappings/readme.md#using-the-elsaindexdata-builder)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[3.2 Field Mappings](/madog/FieldMappings/readme.md#field-mappings)<br>
[4. Index Administration](/madog/IndexAdministration/readme.md#index-administration)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[4.1 Create a new index.](/madog/IndexAdministration/readme.md#create-a-new-index)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[4.2 Check if an index exists](/madog/IndexAdministration/readme.md#check-if-an-index-exists)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[4.3 Delete an index](/madog/IndexAdministration/readme.md#delete-an-index)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[4.4 Update an index mapping. ](/madog/IndexAdministration/readme.md#update-an-index-mapping-)<br>
[5. FAQ (Undone)](/madog/FAQ/readme.md#faq-undone)<br>
[6. Bookmarks](/madog/Bookmarks/readme.md#bookmarks)<br>
[7. Ideas for more features](/madog/FeatureIdeas/readme.md#ideas-for-more-features)<br>
# ELSA - Elasticsearch Simplified API
## Motivation

Elasticsearch's Native API is too complicated and Spring Data Elasticsearch is slow, bloated and always one major version behind. It also uses Spring. Ewww.

## Goals - 5.2.18

* **Easier index mapping**<br>
For index mappings in Elasticsearch you either need to use the clunky `XContentBuilder` or keep the mappings as JSON in separate text files - both are shitty solutions. We need some easy-peasy JPA-styleisy annotated fields for sheezy. So we would have everything inside the model class and would be able to modify it easily.
* **Standard CRUD**<br>
Each simple CRUD operation for every model needs its own implementation. We can easily provide those to a model via some abstract class.
* **Automatic ORM**<br>
We could easily provide functionality to map Elasticsearch responses onto the corresponding model objects.
* **Dynamic indices**<br>
Spring Data made it difficult to change indices dynamically. We could simply provide some getter inside the model to provide whatever index we want.
* **Easier configuration**<br>
The native Elasticsearch REST client uses Apache's HttpAsyncClients `RequestConfig.Builder` and `HttpAsyncClientBuilder` for its configuration. This can be simplified. We can also provide different contexts for clients - e.g. a client for `PROD` and one for `TEST` context.
* **Index Management**<br>
If we want to change the index mapping, we need to reindex the whole thing. It looks that this can be simplified too. Same for backups.


