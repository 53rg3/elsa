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

package statics;

public class Messages {
    private Messages() {
    }

    // ------------------------------------------------------------------------------------------ //
    // EXCEPTIONS
    // ------------------------------------------------------------------------------------------ //
    public static class ExceptionMsg {
        private ExceptionMsg() {
        }

        public static final String REQUEST_FAILED = "Couldn't perform Elasticsearch request.";
        public static final String CLASS_INSTANTIATION_FAILED = "Couldn't create instance from class.";
        public static final String XCONTENTBUILDER_FAILED_TO_CREATE_JSON = "Couldn't create JSON from XContentBuilder.";
        public static final String FAILED_TO_GET_INPUTSTREAM_FROM_RESPONSE = "Failed to get InputStream from Response.";
        public static final String FAILED_TO_GET_INPUTSTREAMREADER_FROM_RESPONSE = "Failed create InputStreamReader from Response.";
        public static final String COULD_NOT_EXTRACT_SNAPSHOT_FROM_JSON = "Couldn't extract snapshot from JSON.";
        public static final String KEY_ALREADY_EXISTS_CAUSED_BY = "Key already exists, caused by: ";
    }

}
