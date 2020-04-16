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

package client;

import assets.DateModel;
import assets.DummyGsonUTCDateAdapter;
import assets.TestHelpers;
import dao.CrudDAO;
import org.elasticsearch.action.index.IndexResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import responses.ElsaResponse;
import statics.ElsaStatics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ModelMapperTest {

    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerModel(DateModel.class, CrudDAO.class)
            .configureGson(d->d
                    .registerTypeAdapter(Date.class, new DummyGsonUTCDateAdapter("yyyy", Locale.UK, "UTC")))
            .createIndexesAndEnsureMappingConsistency(false)
    );
    private static final CrudDAO<DateModel> dao = elsa.getDAO(DateModel.class);

    @BeforeClass
    public static void setup() {
        elsa.admin.createIndex(DateModel.class);
    }

    @AfterClass
    public static void tearDown() {
        elsa.admin.deleteIndex(DateModel.class);
    }
    
    @Test
    public void gsonDateAdapter_datesAreFormatted_pass() throws Exception {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        final DateModel model = new DateModel();
        model.setDate(dateFormat.parse("2018-03-07T13:13:41.664Z"));
        final String expected  = "2018-01-01T00:00:00.000Z";

        final ElsaResponse<IndexResponse> response = dao.index(model);
        TestHelpers.sleep(100);
        final ElsaResponse<DateModel> model2 = dao.get(response.get().getId());

        assertThat(ElsaStatics.UTC_FORMAT.format(model2.get().getDate()), is(expected));
    }
}
