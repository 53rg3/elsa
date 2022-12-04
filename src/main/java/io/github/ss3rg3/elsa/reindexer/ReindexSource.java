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

package io.github.ss3rg3.elsa.reindexer;

import io.github.ss3rg3.elsa.helpers.XJson;
import io.github.ss3rg3.elsa.model.IndexConfig;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.Objects;

public class ReindexSource {

    // ------------------------------------------------------------------------------------------ //
    //  FIELDS
    // ------------------------------------------------------------------------------------------ //

    private final XJson xJson;
    private final IndexConfig indexConfig;


    // ------------------------------------------------------------------------------------------ //
    // CONFIGURATOR
    // ------------------------------------------------------------------------------------------ //

    @FunctionalInterface
    public interface Configurator {

        default Config loadDefaults() {
            return new Config();
        }

        void configure(Config builder);

        default void validate(final Config builder) {
            builder.xJson.throwIfFieldNotExists("index", "Field 'index' in source must not be NULL.");
            Objects.requireNonNull(builder.indexConfig, "'indexConfig' must not be NULL.");
        }

        default ReindexSource applyCustomConfig(final Configurator configurator) {
            final Config builder = configurator.loadDefaults();
            configurator.configure(builder);
            configurator.validate(builder);
            return builder.build();
        }
    }


    // ------------------------------------------------------------------------------------------ //
    // BUILD
    // ------------------------------------------------------------------------------------------ //

    private ReindexSource(final ReindexSource.Config config) {
        this.xJson = config.xJson;
        this.indexConfig = config.indexConfig;
    }


    // ------------------------------------------------------------------------------------------ //
    // BUILDER
    // ------------------------------------------------------------------------------------------ //

    public static class Config {
        private final XJson xJson = new XJson();
        private IndexConfig indexConfig;

        /**
         * (Mandatory) Source index from which shall be reindexed
         */
        public Config fromIndex(final IndexConfig indexConfig) {
            this.xJson.field("index", indexConfig.getIndexName());
            this.indexConfig = indexConfig;
            return this;
        }

        /**
         * (Optional) Select explicitly the fields of the source index which shall be moved
         */
        public Config selectFields(final String... optionalSetting) {
            this.xJson.field("_source", optionalSetting);
            return this;
        }

        /**
         * (Optional) Query to select explicitly particular documents
         */
        public Config whereClause(final QueryBuilder optionalSetting) {
            this.xJson.query(optionalSetting);
            return this;
        }

        /**
         * (Optional) Determines the batch size of a scroll operation, i.e. amount of documents to process at once. Default is 1000.
         */
        public Config batchSize(final Integer optionalSetting) {
            this.xJson.field("size", optionalSetting);
            return this;
        }

        /**
         * (Optional) Determines the order in which documents will be reindex
         */
        public Config sortBy(final XJson optionalSetting) {
            this.xJson.field("sort", optionalSetting.toMap());
            return this;
        }

        /**
         * (Optional) Settings for reindexing from a remote Elasticsearch cluster
         */
        public Config remoteHost(final ReindexRemote.Configurator configurator) {
            this.xJson.field("remote", configurator.applyCustomConfig(configurator).toMap());
            return this;
        }

        protected ReindexSource build() {
            return new ReindexSource(this);
        }
    }

    public IndexConfig getIndexConfig() {
        return this.indexConfig;
    }

    public XJson getXJson() {
        return this.xJson;
    }
}
