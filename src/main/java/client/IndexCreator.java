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

import admin.IndexAdmin;
import dao.ElsaDAO;
import exceptions.ElsaException;
import model.ElsaModel;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

public class IndexCreator {
    private IndexCreator() {}

    protected static void createIndicesOrEnsureMappingConsistency(final boolean shouldProceed,
                                                                  final Map<Class<? extends ElsaModel>, Class<? extends ElsaDAO>> registeredModels,
                                                                  final IndexAdmin indexAdmin) {
        if (!shouldProceed) {
            return;
        }

        for (final Entry<Class<? extends ElsaModel>, Class<? extends ElsaDAO>> entry : registeredModels.entrySet()) {
            final Class<? extends ElsaModel> modelClass = entry.getKey();

            if (modelClass.equals(ElsaModel.class)) {
                throw new IllegalArgumentException("Registering interface ElsaModel.class as model is not allowed. Create a model which implements it.");
            }

            if (!indexAdmin.indexExists(modelClass)) {
                try {
                    indexAdmin.createIndex(modelClass);
                } catch (final ElsaException e) {
                    throw new IllegalStateException("Index creation failed", e);
                }
            } else {
                if (!indexAdmin.updateMapping(modelClass).isPresent()) {
                    throw new IllegalStateException("Index mappings update failed. Check logs for details.");
                }
            }
        }
    }

}
