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
import io.github.ss3rg3.elsa.reindexer.ReindexOptions.ScriptingLanguage;

public class ReindexScript {

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

        default void validate(Config builder) {
            builder.xJson.throwIfFieldNotExists("source", "Field 'source' in Script must not be NULL.");
            builder.xJson.throwIfFieldNotExists("lang", "Field 'lang' in Script must not be NULL.");
        }

        default XJson applyCustomConfig(Configurator configurator) {
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

    private ReindexScript() {
        // NO OP
    }


    // ------------------------------------------------------------------------------------------ //
    // BUILDER
    // ------------------------------------------------------------------------------------------ //

    public static class Config {
        private XJson xJson = new XJson();

        /** The script which shall be applied */
        public Config script(String mandatoryField) {
            this.xJson.field("source", mandatoryField);
            return this;
        }

        /** The scripting language of the script */
        public Config language(ScriptingLanguage mandatoryField) {
            this.xJson.field("lang", mandatoryField);
            return this;
        }

        public XJson build() {
            return this.xJson;
        }
    }

}
