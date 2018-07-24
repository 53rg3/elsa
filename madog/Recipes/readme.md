## Table of Contents
[1. Cookbook Recipes](#cookbook-recipes)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.1 Getting the Result Size of a SearchRequest](#getting-the-result-size-of-a-searchrequest)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.2 Creating a separate context of ELSA in Play for testing](#creating-a-separate-context-of-elsa-in-play-for-testing)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.2.1 Injecting static fields & methods](#injecting-static-fields--methods)<br>
# Cookbook Recipes
## Getting the Result Size of a SearchRequest

Use `SearchDAO.search` to execute any `SearchRequest`. Use the returning `SearchResponse` to get the hits count. Use `SearchResponseMapper` to map the `SearchResponse` to your model.

## Creating a separate context of ELSA in Play for testing

Create an interface for injection of the ElsaClient


```JAVA
public interface Elsa {
    ElsaClient getClient();
    Gson getGson();
}
```


Create an implementation for production of the ElsaClient. Notice the `@Singleton`


```JAVA
@Singleton
public class ElsaProd implements Elsa {

    private final HttpHost[] httpHosts = {new HttpHost("127.0.0.1", 9200, "http")};
    private final ElsaClient client = new ElsaClient(c -> c
            .setClusterNodes(this.httpHosts)
            .stifleThreadUntilClusterIsOnline(true)
            .registerModel(Video.class, CrudDAO.class));

    @Override
    public ElsaClient getClient() {
        return this.client;
    }

    @Override
    public Gson getGson() {
        return this.client.gson;
    }
}
```


Add or configure your Guice Module to bind the interface to the implementation (in `/app/Module.java` if not configured otherwise in `application.conf`).


```JAVA
public class Module extends AbstractModule {
    @Override
    protected void configure() {
        bind(Elsa.class).to(ElsaProd.class);
    }
}
```


Now constructors, methods and fields annotated with `@Inject` in your application will use your `Elsa` interface with the configured `ElsaProd` implementation. Notice that we can use `final` if we use the constructor injection, which wouldn't work if we injected into the field.


```JAVA
public class VideoLookup {

    private final Elsa elsa;
    private final CrudDAO<Video> dao;

    @Inject
    public VideoLookup(Elsa elsa) {
        this.elsa = elsa;
        this.dao = this.elsa.getClient().getDAO(Video.class);
    }

    public void index(final Video video) {
        this.dao.index(video);
    }
    
}
```

---

For testing we need to create a fake application which overrides the `Elsa.class` binding with our testing implementation. I.e. like the `ElsaProd.class`, but with an index prefix or other hosts. We can then simply use `Application.injector().instanceOf(SomeClassWithInjections.class)` to get another implementation. 


```JAVA
public class FakeApp {

    private static Application INSTANCE = new GuiceApplicationBuilder()
            .overrides(bind(Elsa.class).to(ElsaTest.class))
            .build();

    public static <T> T create(Class<T> clazz) {
        return INSTANCE.injector().instanceOf(clazz);
    }

}
```


With our unnecessary helper method `FakeApp.create(clazz)` we can now simply do this: 


```JAVA
public class TryOuts {

    private VideoLookup videoLookup = FakeApp.create(VideoLookup.class);

    @Test
    public void index() {
        videoLookup.index(new Video());
    }

}
```


Guice will now use our `ElsaTest.class` for the injection.

### Injecting static fields & methods

Add `requestStaticInjection(ClassWithStaticInjections.class)` to your module.


```JAVA
public class Module extends AbstractModule {
    @Override
    protected void configure() {
        bind(Elsa.class).to(ElsaProd.class);
        requestStaticInjection(StaticHelpers.class);
    }
}
```


But it's should be considered as a crutch: [https://stackoverflow.com/a/28517826/4179212](https://stackoverflow.com/a/28517826/4179212)

