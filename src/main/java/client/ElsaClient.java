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

package client;

import admin.IndexAdmin;
import bulkprocessor.DefaultBulkResponseListener;
import client.BulkProcessorCreator.BulkProcessorConfigurator;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import dao.ElsaDAO;
import exceptions.JustLogExceptionHandler;
import exceptions.RequestExceptionHandler;
import jsonmapper.JsonMapperLibrary;
import model.ElsaModel;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.client.RestHighLevelClient;
import reindexer.Reindexer;
import scroller.Scroller;
import snapshotter.RepositoryBucket;
import snapshotter.Snapshotter;

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

    private final ImmutableMap<Class<? extends ElsaModel>, Class<? extends ElsaDAO>> registeredModels;
    private final ImmutableMap<Class<? extends ElsaModel>, ? extends ElsaDAO> daoMap;
    private final JsonMapperLibrary jsonMapperLibrary;
    private final RequestExceptionHandler requestExceptionHandler;


    // ------------------------------------------------------------------------------------------ //
    // DEFAULTS & VALIDATE
    // ------------------------------------------------------------------------------------------ //

    @FunctionalInterface
    public interface Configurator {

        static Config loadDefaults() {
            final Config config = new Config();
            config.indexNamePrefix = "";
            config.jsonMapperLibrary = JsonMapperLibrary.GSON;
            config.requestExceptionHandler = new JustLogExceptionHandler();
            config.bulkResponseListener = new DefaultBulkResponseListener();
            config.stifleThreadUntilClusterIsOnline = false;
            config.createIndexesAndEnsureMappingConsistency = true;
            config.modelMapper = new ModelMapper();
            return config;
        }

        default void validate(final Config config) {
            Objects.requireNonNull(config.httpHosts, "HttpHost must not be null.");
            Objects.requireNonNull(config.registeredModels, "At least one model must be registered.");
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
        this.jsonMapperLibrary = config.jsonMapperLibrary;
        this.requestExceptionHandler = config.requestExceptionHandler;
        this.registeredModels = ImmutableMap.copyOf(config.registeredModels);
        this.daoMap = new DaoMapCreator(c -> c
                .elsa(this)
                .jsonMapperLibrary(this.jsonMapperLibrary)
                .registeredModels(this.registeredModels))
                .create();
        final RepositoryBucket repositoryBucket = new RepositoryBucket(config.repositoryBucketConfig);

        // COMPONENTS
        this.admin = new IndexAdmin(this);
        this.bulkProcessor = BulkProcessorCreator.createBulkProcessor(
                this.client,
                config.bulkResponseListener,
                config.bulkProcessorConfigurator);
        this.scroller = new Scroller(this);
        this.reindexer = new Reindexer(this);
        this.snapshotter = new Snapshotter(this, repositoryBucket);

        // METHODS
        ThreadStifler.waitTillClusterIsOnline(config.stifleThreadUntilClusterIsOnline, this.client);
        IndexNamePrepender.setPrefixes(config.indexNamePrefix, this.registeredModels);
        IndexCreator.createIndicesOrEnsureMappingConsistency(
                config.createIndexesAndEnsureMappingConsistency,
                this.registeredModels,
                this.admin);
        repositoryBucket.registerRepositories(this);
    }


    // ------------------------------------------------------------------------------------------ //
    // BUILDER
    // ------------------------------------------------------------------------------------------ //

    public static class Config {
        private Config() {}

        // MANDATORY SETTINGS
        private Map<Class<? extends ElsaModel>, Class<? extends ElsaDAO>> registeredModels;
        private HttpHost[] httpHosts;

        // OPTIONAL SETTINGS
        private RestClientConfig restClientConfig;
        private String indexNamePrefix;
        private JsonMapperLibrary jsonMapperLibrary;
        private RequestExceptionHandler requestExceptionHandler;
        private Listener bulkResponseListener;
        private boolean stifleThreadUntilClusterIsOnline;
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

        /**
         * Thread will sleep if cluster is offline and try to reconnect every second.
         */
        public Config stifleThreadUntilClusterIsOnline(final boolean defaultIsFalse) {
            this.stifleThreadUntilClusterIsOnline = defaultIsFalse;
            return this;
        }

        public Config registerModel(final Class<? extends ElsaModel> modelClass, final Class<? extends ElsaDAO> daoClass) {
            Objects.requireNonNull(modelClass, "Model class must not be NULL.");
            Objects.requireNonNull(daoClass, "DAO class must not be NULL.");

            if(this.registeredModels == null) {
                this.registeredModels = new HashMap<>();
            }

            if (this.registeredModels.putIfAbsent(modelClass, daoClass) != null) {
                throw new IllegalStateException("Model already registered in ElsaClient.Builder: " + modelClass + "\n" +
                        "Make a copy of the model class if you want separate DAOs for whatever reason.");
            }
            return this;
        }

        /** If the indices do not exist, then they will be created. If they exist, their mapping will be updated.
         * If the new mapping is invalid, then this fail on startup. */
        public Config createIndexesAndEnsureMappingConsistency(final boolean defaultIsTrue) {
            this.createIndexesAndEnsureMappingConsistency = defaultIsTrue;
            return this;
        }

        public Config configureGson(final ModelMapper.Configurator configurator) {
            this.modelMapper = new ModelMapper(configurator);
            return this;
        }

//        /** Well... exchanging the JSON library for mapping of the models is a nice idea, because testing and shit,
//         * but have you thought about the fact, that some field types require custom mapping functionality, e.g. dates
//         * such? No you didn't. So we would need a shit load of adapters and stuff.
//         * We take GSON, because the internet says that it faster than Jackson with smaller documents (<100kb), which
//         * is mostly the case when working with Elasticsearch.
//         * See https://blog.takipi.com/the-ultimate-json-library-json-simple-vs-gson-vs-jackson-vs-json/
//         * https://dzone.com/articles/compare-json-api */
//        public Config setJsonMapperLibrary(final JsonMapperLibrary defaultIsGson) {
//            this.jsonMapperLibrary = defaultIsGson;
//            return this;
//        }

        /**
         * In some cases the Elasticsearch cluster responds with Exceptions. You can a pass a default exception handler
         * which will be invoked if no custom handler was passed as argument to the request methods.
         */
        public Config setRequestExceptionHandler(final RequestExceptionHandler defaultIsJustLogExceptionHandler) {
            this.requestExceptionHandler = defaultIsJustLogExceptionHandler;
            return this;
        }

        /**
         * This will add a custom listener to the BulkProcessor, see
         * <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-bulk-processor.html">here</a>
         * for more info.
         */
        public Config setBulkResponseListener(final Listener defaultIsDefaultBulkResponseListener) {
            this.bulkResponseListener = defaultIsDefaultBulkResponseListener;
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

        /**
         * <h1>CAUTION:</h1>
         * This will prepend a custom string to the index name of every registered model (which allows it) on instantiation.
         * This option is meant to be used for unit testing on the same cluster. If you have two instances of Elsa running,
         * using the same models for whatever reason, this will change the index names for both clients because they are static
         * variables. <b>THIS CAN SCREW UP YOUR INDEX.</b>
         */
        public Config setIndexNamePrefix(final String indexNamePrefix) {
            this.indexNamePrefix = indexNamePrefix;
            return this;
        }

    }

    @SuppressWarnings("Type is set in this.createDaoMap()")
    public <T extends ElsaDAO> T getDAO(final Class<? extends ElsaModel> modelClass) {

        return Objects.requireNonNull((T) this.daoMap.get(modelClass), "Requested DAO for model class does not exist. " +
                "Make sure the following model was registered in the ElsaClient instantiation: " + modelClass);
    }

    public RequestExceptionHandler getRequestExceptionHandler() {
        return this.requestExceptionHandler;
    }

}
