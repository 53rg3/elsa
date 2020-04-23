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
class DaoCreator {

    private static final Logger logger = LoggerFactory.getLogger(DaoCreator.class);

    private final ElsaClient elsa;


    DaoCreator(final ElsaClient elsaClient) {
        Objects.requireNonNull(elsaClient, "ElsaClient must not be NULL.");
        this.elsa = elsaClient;
    }

    ImmutableMap<Class<? extends ElsaModel>, ? extends ElsaDAO> createDaoMap(final Collection<DaoConfig> registeredDaos) {
        final Map<Class<? extends ElsaModel>, ElsaDAO> map = new HashMap<>();
        for (final DaoConfig daoConfig : registeredDaos) {
            this.ensureElsaIndexDataInModelIsNotNull(daoConfig.getModelClass()); // todo delete
            this.ensureGetIdAndSetIdInModelWorkProperly(daoConfig.getModelClass());
            map.put(daoConfig.getModelClass(), this.createDAO(daoConfig));
        }
        return ImmutableMap.copyOf(map);
    }

    @SuppressWarnings("unchecked")
    <T extends ElsaDAO> T createDAO(final DaoConfig daoConfig) {
        final Class<? extends ElsaModel> modelClass = daoConfig.getModelClass();
        final Class<? extends ElsaDAO> daoClass = daoConfig.getDaoClass();
        try {
            return (T) daoClass.getConstructor(DaoConfig.class, ElsaClient.class).newInstance(daoConfig, this.elsa);
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("Can't instantiate DAO. Problem with " + modelClass + " or " + daoClass, e);
            throw new IllegalStateException("Can't instantiate DAO. Problem with " + modelClass + " or " + daoClass, e);
        }
    }


    // ------------------------------------------------------------------------------------------ //
    // PRIVATE
    // ------------------------------------------------------------------------------------------ //

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
