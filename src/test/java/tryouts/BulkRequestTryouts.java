package tryouts;

import assets.FakerModel;
import client.ElsaClient;
import dao.CrudDAO;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BulkRequestTryouts {

    private final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
    private final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(this.httpHosts)
            .registerModel(FakerModel.class, CrudDAO.class)
            .stifleThreadUntilClusterIsOnline(true));
    private final CrudDAO<FakerModel> dao = this.elsa.getDAO(FakerModel.class);
    private final String id1 = "fakerModel1_id";
    private final String id2 = "fakerModel2_id";
    private final String id3 = "fakerModel3_id";
    private final FakerModel fakerModel1 = this.createFakerModel(id1, "Alice1");
    private final FakerModel fakerModel2 = this.createFakerModel(id2, "Bob1");
    private final FakerModel fakerModel3 = this.createFakerModel(id3, "Chris1");

//    @Test
    public void executeDifferentBulkRequests() throws Exception {

        // INDEX
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(this.dao.buildIndexRequest(this.fakerModel1));
        bulkRequest.add(this.dao.buildIndexRequest(this.fakerModel2));
        bulkRequest.add(this.dao.buildIndexRequest(this.fakerModel3));
        BulkResponse response = this.elsa.client.bulk(bulkRequest, RequestOptions.DEFAULT);
        assertThat(response.hasFailures(), is(false));
        assertThat(dao.get(id1).get().getName(), is("Alice1"));
        assertThat(dao.get(id2).get().getName(), is("Bob1"));
        assertThat(dao.get(id3).get().getName(), is("Chris1"));

        // UPDATE
        fakerModel1.setName("Alice2");
        fakerModel2.setName("Bob2");
        fakerModel3.setName("Chris2");
        bulkRequest = new BulkRequest();
        bulkRequest.add(this.dao.buildUpdateRequest(this.fakerModel1));
        bulkRequest.add(this.dao.buildUpdateRequest(this.fakerModel2));
        bulkRequest.add(this.dao.buildUpdateRequest(this.fakerModel3));
        response = this.elsa.client.bulk(bulkRequest, RequestOptions.DEFAULT);
        assertThat(response.hasFailures(), is(false));
        assertThat(dao.get(id1).get().getName(), is("Alice2"));
        assertThat(dao.get(id2).get().getName(), is("Bob2"));
        assertThat(dao.get(id3).get().getName(), is("Chris2"));

        // DELETE
        bulkRequest = new BulkRequest();
        bulkRequest.add(this.dao.buildDeleteRequest(this.fakerModel1));
        bulkRequest.add(this.dao.buildDeleteRequest(this.fakerModel2));
        bulkRequest.add(this.dao.buildDeleteRequest(this.fakerModel3));
        response = this.elsa.client.bulk(bulkRequest, RequestOptions.DEFAULT);
        assertThat(response.hasFailures(), is(false));
        assertThat(dao.get(id1).hasResult(), is(false));
        assertThat(dao.get(id2).hasResult(), is(false));
        assertThat(dao.get(id3).hasResult(), is(false));
    }

    private FakerModel createFakerModel(String id, String name) {
        FakerModel fakerModel = FakerModel.createModelWithRandomData();
        fakerModel.setId(id);
        fakerModel.setName(name);
        return fakerModel;
    }
}
