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

package exceptions;

import assets.FakerModel;
import client.ElsaClient;
import dao.CrudDAO;
import dao.DaoConfig;
import org.apache.http.HttpHost;
import org.junit.Test;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * This was once ExceptionExtractorTest, but the tests are still ok and don't take much time.
 */
public class ElsaExceptionTest {

    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerDAO(new DaoConfig(CrudDAO.class, FakerModel.indexConfig))
            .createIndexesAndEnsureMappingConsistency(false));

    @Test
    public void extract_badRequestException_pass() throws ElsaException {
        elsa.admin.createIndex(FakerModel.indexConfig);

        try {
            elsa.admin.createIndex(FakerModel.indexConfig);
        } catch (final ElsaException e) {
            assertThat(e.getHttpStatus(), is(400));
        }

        elsa.admin.deleteIndex(FakerModel.indexConfig);
    }

    @Test
    public void extract_notFoundException_pass() {
        try {
            elsa.admin.deleteIndex("does_not_exist");
        } catch (final ElsaException e) {
            assertThat(e.getHttpStatus(), is(404));
        }
    }

    @Test
    public void extract_connectionRefusedException_pass() {
        final HttpHost[] httpHosts = {new HttpHost("localhost", 1111, "http")};
        final ElsaClient elsa = new ElsaClient(c -> c
                .setClusterNodes(httpHosts)
                .registerDAO(new DaoConfig(CrudDAO.class, FakerModel.indexConfig))
                .createIndexesAndEnsureMappingConsistency(false));

        try {
            elsa.admin.deleteIndex("cluster_is_offline");
        } catch (final ElsaException e) {
            assertThat(e.getHttpStatus(), is(500));
        }
    }

}
