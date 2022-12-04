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

package io.github.ss3rg3.elsa;

import com.google.gson.GsonBuilder;

public class ModelMapper {

    // ------------------------------------------------------------------------------------------ //
    // CONFIGURATOR
    // ------------------------------------------------------------------------------------------ //

    @FunctionalInterface
    public interface Configurator {

        default GsonBuilder loadDefaults() {
            return new GsonBuilder();
        }

        default GsonBuilder applyCustomConfig(final Configurator configurator) {
            final GsonBuilder gsonBuilder = configurator.loadDefaults();
            configurator.configure(gsonBuilder);
            return gsonBuilder;
        }

        void configure(GsonBuilder gsonBuilder);
    }


    // ------------------------------------------------------------------------------------------ //
    //  FIELDS
    // ------------------------------------------------------------------------------------------ //
    private final GsonBuilder gsonBuilder;


    // ------------------------------------------------------------------------------------------ //
    // CONSTRUCTOR
    // ------------------------------------------------------------------------------------------ //

    public ModelMapper(final Configurator configurator) {
        this.gsonBuilder = configurator.applyCustomConfig(configurator);
    }

    public ModelMapper() {
        this.gsonBuilder = new GsonBuilder();
    }

    public GsonBuilder getGsonBuilder() {
        return gsonBuilder;
    }
}
