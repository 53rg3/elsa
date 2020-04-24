package tryouts;

import assets.FakerModel;
import assets.TestModel;
import client.ElsaClient;
import dao.CrudDAO;
import dao.DaoConfig;
import dao.ElsaDAO;
import dao.SearchDAO;
import model.ElsaModel;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static assets.TestHelpers.TEST_CLUSTER_HOSTS;

public class GenericsCrap {
    // todo delete class
//    private static final ElsaClient elsa = new ElsaClient(c -> c
//            .setClusterNodes(TEST_CLUSTER_HOSTS)
//            .registerDAO(new DaoConfig(SearchDAO.class, FakerModel.indexConfig))
//            .registerDAO(new DaoConfig(CrudDAO.class, FakerModel.indexConfig))
//            .createIndexesAndEnsureMappingConsistency(false));
    Map<Class<? extends ElsaModel>, ElsaDAO> daoMap = new HashMap<>();

    @Test
    public void asdf() {
//        final SearchDAO<TestModel> dao1 = elsa.getDAO(TestModel.class);
//        final CrudDAO<FakerModel> dao2 = elsa.getDAO(FakerModel.class);
//        final CrudDAO<TestModel> dao3 = elsa.getDAO(FakerModel.class);
//        final SearchDAO<TestModel> appleDAO = new SearchDAO<>(TestModel.class, elsa);
//        this.daoMap.put(TestModel.class, appleDAO);
//        final CrudDAO<FakerModel> orangeDAO = new CrudDAO<>(FakerModel.class, elsa);
//        this.daoMap.put(FakerModel.class, appleDAO);
//
////        final SearchDAO<FakerModel> testModelDAO = getDAO(FakerModel.class);
//
//        System.out.println(dao1.getClass());
//        System.out.println(dao2.getClass());
//        System.out.println(dao3.getClass());
    }

    @SuppressWarnings("unchecked")
    public <T extends ElsaDAO> T getDAO(final Class<? extends ElsaModel> modelClass) {
        final T dao = (T) this.daoMap.get(modelClass);
        if (!dao.getModelClass().isAssignableFrom(modelClass)) {
            throw new IllegalStateException("Apples are not Oranges!");
        }
        return dao;
    }

}
