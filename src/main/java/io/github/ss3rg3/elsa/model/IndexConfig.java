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

package io.github.ss3rg3.elsa.model;

import org.elasticsearch.core.TimeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IndexConfig {

    // ------------------------------------------------------------------------------------------ //
    // CONFIGURATOR
    // ------------------------------------------------------------------------------------------ //

    @FunctionalInterface
    public interface Configurator {

        default Config loadDefaults() {
            return new Config();
        }

        default void validate(final Config config) {
            evaluateIndexName(config.indexName);
            evaluateShards(config.shards);
            evaluateReplicas(config.replicas);
            Objects.requireNonNull(config.mappingClass, "mappingClass must not be NULL");
            Objects.requireNonNull(config.settings, "If you see this, then it's a bug in the library...");
        }

        default Config applyCustomConfig(final Configurator configurator) {
            final Config config = configurator.loadDefaults();
            configurator.configure(config);
            configurator.validate(config);
            return config;
        }

        void configure(Config config);
    }


    // ------------------------------------------------------------------------------------------ //
    //  FIELDS
    // ------------------------------------------------------------------------------------------ //

    private final String indexName;
    private final Integer shards;
    private final Integer replicas;
    private final TimeValue refreshInterval;
    private final Class<? extends ElsaModel> mappingClass;
    private final Map<String, Object> settings;


    // ------------------------------------------------------------------------------------------ //
    // CONSTRUCTOR
    // ------------------------------------------------------------------------------------------ //

    public IndexConfig(final Configurator configurator) {
        final Config config = configurator.applyCustomConfig(configurator);
        this.indexName = config.indexName;
        this.shards = config.shards;
        this.replicas = config.replicas;
        this.refreshInterval = config.refreshInterval;
        this.mappingClass = config.mappingClass;
        this.settings = config.settings;
    }


    // ------------------------------------------------------------------------------------------ //
    // CONFIG
    // ------------------------------------------------------------------------------------------ //

    public static class Config {
        private Config() {
        }

        private String indexName;
        private Integer shards;
        private Integer replicas;
        private TimeValue refreshInterval = TimeValue.timeValueSeconds(1);
        private Class<? extends ElsaModel> mappingClass;
        private final Map<String, Object> settings = new HashMap<>();

        public Config indexName(final String mandatorySetting) {
            this.indexName = mandatorySetting;
            return this;
        }

        public Config mappingClass(final Class<? extends ElsaModel> mandatorySetting) {
            this.mappingClass = mandatorySetting;
            return this;
        }

        public Config shards(final Integer mandatorySetting) {
            this.shards = mandatorySetting;
            return this;
        }

        public Config replicas(final Integer mandatorySetting) {
            this.replicas = mandatorySetting;
            return this;
        }

        public Config refreshInterval(final TimeValue defaultIs1s) {
            this.refreshInterval = defaultIs1s;
            return this;
        }

        /**
         * Supported types are Boolean, Integer, Double, String. Rest will throw. Elastic Java API can handle more, but
         * no idea what they used for.<br><br>
         * For setting names see here: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules.html
         */
        public Config addIndexSetting(final String settingName, final Object value) {
            Objects.requireNonNull(settingName, "settingName must not be null");
            Objects.requireNonNull(value, "value must not be null");

            this.settings.put(settingName, value);
            return this;
        }

    }

    private static void evaluateIndexName(final String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("IndexName must not be empty or NULL, got: " + value);
        }
    }

    private static void evaluateShards(final Integer value) {
        if (value <= 0) {
            throw new IllegalStateException("Shards can't be 0 or negative, got: " + value);
        }
    }

    private static void evaluateReplicas(final Integer value) {
        if (value < 0) {
            throw new IllegalStateException("Replicas can't be less than 0, got: " + value);
        }
    }

    public synchronized String getIndexName() {
        return this.indexName;
    }

    public int getShards() {
        return this.shards;
    }

    public int getReplicas() {
        return this.replicas;
    }

    public TimeValue getRefreshInterval() {
        return this.refreshInterval;
    }

    public Class<? extends ElsaModel> getMappingClass() {
        return this.mappingClass;
    }

    public Map<String, Object> getSettings() {
        return this.settings;
    }
}
