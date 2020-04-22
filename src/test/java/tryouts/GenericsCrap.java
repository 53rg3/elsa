package tryouts;

import assets.FakerModel;
import assets.TestDAO;
import assets.TestModel;
import client.ElsaClient;
import dao.CrudDAO;
import dao.ElsaDAO;
import dao.SearchDAO;
import model.ElsaModel;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;

public class GenericsCrap {
    // todo delete class
    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(TEST_CLUSTER_HOSTS)
            .registerModel(TestModel.class, SearchDAO.class)
            .registerModel(FakerModel.class, CrudDAO.class)
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
