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

package io.github.ss3rg3.elsa.client;

import assets.DateModel;
import assets.DummyGsonUTCDateAdapter;
import assets.TestHelpers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.ss3rg3.elsa.dao.CrudDAO;
import io.github.ss3rg3.elsa.dao.DaoConfig;
import io.github.ss3rg3.elsa.ElsaClient;
import org.elasticsearch.action.index.IndexResponse;
import org.junit.Test;
import io.github.ss3rg3.elsa.statics.ElsaStatics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ModelMapperTest {

    @Test
    public void customGsonDateAdapter_inELsaClientConfig_pass() throws Exception {
        final ElsaClient elsa = new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .registerDAO(new DaoConfig(CrudDAO.class, DateModel.indexConfig))
                .configureGson(d -> d
                        .registerTypeAdapter(Date.class, new DummyGsonUTCDateAdapter("yyyy", Locale.UK, "UTC")))
                .createIndexesAndEnsureMappingConsistency(false)
        );
        final CrudDAO<DateModel> dao = elsa.getDAO(DateModel.class);

        elsa.admin.createIndex(DateModel.indexConfig);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        final DateModel model = new DateModel();
        model.setDate(dateFormat.parse("2018-03-07T13:13:41.664Z"));
        final String expected = "2018-01-01T00:00:00.000Z";

        final IndexResponse response = dao.index(model);
        TestHelpers.sleep(100);
        final DateModel model2 = dao.get(response.getId());

        assertThat(ElsaStatics.UTC_FORMAT.format(model2.getDate()), is(expected));

        elsa.admin.deleteIndex(DateModel.indexConfig);
    }

    @Test
    public void customGsonDateAdapter_inDaoConfigConfig_pass() throws Exception {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DummyGsonUTCDateAdapter("yyyy", Locale.UK, "UTC")).create();
        final ElsaClient elsa = new ElsaClient(c -> c
                .setClusterNodes(TEST_CLUSTER_HOSTS)
                .registerDAO(new DaoConfig(CrudDAO.class, DateModel.indexConfig, gson))
                .createIndexesAndEnsureMappingConsistency(false));
        final CrudDAO<DateModel> dao = elsa.getDAO(DateModel.class);

        elsa.admin.createIndex(DateModel.indexConfig);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        final DateModel model = new DateModel();
        model.setDate(dateFormat.parse("2018-03-07T13:13:41.664Z"));
        final String expected = "2018-01-01T00:00:00.000Z";

        final IndexResponse response = dao.index(model);
        TestHelpers.sleep(100);
        final DateModel model2 = dao.get(response.getId());

        assertThat(ElsaStatics.UTC_FORMAT.format(model2.getDate()), is(expected));

        elsa.admin.deleteIndex(DateModel.indexConfig);
    }
}
