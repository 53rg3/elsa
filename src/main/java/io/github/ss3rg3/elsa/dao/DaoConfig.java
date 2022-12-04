package io.github.ss3rg3.elsa.dao;

import com.google.gson.Gson;
import io.github.ss3rg3.elsa.model.ElsaModel;
import io.github.ss3rg3.elsa.model.IndexConfig;

import java.util.Objects;

public class DaoConfig {

    private final Class<? extends ElsaModel> modelClass;
    private final Class<? extends ElsaDAO> daoClass;
    private final IndexConfig indexConfig;
    private final Gson gson;

    public DaoConfig(final Class<? extends ElsaDAO> daoClass,
                     final IndexConfig indexConfig) {
        Objects.requireNonNull(daoClass, "daoClass must not be null");
        Objects.requireNonNull(indexConfig, "indexConfig must not be null");
        this.modelClass = indexConfig.getMappingClass();
        this.daoClass = daoClass;
        this.indexConfig = indexConfig;
        this.gson = null;
    }

    public DaoConfig(final Class<? extends ElsaDAO> daoClass,
                     final IndexConfig indexConfig,
                     final Gson gson) {
        Objects.requireNonNull(daoClass, "daoClass must not be null");
        Objects.requireNonNull(indexConfig, "indexConfig must not be null");
        this.modelClass = indexConfig.getMappingClass();
        this.daoClass = daoClass;
        this.indexConfig = indexConfig;
        this.gson = gson;
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

    public Gson getGson() {
        return this.gson;
    }
}
