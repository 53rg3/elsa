package dao;

import model.ElsaModel;
import model.IndexConfig;

public class DaoConfig {

    private final Class<? extends ElsaModel> modelClass;
    private final Class<? extends ElsaDAO> daoClass;
    private final IndexConfig indexConfig;

    public DaoConfig(final Class<? extends ElsaModel> modelClass,
                     final Class<? extends ElsaDAO> daoClass,
                     final IndexConfig indexConfig) {
        this.modelClass = modelClass;
        this.daoClass = daoClass;
        this.indexConfig = indexConfig;
    }

    public Class<? extends ElsaModel> getModelClass() {
        return this.modelClass;
    }

    public Class<? extends ElsaDAO> getDaoClass() {
        return this.daoClass;
    }

    public IndexConfig getIndexConfig() {
        return this.indexConfig;
    }
}
