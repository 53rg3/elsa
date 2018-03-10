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

import dao.ElsaDAO;
import model.ElsaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;

public class IndexNamePrepender {

    private static final Logger logger = LoggerFactory.getLogger(IndexNamePrepender.class);

    protected static void setPrefixes(final String indexNamePrefix, final Map<Class<? extends ElsaModel>, Class<? extends ElsaDAO>> registeredModels) {

        if (indexNamePrefix == null || indexNamePrefix.equals("")) {
            return;
        }

        for (final Entry<Class<? extends ElsaModel>, Class<? extends ElsaDAO>> entry : registeredModels.entrySet()) {
            try {
                final ElsaModel model = entry.getKey().newInstance();
                if (model.getIndexConfig().isDynamicIndexNamingAllowed()) {
                    final String newIndexName = indexNamePrefix + model.getIndexConfig().getIndexName();
                    model.getIndexConfig().setIndexName(newIndexName);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("Can't instantiate ElsaModel for ElsaClient. Problem with " + entry.getKey());
            }
        }
    }

}
