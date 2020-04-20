package tryouts;

import assets.TestDAO;
import assets.TestModel;
import client.ElsaClient;
import exceptions.ElsaElasticsearchException;
import exceptions.ElsaException;
import exceptions.ElsaIOException;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.junit.Test;

import java.io.IOException;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;

public class ExceptionCrap {

    private final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerModel(TestModel.class, TestDAO.class)
            .createIndexesAndEnsureMappingConsistency(false));


    @Test
    public void asdfyxcv() {

//        try {
//            this.elsa.admin.createIndex(TestModel.class);
//        } catch (final IOException e) {
//            System.out.println();
//            e.printStackTrace();
//        }

//        try {
//            final GetResponse response = this.elsa.client.get(new GetRequest("elsa_test_inex", "_id", "yxcv"), RequestOptions.DEFAULT);
//            System.out.println(response.isExists());
//            System.out.println("Source:\n"+response.getSourceAsString());
//        } catch (final ResponseException e) {
//            System.out.println("ResponseException!");
//            e.printStackTrace();
//        } catch (final IOException e) {
//            System.out.println("IOException!!");
//            e.printStackTrace();
//        } catch (final ElasticsearchException e) {
//            ExceptionResponse
//            System.out.println("ElasticsearchException!");
//            System.out.println(e.getResourceType());
//            e.printStackTrace();
//        }

        try {
            this.getById("index", "some_id");
        } catch (final ElsaException e) {
            switch (ElsaException.instanceOf(e)) {
                case ELASTICSEARCH_EXCEPTION:
                    System.out.println("ElasticsearchException: "+e.getRestStatus().getStatus() +", "+e.getMessage());
                    break;
                case IO_EXCEPTION:
                    System.out.println("IOException: "+e.getRestStatus().getStatus() +", "+e.getMessage());
                    break;
                default:
                    System.out.println("UNKNOWN: "+e.getRestStatus().getStatus() +", "+e.getMessage());
            }
        }

    }

    private GetResponse getById(final String index, final String id) throws ElsaException {
        try {
            return this.elsa.client.get(new GetRequest(index, "_id", id), RequestOptions.DEFAULT);
        } catch (final IOException e) {
            throw new ElsaIOException(e);
        } catch (final ElasticsearchException e) {
            throw new ElsaElasticsearchException(e);
        }
    }


    @Test
    public void asdf() {

        final IllegalArgumentException asdf = new IllegalArgumentException("");
        switch (asEnum(asdf)) {
            case ILLEGALSTATE:
                System.out.println("illegal state");
                break;
            default:
                System.out.println("unknown");
        }

    }

    enum ExcepEnum {
        ILLEGALSTATE,
        UNKNOWN;
    }

    private static ExcepEnum asEnum(final Exception e) {
        if(e instanceof IllegalStateException) {
            return ExcepEnum.ILLEGALSTATE;
        } else {
            return ExcepEnum.UNKNOWN;
        }
    }

}
