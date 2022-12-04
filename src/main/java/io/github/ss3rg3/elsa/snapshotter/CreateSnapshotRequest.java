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

package io.github.ss3rg3.elsa.snapshotter;

import com.google.common.base.Joiner;

import java.util.Objects;

public class CreateSnapshotRequest {

    // ------------------------------------------------------------------------------------------ //
    //  FIELDS
    // ------------------------------------------------------------------------------------------ //

    private final String repositoryName;
    private final String snapshotName;
    private final String indices;
    private final Boolean ignore_unavailable;
    private final Boolean include_global_state;
    private final Boolean partial;


    // ------------------------------------------------------------------------------------------ //
    // CONFIGURATOR
    // ------------------------------------------------------------------------------------------ //

    @FunctionalInterface
    public interface Config {

        static Builder createBuilderWithDefaults() {
            return new Builder()
                    .ignoreUnavailable(true)
                    .includeGlobalState(false);
        }

        void applyCustomConfig(Builder builder);

        default void validate(final Builder builder) {
            Objects.requireNonNull(builder.snapshotName, "SnapshotName must not be NULL");
            Objects.requireNonNull(builder.repositoryName, "RepositoryName must not be NULL");
            Objects.requireNonNull(builder.indices, "Indices must not be NULL");
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

    public CreateSnapshotRequest(final Config config) {
        final Builder builder = Config.createBuilder(config);
        this.snapshotName = builder.snapshotName;
        this.repositoryName = builder.repositoryName;
        this.indices = builder.indices;
        this.ignore_unavailable = builder.ignore_unavailable;
        this.include_global_state = builder.include_global_state;
        this.partial = builder.partial;
    }


    // ------------------------------------------------------------------------------------------ //
    // BUILDER
    // ------------------------------------------------------------------------------------------ //

    public static class Builder {
        private Builder() {}

        private String repositoryName;
        private String snapshotName;
        private String indices;
        private Boolean partial;
        private Boolean ignore_unavailable;
        private Boolean include_global_state;

        public Builder repositoryName(final String mandatorySetting) {
            this.repositoryName = mandatorySetting;
            return this;
        }

        public Builder snapshotName(final String mandatorySetting) {
            this.snapshotName = mandatorySetting;
            return this;
        }

        /** Comma separated list. Allows wildcards, see
         * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/multi-index.html">Multiple Indices Docs</a> */
        public Builder indices(final String... mandatorySetting) {
            this.indices = Joiner.on(",").join(mandatorySetting);
            return this;
        }

        public Builder ignoreUnavailable(final Boolean defaultIsTrue) {
            this.ignore_unavailable = defaultIsTrue;
            return this;
        }

        public Builder includeGlobalState(final Boolean defaultIsFalse) {
            this.include_global_state = defaultIsFalse;
            return this;
        }

        public Builder partial(final Boolean defaultIsFalse) {
            this.partial = defaultIsFalse;
            return this;
        }

    }

    public String getRepositoryName() {
        return this.repositoryName;
    }

    public String getSnapshotName() {
        return this.snapshotName;
    }
}
