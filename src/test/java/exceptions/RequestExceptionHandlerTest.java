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

package exceptions;

import assets.*;
import client.ElsaClient;
import dao.CrudDAO;
import helpers.IndexName;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.*;
import org.junit.runners.MethodSorters;
import reindexer.ReindexOptions.ReindexMode;
import reindexer.ReindexSettings;
import reindexer.ReindexSettings.ReindexSettingsBuilder;
import responses.ElsaResponse;
import scroller.ScrollManager;
import snapshotter.CreateRepositoryRequest;
import snapshotter.CreateSnapshotRequest;
import snapshotter.RestoreSnapshotRequest;

import java.util.regex.Pattern;

import static helpers.Search.req;
import static helpers.Search.src;
import static junit.framework.TestCase.assertTrue;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.search.builder.SearchSourceBuilder.searchSource;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class RequestExceptionHandlerTest {

    private static final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
    private static final ElsaClient elsa = new ElsaClient(c -> c
            .setClusterNodes(httpHosts)
            .registerModel(FakerModelForExceptionTesting.class, CrudDAO.class)
            .registerModel(FakerModelInvalidMapping2.class, CrudDAO.class)
            .createIndexesAndEnsureMappingConsistency(false));
    private static final CrudDAO<FakerModelForExceptionTesting> dao = elsa.getDAO(FakerModelForExceptionTesting.class);
    private static final CrudDAO<FakerModelInvalidMapping2> daoForInvalid = elsa.getDAO(FakerModelInvalidMapping2.class);
    private static final FakerModelForExceptionTesting model = FakerModelForExceptionTesting.createModelWithRandomData();
    private static final FakerModelInvalidMapping2 modelInvalid2 = FakerModelInvalidMapping2.createModelWithRandomData();
    private static final String testIndexName = "exception_handler_test";
    private static final FakerModelInvalidMapping modelWithInvalidMapping = new FakerModelInvalidMapping();
    private static final String oldIndexName = modelWithInvalidMapping.getIndexConfig().getIndexName();
    private static final String oldIndexName2 = modelInvalid2.getIndexConfig().getIndexName();

    @BeforeClass
    public static void setup() {
        modelInvalid2.getIndexConfig().setIndexName(FakerModelForExceptionTesting.getIndexName());
    }

    @AfterClass
    public static void tearDown() {
        modelInvalid2.getIndexConfig().setIndexName(oldIndexName2);
    }

    @After
    public void resetIndexName() {
        modelWithInvalidMapping.getIndexConfig().setIndexName(oldIndexName);
    }

    @Test
    public void createIndex_indexAlreadyExists_extractInfo() {
        final Pattern regex = Pattern.compile("index.*already exists");
        final ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();

        elsa.admin.createIndex(FakerModelForExceptionTesting.class, exceptionHandler);
        elsa.admin.createIndex(FakerModelForExceptionTesting.class, exceptionHandler);

        assertThat(exceptionHandler.getInfo().getStatus(), is(400));
        assertTrue(regex.matcher(exceptionHandler.getInfo().getError().getReason()).find());

        elsa.admin.deleteIndex(FakerModelForExceptionTesting.class);
    }

    @Test
    public void updateIndexMapping_mappingDoesNotFit_extractInfo() {
        final Pattern regex = Pattern.compile("mapper.*of different type");
        final ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();
        elsa.admin.createIndex(FakerModelForExceptionTesting.class, exceptionHandler);
        modelWithInvalidMapping.getIndexConfig().setIndexName(testIndexName);

        elsa.admin.updateMapping(FakerModelInvalidMapping.class, exceptionHandler);

        assertThat(exceptionHandler.getInfo().getStatus(), is(400));
        assertTrue(regex.matcher(exceptionHandler.getInfo().getError().getReason()).find());

        elsa.admin.deleteIndex(testIndexName);
    }

    @Test
    public void indexExists_indexDoesNotExist_handlerIsCalled() {
        // Non-existing cluster
        final HttpHost[] httpHosts = {new HttpHost("localhost", 9999, "http")};
        final ElsaClient elsaWithNonExistingCluster = new ElsaClient(c -> c
                .setClusterNodes(httpHosts)
                .registerModel(FakerModel.class, CrudDAO.class)
                .createIndexesAndEnsureMappingConsistency(false));

        ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();
        elsaWithNonExistingCluster.admin.indexExists(FakerModelForExceptionTesting.class, exceptionHandler);
        assertTrue(exceptionHandler.wasCalled());

        exceptionHandler = new ExceptionHandlerWithExtractor();
        elsaWithNonExistingCluster.admin.indexExists("non_existing_index", exceptionHandler);
        assertTrue(exceptionHandler.wasCalled());
    }

    @Test
    public void deleteIndex_indexDoesNotExist_extractInfo() {
        final ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();
        elsa.admin.deleteIndex(FakerModelForExceptionTesting.class, exceptionHandler);

        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(404));
    }

    @Test
    public void index_indexDoesNotExist_extractInfo() {
        final ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();

        elsa.admin.createIndex(FakerModelInvalidMapping2.class);
        daoForInvalid.index(modelInvalid2, exceptionHandler);
        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(400));

        elsa.admin.deleteIndex(FakerModelForExceptionTesting.getIndexName());
    }

    @Test
    public void get_indexDoesNotExist_extractInfo() {
        ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();

        dao.get("someId", exceptionHandler);
        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(404));

        exceptionHandler = new ExceptionHandlerWithExtractor();
        dao.getRawResponse("someId", exceptionHandler);
        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(404));
    }

    @Test
    public void delete_indexDoesNotExist_extractInfo() {
        final ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();
        model.setId("someId");

        dao.delete(model, exceptionHandler);

        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(404));
    }

    @Test
    public void update_indexDoesNotExist_extractInfo() {
        final ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();
        model.setId("someId");

        dao.update(model, exceptionHandler);

        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(404));
        elsa.admin.deleteIndex(model.getIndexConfig().getIndexName());
    }

    @Test
    public void reindexer_indexDoesNotExist_extractInfo() {
        final ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();
        final ReindexSettings reindexSettings = new ReindexSettingsBuilder()
                .configureSource(c -> c
                        .fromIndex("does_not_exist"))
                .configureDestination(c -> c
                        .intoIndex("also_does_not_exist"))
                .build();
        elsa.reindexer.execute(reindexSettings, ReindexMode.DESTINATION_INDEX_AND_MAPPINGS_ALREADY_EXIST, exceptionHandler);

        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(404));
    }

    @Test
    public void scroller_indexDoesNotExistAndScrollIdIsInvalid_extractInfo() {
        ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();
        final SearchRequest request = new SearchRequest()
                .indices(FakerModelForExceptionTesting.getIndexName())
                .source(searchSource()
                        .query(matchAllQuery())
                        .size(1));
        final ScrollManager scrollManager = new ScrollManager(TimeValue.timeValueMillis(1L));

        // Index does not exist
        elsa.scroller.initialize(request, scrollManager, exceptionHandler);
        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(404));

        // With existing index
        exceptionHandler = new ExceptionHandlerWithExtractor();
        elsa.admin.createIndex(FakerModelForExceptionTesting.class);
        final ScrollManagerFake scrollManagerFake = new ScrollManagerFake(TimeValue.timeValueSeconds(1L));
        final ElsaResponse<SearchResponse> searchResponse = elsa.scroller.initialize(request, scrollManagerFake, exceptionHandler);
        elsa.scroller.getNext(scrollManagerFake, searchResponse.get(), exceptionHandler);

        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(400));

        exceptionHandler = new ExceptionHandlerWithExtractor();
        elsa.scroller.clearScroll(scrollManagerFake, exceptionHandler);
        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(400));

        elsa.admin.deleteIndex(FakerModelForExceptionTesting.class);
    }

    @Test
    public void search_IndexDoesNotExist_extractInfo() {
        final ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();
        final SearchRequest request = new SearchRequest()
                .indices(FakerModelForExceptionTesting.getIndexName())
                .source(searchSource()
                        .query(matchAllQuery())
                        .size(1));
        dao.search(request, exceptionHandler);

        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(404));
    }

    @Test
    public void snapshotter_InfoMethodsWithoutArguments_handlerWasCalled() {
        // Requests without arguments which don't fail, so we use a non-existing cluster
        final ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();
        final HttpHost[] httpHosts = {new HttpHost("localhost", 9999, "http")};
        final ElsaClient elsaWithNonExistingCluster = new ElsaClient(c -> c
                .setClusterNodes(httpHosts)
                .registerModel(FakerModel.class, CrudDAO.class)
                .createIndexesAndEnsureMappingConsistency(false));

        elsaWithNonExistingCluster.snapshotter.getRepositories(exceptionHandler);
        assertTrue(exceptionHandler.wasCalled());
    }

    @Test
    public void snapshotter_InfoMethodsWithArguments_extractInfo() {
        ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();
        elsa.snapshotter.getRepositoryByName("repo_does_not_exist", exceptionHandler);
        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(404));


        exceptionHandler = new ExceptionHandlerWithExtractor();
        elsa.snapshotter.getSnapshots("repo_does_not_exist", exceptionHandler);
        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(404));


        exceptionHandler = new ExceptionHandlerWithExtractor();
        elsa.snapshotter.getSnapshotByName("repo_does_not_exist", "snapshot_does_exist", exceptionHandler);
        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(404));
    }

    @Test
    public void snapshotter_createFails_extractInfo() {
        ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();
        elsa.snapshotter.createRepository(new CreateRepositoryRequest(c -> c
                .pathToLocation("%&/(")
                .repositoryName("%&/(")), exceptionHandler);
        assertTrue(exceptionHandler.wasCalled());


        exceptionHandler = new ExceptionHandlerWithExtractor();
        elsa.snapshotter.createSnapshot(new CreateSnapshotRequest(c -> c
                .indices("asdf")
                .repositoryName("does_not_exist")
                .snapshotName("does_not_exist")), exceptionHandler);
        System.out.println(exceptionHandler.getInfo());
        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(404));


        exceptionHandler = new ExceptionHandlerWithExtractor();
        elsa.snapshotter.restoreSnapshot(new RestoreSnapshotRequest(c -> c
                .indices("does_not_exist")
                .repositoryName("does_not_exist")
                .snapshotName("does_not_exist")), exceptionHandler);
        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(404));


        exceptionHandler = new ExceptionHandlerWithExtractor();
        elsa.snapshotter.deleteSnapshot("does_not_exist", "does_not_exist", exceptionHandler);
        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(404));


        exceptionHandler = new ExceptionHandlerWithExtractor();
        elsa.snapshotter.deleteRepository("does_not_exist", exceptionHandler);
        assertTrue(exceptionHandler.wasCalled());
        assertThat(exceptionHandler.getInfo().getStatus(), is(404));
    }

    @Test
    public void elsaClient_withRequestExceptionHandler_extractInfo() {
        final ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();
        final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
        final ElsaClient elsa = new ElsaClient(c -> c
                .setClusterNodes(httpHosts)
                .registerModel(FakerModelForExceptionTesting.class, CrudDAO.class)
                .createIndexesAndEnsureMappingConsistency(false)
                .setRequestExceptionHandler(exceptionHandler));
        final Pattern regex = Pattern.compile("index.*already exists");

        elsa.admin.createIndex(FakerModelForExceptionTesting.class, exceptionHandler);
        elsa.admin.createIndex(FakerModelForExceptionTesting.class, exceptionHandler);

        assertThat(exceptionHandler.getInfo().getStatus(), is(400));
        assertTrue(regex.matcher(exceptionHandler.getInfo().getError().getReason()).find());

        elsa.admin.deleteIndex(FakerModelForExceptionTesting.class);
    }

    @Test
    public void search_clusterIsOffline_extractInfo() {
        final HttpHost[] httpHosts = {new HttpHost("localhost", 1111, "http")};
        final ElsaClient elsa = new ElsaClient(c -> c
                .setClusterNodes(httpHosts)
                .registerModel(FakerModel.class, CrudDAO.class)
                .createIndexesAndEnsureMappingConsistency(false));
        final CrudDAO<FakerModel> dao = elsa.getDAO(FakerModel.class);

        final SearchRequest request = req()
                .indices(IndexName.of(FakerModel.class))
                .source(src()
                        .query(matchAllQuery()));

        ExceptionHandlerWithExtractor exceptionHandler = new ExceptionHandlerWithExtractor();
        dao.search(request, exceptionHandler);
        assertThat(exceptionHandler.getInfo().getStatus(), is(503));

        exceptionHandler = new ExceptionHandlerWithExtractor();
        dao.searchAndMapFirstHit(request, exceptionHandler);
        assertThat(exceptionHandler.getInfo().getStatus(), is(503));

        exceptionHandler = new ExceptionHandlerWithExtractor();
        dao.searchAndMapToList(request, exceptionHandler);
        assertThat(exceptionHandler.getInfo().getStatus(), is(503));

        exceptionHandler = new ExceptionHandlerWithExtractor();
        dao.searchAndMapToStream(request, exceptionHandler);
        assertThat(exceptionHandler.getInfo().getStatus(), is(503));
    }
    
    @Test(expected = IllegalStateException.class)
    public void throwExceptionHandler_throws() {
        final HttpHost[] httpHosts = {new HttpHost("localhost", 9200, "http")};
        final ElsaClient elsa = new ElsaClient(c -> c
                .setClusterNodes(httpHosts)
                .registerModel(FakerModel.class, CrudDAO.class)
                .setRequestExceptionHandler(new ThrowExceptionHandler()));
        elsa.admin.deleteIndex("does_not_exist");
    }
}
