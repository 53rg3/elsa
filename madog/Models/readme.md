## Table of Contents
[1. Models](#models)<br>
# Models

For a usage example see [TestModel.java](/src/test/java/assets/TestModelWithAddedMappings.java)


**Requirements**


* Models must implement the `ElsaModel` interface.
* All methods must be implemented properly. Otherwise ELSA will throw exceptions on startup.
* Models must set up the `IndexConfig` instance properly. Otherwise ELSA will throw exceptions on startup.
* The ID field should be marked as `transient`. Otherwise Elasticsearch will save them in `_source`.
* Dynamic index naming must be allowed explicitly. `IndexConfig` is static, so changing the index name in one instance of the model will also change it in all other instances of the same model. The `IndexConfig` is accessible via `.getIndexConfig()`.
* All index mappings can be created via Spring Data Elasticsearch annotations. If a field is not marked as `transient` and dynamic mapping in Elasticsearch is not disabled, then it will be indexed by Elasticsearch.
* Mappings can't be changed once created. You can only add fields. Changing a mapping, e.g. configuring a different analyzer, requires reindexing of the whole index.


