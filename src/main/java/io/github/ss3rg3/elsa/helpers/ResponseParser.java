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

package io.github.ss3rg3.elsa.helpers;

import com.google.common.io.CharStreams;
import org.elasticsearch.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.ss3rg3.elsa.statics.Messages.ExceptionMsg;

import java.io.IOException;
import java.io.InputStreamReader;

public class ResponseParser {

    private static final Logger logger = LoggerFactory.getLogger(ResponseParser.class);

    /**
     * The return can be passed to GSON's fromJson
     */
    public static InputStreamReader convertToReader(final Response response) {
        try {
            return new InputStreamReader(response.getEntity().getContent());
        } catch (final IOException e) {
            logger.error(ExceptionMsg.FAILED_TO_GET_INPUTSTREAM_FROM_RESPONSE, e);
            throw new IllegalStateException(ExceptionMsg.FAILED_TO_GET_INPUTSTREAM_FROM_RESPONSE, e);
        }
    }

    public static String convertToString(final Response response) {
        try (final InputStreamReader reader = convertToReader(response)) {
            return CharStreams.toString(reader);
        } catch (final IOException e) {
            logger.error(ExceptionMsg.FAILED_TO_GET_INPUTSTREAM_FROM_RESPONSE, e);
            throw new IllegalStateException(ExceptionMsg.FAILED_TO_GET_INPUTSTREAM_FROM_RESPONSE, e);
        }
    }

}
