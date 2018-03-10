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

package responses;

import assets.FakerModel;
import client.ElsaClient;
import dao.CrudDAO;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ExceptionExtractorTest {

    private static final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(httpHosts)
            .registerModel(FakerModel.class, CrudDAO.class)
            .createIndexesAndEnsureMappingConsistency(false));

    @Test
    public void extract_badRequestException_pass() {
        elsa.admin.createIndex(FakerModel.class);
        ElsaResponse<CreateIndexResponse> response = elsa.admin.createIndex(FakerModel.class);

        assertThat(response.isPresent(), is(false));
        assertThat(response.hasException(), is(true));
        assertThat(response.getExceptionResponse().getStatus(), is(400));

        elsa.admin.deleteIndex(FakerModel.class);
    }

    @Test
    public void extract_notFoundException_pass() {
        ElsaResponse<DeleteIndexResponse> response = elsa.admin.deleteIndex("does_not_exist");

        assertThat(response.isPresent(), is(false));
        assertThat(response.hasException(), is(true));
        assertThat(response.getExceptionResponse().getStatus(), is(404));
    }

    @Test
    public void extract_connectionRefusedException_pass() {
        HttpHost[] httpHosts = {new HttpHost("localhost", 1111, "http")};
        ElsaClient elsa = new ElsaClient(c -> c
                .setClusterNodes(httpHosts)
                .registerModel(FakerModel.class, CrudDAO.class)
                .createIndexesAndEnsureMappingConsistency(false));

        ElsaResponse<DeleteIndexResponse> response = elsa.admin.deleteIndex("cluster_is_offline");
        assertThat(response.isPresent(), is(false));
        assertThat(response.hasException(), is(true));
        assertThat(response.getExceptionResponse().getStatus(), is(503));
    }

}
