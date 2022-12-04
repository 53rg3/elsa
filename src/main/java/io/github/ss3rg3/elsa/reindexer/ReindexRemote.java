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
import org.elasticsearch.core.TimeValue;

import java.util.Objects;

public class ReindexRemote {

    // ------------------------------------------------------------------------------------------ //
    //  FIELDS
    // ------------------------------------------------------------------------------------------ //

    // None, build returns XJson. Class is just a wrapper.


    // ------------------------------------------------------------------------------------------ //
    // CONFIGURATOR
    // ------------------------------------------------------------------------------------------ //

    @FunctionalInterface
    public interface Configurator {

        default Config loadDefaults() {
            return new Config();
        }

        void configure(Config builder);

        default void validate(Config builder) {
            Objects.requireNonNull(builder.url, "Settings in RemoteHost for 'url' must not be NULL.");
            Objects.requireNonNull(builder.port, "Settings in RemoteHost for 'port' must not be NULL.");
        }

        default XJson applyCustomConfig(Configurator configurator) {
            Config config = loadDefaults();
            configurator.configure(config);
            configurator.validate(config);
            return config.build();
        }
    }


    // ------------------------------------------------------------------------------------------ //
    // BUILD
    // ------------------------------------------------------------------------------------------ //

    private ReindexRemote() {
        // NO OP
    }


    // ------------------------------------------------------------------------------------------ //
    // BUILDER
    // ------------------------------------------------------------------------------------------ //

    public static class Config {
        private XJson xJson = new XJson();
        private String url;
        private Integer port;

        public Config url(String mandatoryField) {
            this.url = mandatoryField;
            return this;
        }

        public Config port(Integer mandatoryField) {
            this.port = mandatoryField;
            return this;
        }

        public Config userName(String optionalField) {
            this.xJson.field("username", optionalField);
            return this;
        }

        public Config password(String optionalField) {
            this.xJson.field("password", optionalField);
            return this;
        }

        public Config socketTimeout(TimeValue optionalField) {
            this.xJson.field("socket_timeout", optionalField.toString());
            return this;
        }

        public Config connectTimeout(TimeValue optionalField) {
            this.xJson.field("connect_timeout", optionalField.toString());
            return this;
        }

        public XJson build() {
            this.xJson.field("host", url+":"+port);
            return this.xJson;
        }
    }

}
