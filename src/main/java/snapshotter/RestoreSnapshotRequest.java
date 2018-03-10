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

import com.google.common.base.Joiner;
import helpers.XJson;

import java.util.Objects;

public class RestoreSnapshotRequest {
    
    // ------------------------------------------------------------------------------------------ //
    //  FIELDS
    // ------------------------------------------------------------------------------------------ //

    private final String repositoryName;
    private final String snapshotName;
    private final XJson xJson;
    
    
    // ------------------------------------------------------------------------------------------ //
    // CONFIGURATOR
    // ------------------------------------------------------------------------------------------ //
    
    @FunctionalInterface
    public interface Config {
    
        static Builder createBuilderWithDefaults() {
            return new Builder();
        }
    
        void applyCustomConfig(Builder builder);
    
        default void validate(final Builder builder) {
            Objects.requireNonNull(builder.snapshotName, "SnapshotName must not be NULL");
            Objects.requireNonNull(builder.repositoryName, "RepositoryName must not be NULL");
            builder.xJson.throwIfFieldNotExists("indices", "Indices must not be NULL");
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
    
    public RestoreSnapshotRequest(final Config config) {
        final Builder builder = Config.createBuilder(config);
        this.snapshotName = builder.snapshotName;
        this.repositoryName = builder.repositoryName;
        this.xJson = builder.xJson;
    }
    
    
    // ------------------------------------------------------------------------------------------ //
    // BUILDER
    // ------------------------------------------------------------------------------------------ //
    
    public static class Builder {
        private Builder() {}

        private String repositoryName;
        private String snapshotName;
        private final XJson xJson = new XJson();

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
            this.xJson.field("indices", Joiner.on(",").join(mandatorySetting));
            return this;
        }

        /** Setting it to true will cause indices that do not exist to be ignored during snapshot creation. By default,
         * when ignore_unavailable option is not set and an index is missing the snapshot request will fail. */
        public Builder ignoreUnavailable(final Boolean defaultIsFalse) {
            this.xJson.field("ignore_unavailable", defaultIsFalse);
            return this;
        }

        /** The restored templates that don’t currently exist in the cluster are added and existing templates with the
         * same name are replaced by the restored templates. The restored persistent settings are added to the existing
         * persistent settings. */
        public Builder includeGlobalState(final Boolean defaultIsFalse) {
            this.xJson.field("include_global_state", defaultIsFalse);
            return this;
        }

        /** Allows to prevent aliases from being restored together with associated indices */
        public Builder includeAliases(final Boolean defaultIsTrue) {
            this.xJson.field("include_aliases", defaultIsTrue);
            return this;
        }

        /** Pattern to use for renaming the indices. See renameReplacement() for more info. */
        public Builder renamePattern(final String optionalSetting) {
            this.xJson.field("rename_pattern", optionalSetting);
            return this;
        }

        /** Renames the indices. Uses the rules explained here:
         * <a href="https://docs.oracle.com/javase/6/docs/api/java/util/regex/Matcher.html#appendReplacement(java.lang.StringBuffer,%20java.lang.String)">appendReplacement</a>*/
        public Builder renameReplacement(final String optionalSetting) {
            this.xJson.field("rename_replacement", optionalSetting);
            return this;
        }

        /** Overrides dynamic index setting. Certain settings like number of shards are not dynamic and will produce errors.
         * Allowed settings can be obtained here: <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules.html">Dynamic index settings</a> */
        public Builder overrideDynamicIndexSettings(final XJson optionalSetting) {
            this.xJson.field("index_settings", optionalSetting.toMap());
            return this;
        }
    
    }

    public String getRepositoryName() {
        return this.repositoryName;
    }

    public String getSnapshotName() {
        return this.snapshotName;
    }

    public XJson getXJson() {
        return this.xJson;
    }
}
