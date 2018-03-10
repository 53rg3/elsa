## Table of Contents
[1. Creating Models](#creating-models)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.1 Creating an ElsaModel](#creating-an-elsamodel)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.1.1 Using the ElsaIndexData builder](#using-the-elsaindexdata-builder)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.2 Field Mappings](#field-mappings)<br>
# Creating Models
## Creating an ElsaModel

* For a simple example of a valid ElsaModel see [Example Model](/madog/_resources/examples/ElsaModelExample.java). 
* A model class must implement `ElsaModel` and return an instance of `ElsaIndexData` with the overridden `getIndexData()` method.
* Use the `ElsaIndexData.Builder()` to create the `ElsaIndexData` field.
* Create the `ElsaIndexData` field as `private static final` so all models of this type will share the same instance.
* IndexName is mutable, changing is thread-safe. All other settings are immutable.
* If there are other constructors than the default one, then an empty constructor needs to be provided.


### Using the ElsaIndexData builder

:mag_right:  The settings for indexName, type, amount of shards, amount of replicas are mandatory. 

Example: 


```JAVA
private static final ElsaIndexData indexData = new ElsaIndexData.Builder()
        .setIndexName("youtube")
        .setType("video")
        .setShards(1)
        .setReplicas(1)
        .build();
```

## Field Mappings

* We can use any mapping annotations from Spring Data Elasticsearch as defined in [Field](https://docs.spring.io/spring-data/elasticsearch/docs/current/api/org/springframework/data/elasticsearch/annotations/Field.html), with all defined field types as defined in [FieldData](https://docs.spring.io/spring-data/elasticsearch/docs/current/api/org/springframework/data/elasticsearch/annotations/FieldType.html).
* We used Spring Data Elasticsearch version **3.0.3** for this.
* For an example check [SampleDateMappingEntity](https://github.com/spring-projects/spring-data-elasticsearch/blob/master/src/test/java/org/springframework/data/elasticsearch/entities/SampleDateMappingEntity.java).


