## Table of Contents
[1. Modules](#modules)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.1 Overview](#overview)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.2 BulkProcessor](#bulkprocessor)<br>
# Modules

All modules can be directly accessed via the ElsaClient instance, e.g. `elsa.admin`, `elsa.scroller`.

## Overview

* **Client**<br>
Instance of Elasticsearch native High Level REST Client. This gives you also access to the Low Level REST Client.
* **Admin**<br>
CRUD operations for indices, e.g. `.deleteIndex()`, `.createIndex()`, `.indexExists()`, `.updateMapping()`.
* **BulkProcessor**<br>
Exposes Elasticsearch's native BulkProcessor. See[ here](https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-bulk-processor.html). For an usage example see [BulkProcessorTest.java](/src/test/java/bulkprocessor/BulkProcessorTest.java)
* **Scroller**<br>
Abstraction for Elasticsearch's scrolling functionality, i.e. retrieval of `SearchRequest` with big result sets. For example you can scroll over the whole index with the `QueryBuilders.matchAllQuery()`. See[ here](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html).
* **Snapshotter**<br>
Abstraction of Elasticsearch's 'snapshot and restore' functionality, i.e. backups. See [ here](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-snapshots.html).
* **Reindexer**<br>
Abstraction of Elasticsearch's Reindex API, i.e. rebuild an index again. Can be used to change index mappings,  remove, rename and edit fields, create an index with a subset of another index and updating document which match a particular query.
* **GSON**<br>
Exposes the internal JSON mapper library with the settings made in the ElsaClient instantiation.


## BulkProcessor

All modules can be directly accessed via the ElsaClient instance, e.g. `elsa.admin`, `elsa.scroller`.

