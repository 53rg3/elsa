[1. Install](/madog/Install/readme.md#install)<br>
[2. Create an ElsaClient instance](/madog/ElsaClient/readme.md#create-an-elsaclient-instance)<br>
[3. Minimal configuration](/madog/ElsaClient/readme.md#minimal-configuration)<br>
[4. Minimal configuration](/madog/ElsaClient/readme.md#minimal-configuration)<br>
[5. Modules](/madog/Modules/readme.md#modules)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[5.1 Overview](/madog/Modules/readme.md#overview)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[5.2 BulkProcessor](/madog/Modules/readme.md#bulkprocessor)<br>
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

