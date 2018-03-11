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

import client.ElsaClient;
import responses.ConfirmationResponse;
import responses.ElsaResponse;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class RepositoryBucket {

    // ------------------------------------------------------------------------------------------ //
    //  FIELDS
    // ------------------------------------------------------------------------------------------ //

    private final Map<String, SnapshotRepository> repositoryBucket;


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

    public RepositoryBucket(final Config config) {
        final Builder builder;
        if (config == null) {
            builder = Config.createBuilderWithDefaults();
        } else {
            builder = Config.createBuilder(config);
        }
        this.repositoryBucket = builder.bucket;
    }


    // ------------------------------------------------------------------------------------------ //
    // BUILDER
    // ------------------------------------------------------------------------------------------ //

    public static class Builder {
        private Builder() {
        }

        private final Map<String, SnapshotRepository> bucket = new LinkedHashMap<>();

        public Builder add(final SnapshotRepository snapshotRepository) {
            this.putIfAbsent(snapshotRepository);
            return this;
        }

        private void putIfAbsent(final SnapshotRepository snapshotRepository) {
            if (this.bucket.putIfAbsent(snapshotRepository.getRepositoryName(), snapshotRepository) != null) {
                throw new IllegalArgumentException("Repository '" + snapshotRepository.getRepositoryName() + "' is already registered.");
            }
        }

    }


    // ------------------------------------------------------------------------------------------ //
    // PUBLIC METHODS
    // ------------------------------------------------------------------------------------------ //

    public void registerRepositories(final ElsaClient elsa) {
        this.getRepositoryBucket().forEach((key, value) -> {
            final ElsaResponse<ConfirmationResponse> response = elsa.snapshotter.createRepository(
                    new CreateRepositoryRequest(c -> c
                            .repositoryName(value.getRepositoryName())
                            .pathToLocation(value.getPathToLocation())));
            Objects.requireNonNull(response.get(), "" +
                    "Couldn't create repository provided in your ElsaClient implementation. " +
                    "Check your elasticsearch.yml config if these are configured or check logs for more info.");
        });
    }


    // ------------------------------------------------------------------------------------------ //
    // GETTER
    // ------------------------------------------------------------------------------------------ //

    public Map<String, SnapshotRepository> getRepositoryBucket() {
        return this.repositoryBucket;
    }
}
