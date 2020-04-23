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

package client;

import com.google.common.collect.ImmutableMap;
import dao.DaoConfig;
import dao.ElsaDAO;
import model.ElsaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Creates a map to retrieve DAO classes for registered models.
 */
public class DaoMapCreator {

    private static final Logger logger = LoggerFactory.getLogger(DaoMapCreator.class);

    // ------------------------------------------------------------------------------------------ //
    //  FIELDS
    // ------------------------------------------------------------------------------------------ //

    private final ElsaClient elsa;
    private final Collection<DaoConfig> daoConfigs;

    // ------------------------------------------------------------------------------------------ //
    // CONFIGURATOR
    // ------------------------------------------------------------------------------------------ //

    @FunctionalInterface
    protected interface Config {

        static Builder createBuilderWithDefaults() {
            return new Builder();
        }

        void applyCustomConfig(Builder builder);

        default void validate(final Builder builder) {
            Objects.requireNonNull(builder.elsa, "ElsaClient must not be NULL.");
            Objects.requireNonNull(builder.registeredModels, "RegisteredModels must not be NULL.");
        }

        static Builder createBuilder(final Config config) {
            final Builder builder = createBuilderWithDefaults();
            config.applyCustomConfig(builder);
            config.validate(builder);
            return builder;
        }
    }


    // ------------------------------------------------------------------------------------------ //
    // BUILD
    // ------------------------------------------------------------------------------------------ //

    DaoMapCreator(final Config config) {
        final Builder builder = Config.createBuilder(config);
        this.elsa = builder.elsa;
        this.daoConfigs = builder.registeredModels;
    }


    // ------------------------------------------------------------------------------------------ //
    // BUILDER
    // ------------------------------------------------------------------------------------------ //

    public static class Builder {
        private Builder() {
        }

        private ElsaClient elsa;
        private Collection<DaoConfig> registeredModels;

        public Builder elsa(final ElsaClient mandatorySetting) {
            this.elsa = mandatorySetting;
            return this;
        }

        public Builder registeredModels(final Collection<DaoConfig> mandatorySetting) {
            this.registeredModels = mandatorySetting;
            return this;
        }

    }

    // ------------------------------------------------------------------------------------------ //
    // METHODS
    // ------------------------------------------------------------------------------------------ //

    public ImmutableMap<Class<? extends ElsaModel>, ? extends ElsaDAO> create() {
        final Map<Class<? extends ElsaModel>, ElsaDAO> map = new HashMap<>();
        for (final DaoConfig daoConfig : this.daoConfigs) {

            final Class<? extends ElsaModel> modelClass = daoConfig.getModelClass();
            final Class<? extends ElsaDAO> daoClass = daoConfig.getDaoClass();

            this.ensureElsaIndexDataInModelIsNotNull(modelClass); // todo delete
            this.ensureGetIdAndSetIdInModelWorkProperly(modelClass);

            try {
                map.put(modelClass, daoClass.getConstructor(DaoConfig.class, ElsaClient.class).newInstance(daoConfig, this.elsa));
            } catch (final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                logger.error("Can't instantiate DAOMap for ElsaClient. Problem with " + modelClass + " or " + daoClass, e);
                throw new IllegalStateException("Can't instantiate DAOMap for ElsaClient. Problem with " + modelClass + " or " + daoClass, e);
            }
        }
        return ImmutableMap.copyOf(map);
    }

    private void ensureElsaIndexDataInModelIsNotNull(final Class<? extends ElsaModel> modelClass) {
        try {
            if (modelClass.newInstance().getIndexConfig() == null) {
                throw new IllegalStateException("IndexConfig was not instantiated in model: " + modelClass);
            }
        } catch (final InstantiationException | IllegalAccessException e) {
            logger.error("Can't instantiate ElsaModel for ElsaClient. Caused by " + modelClass);
        }
    }

    private void ensureGetIdAndSetIdInModelWorkProperly(final Class<? extends ElsaModel> modelClass) {
        try {
            final ElsaModel model = modelClass.newInstance();
            final String id = "qwer1234";
            model.setId(id);
            if (!model.getId().equals(id)) {
                throw new IllegalStateException("Methods getId() or setId() was not implemented properly in model: " + modelClass);
            }
        } catch (final InstantiationException | IllegalAccessException e) {
            logger.error("Can't instantiate ElsaModel for ElsaClient. Problem with " + modelClass);
        }
    }
}
