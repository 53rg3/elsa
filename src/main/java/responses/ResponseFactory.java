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

package responses;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import helpers.ResponseParser;
import org.elasticsearch.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import statics.ElsaStatics;
import statics.Messages.ExceptionMsg;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import static helpers.KeysToValuesConverter.convertObjectOfObjects;
import static helpers.KeysToValuesConverter.convertSingleObject;
import static helpers.XJsonType.mapStringObjectType;
import static statics.ElsaStatics.GSON;

public class ResponseFactory {
    private ResponseFactory() {
    }

    private static final Type repositoryInfoResponseType = new TypeToken<List<RepositoryInfoResponse>>() {
    }.getType();
    private static final Type snapshotInfoResponseType = new TypeToken<List<SnapshotInfoResponse>>() {
    }.getType();
    private static final Logger logger = LoggerFactory.getLogger(ResponseFactory.class);

    public static ReindexResponse createReindexResponse(final Response response) {
        try (final InputStreamReader reader = ResponseParser.convertToReader(response)) {
            return ElsaStatics.GSON.fromJson(reader, ReindexResponse.class);
        } catch (final IOException e) {
            logger.error(ExceptionMsg.FAILED_TO_GET_INPUTSTREAMREADER_FROM_RESPONSE, e);
            throw new IllegalStateException(ExceptionMsg.FAILED_TO_GET_INPUTSTREAMREADER_FROM_RESPONSE, e);
        }
    }

    public static ConfirmationResponse createConfirmationResponse(final Response response) {
        try (final InputStreamReader reader = ResponseParser.convertToReader(response)) {
            return ElsaStatics.GSON.fromJson(reader, ConfirmationResponse.class);
        } catch (final IOException e) {
            logger.error(ExceptionMsg.FAILED_TO_GET_INPUTSTREAMREADER_FROM_RESPONSE, e);
            throw new IllegalStateException(ExceptionMsg.FAILED_TO_GET_INPUTSTREAMREADER_FROM_RESPONSE, e);
        }
    }

    public static List<SnapshotInfoResponse> createSnapshotInfoListResponse(final Response response) {
        try (final InputStreamReader reader = ResponseParser.convertToReader(response)) {
            final JsonElement jsonElement = ElsaStatics.GSON.fromJson(reader, JsonObject.class)
                    .getAsJsonArray("snapshots");
            return ElsaStatics.GSON.fromJson(jsonElement, snapshotInfoResponseType);
        } catch (final IOException e) {
            logger.error(ExceptionMsg.COULD_NOT_EXTRACT_SNAPSHOT_FROM_JSON, e);
            throw new IllegalStateException(ExceptionMsg.COULD_NOT_EXTRACT_SNAPSHOT_FROM_JSON, e);
        }
    }

    public static SnapshotInfoResponse createSnapshotInfoResponse(final Response response) {
        try (final InputStreamReader reader = ResponseParser.convertToReader(response)) {
            final JsonElement jsonElement = ElsaStatics.GSON.fromJson(reader, JsonObject.class)
                    .getAsJsonArray("snapshots")
                    .get(0);
            return ElsaStatics.GSON.fromJson(jsonElement, SnapshotInfoResponse.class);
        } catch (final IOException e) {
            logger.error(ExceptionMsg.COULD_NOT_EXTRACT_SNAPSHOT_FROM_JSON, e);
            throw new IllegalStateException(ExceptionMsg.COULD_NOT_EXTRACT_SNAPSHOT_FROM_JSON, e);
        }
    }

    public static RepositoryInfoResponse createRepositoryInfoResponse(final Response response) {
        final String json = ResponseParser.convertToString(response);
        final String convertedJson = convertSingleObject(GSON.fromJson(json, mapStringObjectType), "name");
        return GSON.fromJson(convertedJson, RepositoryInfoResponse.class);
    }

    public static List<RepositoryInfoResponse> createRepositoryInfoListResponse(final Response response) {
        final String json = ResponseParser.convertToString(response);
        final String convertedJson = convertObjectOfObjects(GSON.fromJson(json, mapStringObjectType), "name");
        return GSON.fromJson(convertedJson, repositoryInfoResponseType);
    }

}
