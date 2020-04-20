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

package reindexer;

import client.ElsaClient;
import endpoints.Endpoint;
import exceptions.ElsaException;
import helpers.RequestBody;
import model.ElsaModel;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import reindexer.ReindexOptions.ReindexMode;
import responses.ElsaResponse;
import responses.ReindexResponse;
import responses.ResponseFactory;
import statics.Method;
import statics.UrlParams;

public class Reindexer {

    private final ElsaClient elsa;

    public Reindexer(final ElsaClient elsa) {
        this.elsa = elsa;
    }

    public ElsaResponse<ReindexResponse> execute(final ReindexSettings reindexSettings,
                                                 final ReindexMode reindexMode,
                                                 final RequestOptions options) throws ElsaException {

        switch (reindexMode) {
            case CREATE_NEW_INDEX_FROM_MODEL_IN_DESTINATION:
                this.ensureDestinationModelClassExists(reindexSettings.getModelClass());
                this.elsa.admin.createIndex(reindexSettings.getModelClass(), options);
                break;
            case ABORT_IF_MAPPING_INCORRECT:
                this.ensureDestinationModelClassExists(reindexSettings.getModelClass());
                this.elsa.admin.updateMapping(reindexSettings.getModelClass());
                break;
            case DESTINATION_INDEX_AND_MAPPINGS_ALREADY_EXIST:
                break;
            default:
                throw new IllegalArgumentException("ReindexMode is not implemented, got: " + reindexMode);
        }

        try {
            final Response response = this.elsa.client.getLowLevelClient().performRequest( // todo Request
                    Method.POST,
                    Endpoint.REINDEX,
                    UrlParams.NONE,
                    RequestBody.asJson(reindexSettings.getXContentBuilder()));
            return ElsaResponse.of(ResponseFactory.createReindexResponse(response));
        } catch (final Exception e) {
            return ElsaResponse.of(e); // TODO throw ElsaException
        }
    }

    public ElsaResponse<ReindexResponse> execute(final ReindexSettings reindexSettings,
                                                 final ReindexMode reindexMode) throws ElsaException {
        return this.execute(reindexSettings, reindexMode, RequestOptions.DEFAULT);
    }

    private void ensureDestinationModelClassExists(final Class<? extends ElsaModel> modelClass) {
        if (modelClass == null) {
            throw new IllegalStateException("Model class for destination was not set in ReindexSettings.");
        }
    }


}
