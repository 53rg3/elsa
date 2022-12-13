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

import io.github.ss3rg3.elsa.admin.IndexAdmin;
import io.github.ss3rg3.elsa.bulkprocessor.BulkProcessorConfigurator;
import io.github.ss3rg3.elsa.bulkprocessor.DefaultBulkResponseListener;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import io.github.ss3rg3.elsa.dao.DaoConfig;
import io.github.ss3rg3.elsa.dao.ElsaDAO;
import io.github.ss3rg3.elsa.exceptions.ElsaException;
import io.github.ss3rg3.elsa.model.ElsaModel;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import io.github.ss3rg3.elsa.reindexer.Reindexer;
import io.github.ss3rg3.elsa.scroller.Scroller;
import io.github.ss3rg3.elsa.snapshotter.RepositoryBucket;
import io.github.ss3rg3.elsa.snapshotter.Snapshotter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ElsaClient {

    // ------------------------------------------------------------------------------------------ //
    // FIELDS
    // ------------------------------------------------------------------------------------------ //

    public final RestHighLevelClient client;
    public final IndexAdmin admin;
    public final BulkProcessor bulkProcessor;
    public final Scroller scroller;
    public final Reindexer reindexer;
    public final Snapshotter snapshotter;
    public final Gson gson;
    private final DaoCreator daoCreator;
    private final ImmutableMap<Class<? extends ElsaModel>, ? extends ElsaDAO> daoMap;


    // ------------------------------------------------------------------------------------------ //
    // DEFAULTS & VALIDATE
    // ------------------------------------------------------------------------------------------ //

    @FunctionalInterface
    public interface Configurator {

        static Config loadDefaults() {
            final Config config = new Config();
            config.bulkResponseListener = new DefaultBulkResponseListener();
            config.requestOptionsForBulkProcessor = RequestOptions.DEFAULT;
            config.createIndexesAndEnsureMappingConsistency = true;
            config.modelMapper = new ModelMapper();
            return config;
        }

        default void validate(final Config config) {
            if (config.httpHosts == null || config.httpHosts.length == 0) {
                throw new IllegalArgumentException("httpHosts must not be null or empty");
            }
        }

        static Config create(final Configurator configurator) {
            final Config config = Configurator.loadDefaults();
            configurator.applyCustomConfig(config);
            configurator.validate(config);
            return config;
        }

        void applyCustomConfig(Config config);
    }


    // ------------------------------------------------------------------------------------------ //
    // BUILD
    // ------------------------------------------------------------------------------------------ //

    public ElsaClient(final Configurator configurator) {

        // FIELDS
        final Config config = Configurator.create(configurator);
        this.client = RestClientConfig.create(config.httpHosts, config.restClientConfig);
        this.gson = config.modelMapper.getGsonBuilder().create();
        final RepositoryBucket repositoryBucket = new RepositoryBucket(config.repositoryBucketConfig);

        // COMPONENTS
        this.admin = new IndexAdmin(this);
        this.bulkProcessor = this.createAnotherBulkProcessor(
                this.client,
                config.bulkResponseListener,
                config.requestOptionsForBulkProcessor,
                config.bulkProcessorConfigurator);
        this.scroller = new Scroller(this);
        this.reindexer = new Reindexer(this);
        this.snapshotter = new Snapshotter(this, repositoryBucket);

        // DAOs
        this.daoCreator = new DaoCreator(this);
        this.daoMap = this.daoCreator.createDaoMap(config.daoConfigMap.values());

        // METHODS
        try {
            IndexCreator.createIndicesOrEnsureMappingConsistency(
                    config.createIndexesAndEnsureMappingConsistency,
                    config.daoConfigMap.values(),
                    this.admin);
        } catch (final ElsaException e) {
            throw new IllegalStateException("Couldn't create indices or update mapping.", e);
        }

        try {
            repositoryBucket.registerRepositories(this);
        } catch (final ElsaException e) {
            throw new IllegalStateException("Couldn't register repositories", e);
        }

    }


    // ------------------------------------------------------------------------------------------ //
    // BUILDER
    // ------------------------------------------------------------------------------------------ //

    public static class Config {
        private Config() {
        }

        // MANDATORY SETTINGS
        private final Map<Class<? extends ElsaModel>, DaoConfig> daoConfigMap = new HashMap<>();
        private HttpHost[] httpHosts;

        // OPTIONAL SETTINGS
        private RestClientConfig restClientConfig;
        private Listener bulkResponseListener;
        private RequestOptions requestOptionsForBulkProcessor;
        private boolean createIndexesAndEnsureMappingConsistency;
        private BulkProcessorConfigurator bulkProcessorConfigurator;
        private RepositoryBucket.Config repositoryBucketConfig;
        private ModelMapper modelMapper;


        public Config setClusterNodes(final HttpHost[] httpHosts) {
            this.httpHosts = httpHosts;
            return this;
        }

        public Config configureLowLevelClient(final RestClientConfig restClientConfig) {
            this.restClientConfig = restClientConfig;
            return this;
        }

        public Config registerDAO(final DaoConfig daoConfig) {
            Objects.requireNonNull(daoConfig, "daoConfig class must not be NULL.");
            Objects.requireNonNull(daoConfig.getModelClass(), "Model class must not be NULL.");
            Objects.requireNonNull(daoConfig.getDaoClass(), "DAO class must not be NULL.");

            if (this.daoConfigMap.putIfAbsent(daoConfig.getModelClass(), daoConfig) != null) {
                throw new IllegalStateException("Model already registered in ElsaClient.Builder: " + daoConfig.getModelClass() + "\n" +
                        "Make a copy (or simply extend) of the io.github.ss3rg3.elsa.model class if you want separate DAOs for whatever reason.");
            }
            return this;
        }

        /**
         * If the indices do not exist, then they will be created. If they exist, their mapping will be updated.
         * If the new mapping is invalid, then this fail on startup.
         */
        public Config createIndexesAndEnsureMappingConsistency(final boolean defaultIsTrue) {
            this.createIndexesAndEnsureMappingConsistency = defaultIsTrue;
            return this;
        }

        public Config configureGson(final ModelMapper.Configurator configurator) {
            this.modelMapper = new ModelMapper(configurator);
            return this;
        }

        /**
         * This will add a custom listener to the BulkProcessor, see
         * <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-bulk-processor.html">here</a>
         * for more info.
         */ // Warning is IntelliJ bug. Return value is used, but since it's last position in the builder it's somehow not registered?
        public Config setBulkResponseListener(final Listener defaultIsDefaultBulkResponseListener,
                                              final RequestOptions defaultIsRequestOptionsDefault) {
            this.bulkResponseListener = defaultIsDefaultBulkResponseListener;
            this.requestOptionsForBulkProcessor = defaultIsRequestOptionsDefault;
            return this;
        }

        /**
         * Here you can configure the Elastic's native BulkProcessor, see
         * <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-bulk-processor.html">here</a>
         * for more info.
         */
        public Config configureBulkProcessor(final BulkProcessorConfigurator configurator) {
            this.bulkProcessorConfigurator = configurator;
            return this;
        }

        /**
         * This will try to register the repositories for snapshots on start-up, so you can be sure to have
         * set up the `path.repo` variable in the config file elasticsearch.yml properly, before doing any operations.
         * The repositories will be registered with the default config (compress=true, type=fs).
         */
        public Config registerSnapshotRepositories(final RepositoryBucket.Config configurator) {
            this.repositoryBucketConfig = configurator;
            return this;
        }

    }

    /**
     * Convenience method for DAOs registered via ElsaClient instantiation. You can also use ElsaClient.createDAO()
     * if you need different IndexConfigs for working on multiple indices with the same io.github.ss3rg3.elsa.model.
     */
    @SuppressWarnings("unchecked")
    public <T extends ElsaDAO> T getDAO(final Class<? extends ElsaModel> modelClass) {
        return Objects.requireNonNull((T) this.daoMap.get(modelClass), "Requested DAO for io.github.ss3rg3.elsa.model class does not exist. " +
                "Make sure the following io.github.ss3rg3.elsa.model was registered in the ElsaClient instantiation: " + modelClass);
    }

    /**
     * Use this method if you want to work with multiple indices which use the same io.github.ss3rg3.elsa.model. Otherwise register DAOs
     * via the ElsaClient instantiation and use ElsaClient.getDAO(ElsaModel).
     */
    public <T extends ElsaDAO> T createDAO(final DaoConfig daoConfig) {
        return this.daoCreator.createDAO(daoConfig);
    }

    /**
     * Just use the one which is automatically created in your ElsaClient. There seems to be no use for multiple
     * BulkProcessors. Another instance will use the same ThreadPool in Elastic's io.github.ss3rg3.elsa.client. If you want prioritize specific
     * BulkRequests then put a PriorityQueue in front of it.
     */
    public BulkProcessor createAnotherBulkProcessor(final RestHighLevelClient client,
                                                    final Listener bulkResponseListener,
                                                    final RequestOptions requestOptions,
                                                    final BulkProcessorConfigurator bulkProcessorConfigurator) {

        final BulkProcessor.Builder bulkProcessorBuilder = BulkProcessor.builder(
                (request, bulkActionListener) -> client.bulkAsync(request, requestOptions, bulkActionListener), bulkResponseListener);
        if (bulkProcessorConfigurator != null) {
            return bulkProcessorConfigurator.configure(bulkProcessorBuilder).build();
        }
        return bulkProcessorBuilder.build();
    }

}
