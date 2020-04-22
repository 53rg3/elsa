package tryouts;

import assets.FakerModel;
import assets.TestModel;
import client.ElsaClient;
import dao.CrudDAO;
import dao.DaoConfig;
import dao.SearchDAO;
import org.junit.Test;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;

public class GenericsCrap {
    // todo delete class
    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerDAO(new DaoConfig(TestModel.class, SearchDAO.class, FakerModel.indexConfig))
            .registerDAO(new DaoConfig(FakerModel.class, CrudDAO.class, FakerModel.indexConfig))
            .createIndexesAndEnsureMappingConsistency(false));

    @Test
    public void asdf() {
        final SearchDAO<TestModel> dao1 = elsa.getDAO(TestModel.class);
        final CrudDAO<FakerModel> dao2 = elsa.getDAO(FakerModel.class);
        final CrudDAO<TestModel> dao3 = elsa.getDAO(FakerModel.class);

        System.out.println(dao1.getClass());
        System.out.println(dao2.getClass());
        System.out.println(dao3.getClass());
    }

}
