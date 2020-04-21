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

package snapshotter;

import java.util.Objects;

public class CreateRepositoryRequest {

    // ------------------------------------------------------------------------------------------ //
    //  FIELDS
    // ------------------------------------------------------------------------------------------ //

    private final String repositoryName;
    private final String type;
    private final Settings settings;


    // ------------------------------------------------------------------------------------------ //
    // CONFIGURATOR
    // ------------------------------------------------------------------------------------------ //

    @FunctionalInterface
    public interface Config {

        static Builder createBuilderWithDefaults() {
            return new Builder()
                    .type("fs")
                    .compress(true);
        }

        void applyCustomConfig(Builder builder);

        default void validate(final Builder builder) {
            Objects.requireNonNull(builder.location, "pathToLocation must not be NULL");
            Objects.requireNonNull(builder.repositoryName, "repository must not be NULL");
        }

        static Builder createBuilder(final Config config) {
            final Builder builder = createBuilderWithDefaults();
            config.applyCustomConfig(builder);
            config.validate(builder);
            return builder;
        }
    }


    // ------------------------------------------------------------------------------------------ //
    // BUILD
    // ------------------------------------------------------------------------------------------ //

    public CreateRepositoryRequest(final Config config) {
        final Builder builder = Config.createBuilder(config);
        this.repositoryName = builder.repositoryName;
        this.type = builder.type;
        this.settings = new Settings(builder.location, builder.compress);
    }


    // ------------------------------------------------------------------------------------------ //
    // BUILDER
    // ------------------------------------------------------------------------------------------ //

    public static class Builder {
        private Builder() {
        }

        private String repositoryName;
        private String type;
        private Boolean compress;
        private String location;

        public Builder repositoryName(final String mandatorySetting) {
            this.repositoryName = mandatorySetting;
            return this;
        }

        public Builder type(final String defaultIsFS) {
            this.type = defaultIsFS;
            return this;
        }

        public Builder compress(final Boolean defaultIsTrue) {
            this.compress = defaultIsTrue;
            return this;
        }

        public Builder pathToLocation(final String mandatorySetting) {
            this.location = mandatorySetting;
            return this;
        }

    }

    // ------------------------------------------------------------------------------------------ //
    // INNER CLASSES
    // ------------------------------------------------------------------------------------------ //

    private static class Settings {
        private final Boolean compress;
        private final String location;

        public Settings(final String location, final Boolean compress) {
            this.compress = compress;
            this.location = location;
        }
    }


    // ------------------------------------------------------------------------------------------ //
    // GETTERS
    // ------------------------------------------------------------------------------------------ //

    public String getRepositoryName() {
        return this.repositoryName;
    }
}
