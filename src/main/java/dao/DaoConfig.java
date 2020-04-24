package dao;

import model.ElsaModel;
import model.IndexConfig;

import java.util.Objects;

public class DaoConfig {

    private final Class<? extends ElsaModel> modelClass;
    private final Class<? extends ElsaDAO> daoClass;
    private final IndexConfig indexConfig;

    public DaoConfig(final Class<? extends ElsaDAO> daoClass,
                     final IndexConfig indexConfig) {
        Objects.requireNonNull(daoClass, "daoClass must not be null");
        Objects.requireNonNull(indexConfig, "indexConfig must not be null");
        this.modelClass = indexConfig.getMappingClass();
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
