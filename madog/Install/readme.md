## Table of Contents
[1. Install](#install)<br>
# Install

Clone the repository


```bash
git clone https://github.com/53rg3/elsa.git
```


Let Maven install it into the local repository. If you want to run tests you need a running cluster at `http://localhost:9200`.


```bash
mvn clean install -Dmaven.test.skip=true
```


Add the dependency in the POM. Make sure the versions match (see pom.xml in repository)


```xml
<dependency>
    <groupId>io.github.53rg3</groupId>
    <artifactId>elsa</artifactId>
    <version>0.1</version>
</dependency>
```

