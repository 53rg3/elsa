## Table of Contents
[1. Modules](#modules)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.1 Overview](#overview)<br>
# Modules

All modules can be directly accessed via the ElsaClient instance, e.g. `elsa.admin`, `elsa.scroller`.

## Overview

* **Client**<br>
Instance of Elasticsearch native High Level REST Client. This gives you also access to the Low Level REST Client.
* **Admin**<br>
CRUD operations for indices, e.g. `.deleteIndex()`, `.createIndex()`, `.indexExists()`, `.updateMapping()`.
* **BulkProcessor**<br>
Exposes Elasticsearch's native BulkProcessor (see[ here](https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-bulk-processor.html)). For a usage example see [BulkProcessorTest.java](/src/test/java/bulkprocessor/BulkProcessorTest.java). 
:heavy_exclamation_mark: Tip: Max out the amount of cores your CPU offers with `.setConcurrentRequests()`. Makes bulk indexing A LOT faster. Default is just '1'.
* **Scroller**<br>
Abstraction for Elasticsearch's scrolling functionality (see [ here](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html).), i.e. retrieval of `SearchRequest` with big result sets. For example you can scroll over the whole index with the `QueryBuilders.matchAllQuery()`. For a usage example see [ScrollerTest.java](/src/test/java/scroller/ScrollerTest.java)
* **Snapshotter**<br>
Abstraction of Elasticsearch's 'snapshot and restore' functionality (see [ here](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-snapshots.html).), i.e. backups. For a usage example see [SnapshotterTest.java](/src/test/java/snapshotter/SnapshotterTest.java)
* **Reindexer**<br>
Abstraction of Elasticsearch's Reindex API, i.e. rebuild an index again. Can be used to change index mappings,  remove, rename and edit fields, create an index with a subset of another index and updating document which match a particular query. For a usage example see [ReindexerTest.java](/src/test/java/reindexer/ReindexerTest.java)
* **GSON**<br>
Exposes the internal JSON mapper library with the settings made in the ElsaClient instantiation. Can be used to parse raw Elasticsearch responses from the Low Level REST Client. See also section [Helpers](/madog/Helpers/readme.md).


