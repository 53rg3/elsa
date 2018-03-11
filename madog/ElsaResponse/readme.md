## Table of Contents
[1. ElsaResponse](#elsaresponse)<br>
# ElsaResponse

All requests to Elasticsearch can throw exceptions in case the cluster is offline, an index does not exist, a request is malformed, etc. These exceptions mostly have JSON inside the message with additional information. ELSA tries to parse the message and to pass them back to the caller. `ElsaResponse` is an imitation of Java's `Optional` and encapsulates successful responses as well as Exceptions and provides a convenient way to react to both without try-catch blocks.

Example:


```JAVA
DeleteIndexResponse response = elsa.admin.deleteIndex("does_not_exist")
            .orElseThrow(IllegalStateException::new);
ElsaResponse<DeleteIndexResponse> response = elsa.admin.deleteIndex("does_not_exist");
if(response.hasException()) {
   ExceptionResponse exceptionResponse = response.getExceptionResponse();
}
if(response.isPresent()) {
   YourModel model = response.get();
}

```

