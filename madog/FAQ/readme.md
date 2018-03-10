## Table of Contents
[1. FAQ (Undone)](#faq-undone)<br>
# FAQ (Undone)

* **What's the difference between REST HLC & LLC? When to use which?**<br>
The high-level REST client, which is based on the low-level client, takes care of request marshalling and response un-marshalling. The HLC is just a bunch of simplified methods decorated with a LLC. So it's actually just a wrapper which provides more functionality.
* **TransportClient vs new REST Client?**<br>
Transport client uses TCP to communicate. It is used internally for communication in an Elasticsearch cluster. REST client uses HTTP. REST client is slower, but the performance is acceptable. Basically, the company just wanted less effort and a single method for all users to communicate with the cluster.


