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

package helpers;

import statics.Messages.ExceptionMsg;
import model.ElsaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelClass {
    private ModelClass() {}

    private static final Logger logger = LoggerFactory.getLogger(ModelClass.class);
    // todo urm? do we need that here? Only used for a test??
    public static <T extends ElsaModel> T createEmpty(Class<T> modelClass) {
        try {
            return modelClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(ExceptionMsg.CLASS_INSTANTIATION_FAILED, e);
        }
        throw new IllegalStateException(ExceptionMsg.CLASS_INSTANTIATION_FAILED);
    }

}
