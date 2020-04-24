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

import helpers.XJson;
import model.IndexConfig;
import reindexer.ReindexOptions.VersionType;

import java.util.Objects;

public class ReindexDestination {

    // ------------------------------------------------------------------------------------------ //
    //  FIELDS
    // ------------------------------------------------------------------------------------------ //

    private final IndexConfig indexConfig;
    private final XJson xJson;


    // ------------------------------------------------------------------------------------------ //
    // CONFIGURATOR
    // ------------------------------------------------------------------------------------------ //

    @FunctionalInterface
    public interface Configurator {

        default Config loadDefaults() {
            return new Config();
        }

        default void validate(final Config builder) {
            builder.xJson.throwIfFieldNotExists("index", "Field 'index' in Destination must not be NULL.");
            Objects.requireNonNull(builder.indexConfig, "'indexConfig' must not be NULL.");
        }

        default ReindexDestination applyCustomConfig(final Configurator configurator) {
            final Config config = configurator.loadDefaults();
            configurator.configure(config);
            configurator.validate(config);
            return config.build();
        }

        void configure(Config builder);

    }


    // ------------------------------------------------------------------------------------------ //
    // BUILD
    // ------------------------------------------------------------------------------------------ //

    private ReindexDestination(final Config config) {
        this.indexConfig = config.indexConfig;
        this.xJson = config.xJson;
    }


    // ------------------------------------------------------------------------------------------ //
    // BUILDER
    // ------------------------------------------------------------------------------------------ //

    public static class Config {
        private final XJson xJson = new XJson();
        private IndexConfig indexConfig;

        /**
         * (Mandatory) Name of the target index which shall be filled
         */
        public Config intoIndex(final IndexConfig indexConfig) {
            this.xJson.field("index", indexConfig.getIndexName());
            this.indexConfig = indexConfig;
            return this;
        }

        /**
         * (Optional) How to handle different document versions. Default is 'internal'.
         */
        public Config versionType(final VersionType optionalSetting) {
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


    public IndexConfig getIndexConfig() {
        return this.indexConfig;
    }

    public XJson getXJson() {
        return this.xJson;
    }
}
