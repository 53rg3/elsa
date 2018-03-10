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

package reindexer;

import helpers.IndexName;
import helpers.ModelClass;
import helpers.XJson;
import model.ElsaModel;
import reindexer.ReindexOptions.VersionType;

public class ReindexDestination {

    // ------------------------------------------------------------------------------------------ //
    //  FIELDS
    // ------------------------------------------------------------------------------------------ //

    private final Class<? extends ElsaModel> modelClass;
    private final XJson xJson;
    
    
    // ------------------------------------------------------------------------------------------ //
    // CONFIGURATOR
    // ------------------------------------------------------------------------------------------ //
    
    @FunctionalInterface
    public interface Configurator {

        default Config loadDefaults() {
            return new Config();
        }
    
        default void validate(Config builder) {
            builder.xJson.throwIfFieldNotExists("index", "Field 'index' in Destination must not be NULL.");
        }

        default ReindexDestination applyCustomConfig(Configurator configurator) {
            Config config = configurator.loadDefaults();
            configurator.configure(config);
            configurator.validate(config);
            return config.build();
        }

        void configure(Config builder);

    }
    
    
    // ------------------------------------------------------------------------------------------ //
    // BUILD
    // ------------------------------------------------------------------------------------------ //
    
    private ReindexDestination(Config config) {
        this.modelClass = config.modelClass;
        this.xJson = config.xJson;
    }
    
    
    // ------------------------------------------------------------------------------------------ //
    // BUILDER
    // ------------------------------------------------------------------------------------------ //
    
    public static class Config {
        private XJson xJson = new XJson();
        private Class<? extends ElsaModel> modelClass;

        /** (Mandatory) Name of the target index which shall be filled */
        public Config intoIndex(String mandatoryField) {
            this.xJson.field("index", mandatoryField);
            return this;
        }

        /** (Mandatory) Name of the target index which shall be filled */
        public Config intoIndex(final Class<? extends ElsaModel> modelClass) {
            xJson.field("index", IndexName.of(modelClass));
            this.modelClass = modelClass;
            return this;
        }

        /** (Optional) How to handle different document versions. Default is 'internal'. */
        public Config versionType(VersionType optionalSetting) {
            this.xJson.field("version_type", optionalSetting.toString());
            return this;
        }
    
        public ReindexDestination build() {
            return new ReindexDestination(this);
        }
    }

    // ------------------------------------------------------------------------------------------ //
    // GETTER & SETTER
    // ------------------------------------------------------------------------------------------ //


    public Class<? extends ElsaModel> getModelClass() {
        return this.modelClass;
    }

    public XJson getXJson() {
        return this.xJson;
    }
}
