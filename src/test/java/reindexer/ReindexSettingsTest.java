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

package reindexer;

import assets.FakerModel;
import assets.TestModel;
import helpers.IndexName;
import helpers.XJson;
import model.ElsaModel;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import reindexer.ReindexOptions.Conflicts;
import reindexer.ReindexOptions.ScriptingLanguage;
import reindexer.ReindexOptions.VersionType;
import reindexer.ReindexSettings.ReindexSettingsBuilder;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReindexSettingsTest {


    private final Class<? extends ElsaModel> fromIndexClass = FakerModel.class;
    private final Class<? extends ElsaModel> intoIndexClass = TestModel.class;

    @Test
    public void build_full_pass() throws Exception {

        final String expected = "{\"conflicts\":\"proceed\",\"size\":1000,\"source\":{\"remote\":{\"host\":\"http://otherhost:9200\",\"username\":\"user\",\"password\":\"pass\",\"socket_timeout\":\"1m\",\"connect_timeout\":\"10s\"},\"index\":\"twitter\",\"size\":100,\"sort\":{\"date\":\"desc\",\"name\":\"asc\"},\"_source\":[\"user\",\"email\"],\"query\":{\"term\":{\"name\":{\"value\":\"kimchy\",\"boost\":1.0}}}},\"dest\":{\"index\":\"new_twitter\",\"version_type\":\"external\"},\"script\":{\"source\":\"if (ctx._source.foo == 'bar') {ctx._version++; ctx._source.remove('foo')}\",\"lang\":\"painless\"}}";

        final ReindexSettings reindexSettings = new ReindexSettingsBuilder()
                .conflicts(Conflicts.PROCEED)
                .totalSize(1000)
                .configureSource(c -> c
                        .fromIndex("twitter")
                        .selectFields("user", "email")
                        .whereClause(QueryBuilders.termQuery("name", "kimchy"))
                        .remoteHost(r -> r
                                .url("http://otherhost")
                                .port(9200)
                                .userName("user")
                                .password("pass")
                                .socketTimeout(TimeValue.timeValueMinutes(1))
                                .connectTimeout(TimeValue.timeValueSeconds(10)))
                        .batchSize(100)
                        .sortBy(new XJson()
                                .field("date", "desc")
                                .field("name", "asc")))
                .configureDestination(c -> c
                        .intoIndex("new_twitter")
                        .versionType(VersionType.EXTERNAL))
                .configureScript(c -> c
                        .language(ScriptingLanguage.PAINLESS)
                        .script("if (ctx._source.foo == 'bar') {ctx._version++; ctx._source.remove('foo')}"))
                .build();

        JSONAssert.assertEquals(expected, Strings.toString(reindexSettings.getXContentBuilder()), true);

    }

    @Test
    public void build_withClassesAsArguments_pass() throws Exception {

        final String expected = "{\"source\":{\"index\":\""+ IndexName.of(this.fromIndexClass) +"\"},\"dest\":{\"index\":\""+ IndexName.of(this.intoIndexClass) +"\"}}";

        final ReindexSettings reindexSettings = new ReindexSettingsBuilder()
                .configureSource(c -> c
                        .fromIndex(this.fromIndexClass))
                .configureDestination(c -> c
                        .intoIndex(this.intoIndexClass))
                .build();

        final String result = Strings.toString(reindexSettings.getXContentBuilder());

        assertThat(result, is(expected));

    }
    
    @Test(expected = IllegalArgumentException.class)
    public void build_sourceIndexWithClassAndString_throw() {
        final ReindexSettings reindexSettings = new ReindexSettingsBuilder()
                .configureSource(c -> c
                        .fromIndex(this.fromIndexClass)
                        .fromIndex("asdf"))
                .configureDestination(c -> c
                        .intoIndex(this.intoIndexClass))
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_destinationIndexWithClassAndString_throw() {
        final ReindexSettings reindexSettings = new ReindexSettingsBuilder()
                .configureSource(c -> c
                        .fromIndex(this.fromIndexClass))
                .configureDestination(c -> c
                        .intoIndex(this.intoIndexClass)
                        .intoIndex("asdf"))
                .build();
    }

}
