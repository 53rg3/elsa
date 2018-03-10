//package tryouts;
//
//import assets.FakerModel;
//import client.ElsaClient;
//import com.google.common.base.Stopwatch;
//import dao.CrudDAO;
//import helpers.ModelClass;
//import org.apache.http.HttpHost;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.common.unit.TimeValue;
//import org.junit.Test;
//import responses.ElsaResponse;
//
//import java.util.Optional;
//import java.util.concurrent.TimeUnit;
//
//import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
//import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
//import static org.elasticsearch.search.builder.SearchSourceBuilder.searchSource;
//
//public class CreateTestIndex {
//    private final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
//    private final ElsaClient elsa = new ElsaClient(c -> c
//            .setClusterNodes(httpHosts)
//            .registerModel(FakerModel.class, CrudDAO.class)
//            .configureBulkProcessor(config -> config
//                    .setFlushInterval(TimeValue.timeValueSeconds(10))
//                    .setConcurrentRequests(8))
//            .stifleThreadUntilClusterIsOnline(true));
//    private final CrudDAO<FakerModel> crudDAO = elsa.getDAO(FakerModel.class);
//
//    @Test
//    public void run() {
//        final Stopwatch stopwatch = Stopwatch.createStarted();
//        final int bulkSize = 100;
//        for (int i = 0; i < bulkSize; i++) {
//            this.elsa.bulkProcessor.add(crudDAO.buildIndexRequest(FakerModel.createModelWithRandomData()));
//            if (i % 100_000 == 0) {
//                System.out.println(i + ". - Round: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
//                stopwatch.reset();
//                stopwatch.start();
//            }
//        }
//        this.elsa.bulkProcessor.flush();
//        System.out.println("Total elapsed time: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
//
//    }
//
//}
