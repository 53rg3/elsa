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
import statics.ElsaStatics;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static helpers.KeysToValuesConverter.convertObjectOfObjects;
import static helpers.KeysToValuesConverter.convertSingleObject;
import static helpers.XJsonType.mapStringObjectType;
import static statics.ElsaStatics.GSON;

public class ResponseFactory {
    private ResponseFactory() {}

    private static final Type repositoryInfoResponseType = new TypeToken<List<RepositoryInfoResponse>>(){}.getType();
    private static final Type snapshotInfoResponseType = new TypeToken<List<SnapshotInfoResponse>>(){}.getType();

    public static ReindexResponse createReindexResponse(final Response response) {
        return ElsaStatics.GSON.fromJson(ResponseParser.convertToReader(response), ReindexResponse.class);
    }

    public static ConfirmationResponse createConfirmationResponse(final Response response) {
        return ElsaStatics.GSON.fromJson(ResponseParser.convertToReader(response), ConfirmationResponse.class);
    }

    public static List<SnapshotInfoResponse> createSnapshotInfoListResponse(final Response response) {
        try {
            JsonElement jsonElement = ElsaStatics.GSON.fromJson(ResponseParser.convertToReader(response), JsonObject.class)
                    .getAsJsonArray("snapshots");
            return ElsaStatics.GSON.fromJson(jsonElement, snapshotInfoResponseType);
        } catch(Exception e) {
            throw new IllegalStateException("Couldn't extract snapshot from JSON. That's a bug.");
        }
    }

    public static SnapshotInfoResponse createSnapshotInfoResponse(final Response response) {
        try {
            JsonElement jsonElement = ElsaStatics.GSON.fromJson(ResponseParser.convertToReader(response), JsonObject.class)
                    .getAsJsonArray("snapshots")
                    .get(0);
            return ElsaStatics.GSON.fromJson(jsonElement, SnapshotInfoResponse.class);
        } catch(Exception e) {
            throw new IllegalStateException("Couldn't extract snapshot from JSON. That's a bug.");
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
