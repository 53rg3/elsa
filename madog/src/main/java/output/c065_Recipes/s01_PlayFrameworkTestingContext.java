/*
 * Copyright 2018 Sergej Schaefer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package output.c065_Recipes;

import madog.core.Output;
import madog.core.Print;


public class s01_PlayFrameworkTestingContext extends Output {

    @Override
    public void addMarkDownAsCode() {

        Print.h2("Creating a separate context of ELSA in Play for testing");

        Print.wrapped("Create an interface for injection of the ElsaClient");
        Print.codeBlock("" +
                "public interface Elsa {\n" +
                "    ElsaClient getClient();\n" +
                "    Gson getGson();\n" +
                "}");

        Print.wrapped("Create an implementation for production of the ElsaClient. Notice the `@Singleton`");
        Print.codeBlock("" +
                "@Singleton\n" +
                "public class ElsaProd implements Elsa {\n" +
                "\n" +
                "    private final HttpHost[] httpHosts = {new HttpHost(\"127.0.0.1\", 9200, \"http\")};\n" +
                "    private final ElsaClient client = new ElsaClient(c -> c\n" +
                "            .setClusterNodes(this.httpHosts)\n" +
                "            .stifleThreadUntilClusterIsOnline(true)\n" +
                "            .registerModel(Video.class, CrudDAO.class));\n" +
                "\n" +
                "    @Override\n" +
                "    public ElsaClient getClient() {\n" +
                "        return this.client;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Gson getGson() {\n" +
                "        return this.client.gson;\n" +
                "    }\n" +
                "}" +
                "");

        Print.wrapped("Add or configure your Guice Module to bind the interface to the implementation (in `/app/Module.java` " +
                "if not configured otherwise in `application.conf`).");
        Print.codeBlock("" +
                "public class Module extends AbstractModule {\n" +
                "    @Override\n" +
                "    protected void configure() {\n" +
                "        bind(Elsa.class).to(ElsaProd.class);\n" +
                "    }\n" +
                "}");

        Print.wrapped("Now constructors, methods and fields annotated with `@Inject` in your application will use your " +
                "`Elsa` interface with the configured `ElsaProd` implementation. Notice that we can use `final` if we use " +
                "the constructor injection, which wouldn't work if we injected into the field.");
        Print.codeBlock("" +
                "public class VideoLookup {\n" +
                "\n" +
                "    private final Elsa elsa;\n" +
                "    private final CrudDAO<Video> dao;\n" +
                "\n" +
                "    @Inject\n" +
                "    public VideoLookup(Elsa elsa) {\n" +
                "        this.elsa = elsa;\n" +
                "        this.dao = this.elsa.getClient().getDAO(Video.class);\n" +
                "    }\n" +
                "\n" +
                "    public void index(final Video video) {\n" +
                "        this.dao.index(video);\n" +
                "    }\n" +
                "    \n" +
                "}");

        Print.separator();

        Print.wrapped("For testing we need to create a fake application which overrides the `Elsa.class` binding with our " +
                "testing implementation. I.e. like the `ElsaProd.class`, but with an index prefix or other hosts. We can then " +
                "simply use `Application.injector().instanceOf(SomeClassWithInjections.class)` to get another implementation. ");
        Print.codeBlock("" +
                "public class FakeApp {\n" +
                "\n" +
                "    private static Application INSTANCE = new GuiceApplicationBuilder()\n" +
                "            .overrides(bind(Elsa.class).to(ElsaTest.class))\n" +
                "            .build();\n" +
                "\n" +
                "    public static <T> T create(Class<T> clazz) {\n" +
                "        return INSTANCE.injector().instanceOf(clazz);\n" +
                "    }\n" +
                "\n" +
                "}");

        Print.wrapped("With our unnecessary helper method `FakeApp.create(clazz)` we can now simply do this: ");
        Print.codeBlock("" +
                "public class TryOuts {\n" +
                "\n" +
                "    private VideoLookup videoLookup = FakeApp.create(VideoLookup.class);\n" +
                "\n" +
                "    @Test\n" +
                "    public void index() {\n" +
                "        videoLookup.index(new Video());\n" +
                "    }\n" +
                "\n" +
                "}");

        Print.wrapped("Guice will now use our `ElsaTest.class` for the injection.");


        Print.h3("Injecting static fields & methods");
        Print.wrapped("Add `requestStaticInjection(ClassWithStaticInjections.class)` to your module.");
        Print.codeBlock("" +
                "public class Module extends AbstractModule {\n" +
                "    @Override\n" +
                "    protected void configure() {\n" +
                "        bind(Elsa.class).to(ElsaProd.class);\n" +
                "        requestStaticInjection(StaticHelpers.class);\n" +
                "    }\n" +
                "}");

    }

}
