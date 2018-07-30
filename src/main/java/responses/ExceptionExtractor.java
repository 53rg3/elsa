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

import org.elasticsearch.client.ResponseException;

import javax.annotation.Nullable;
import java.net.ConnectException;

public class ExceptionExtractor {
    private ExceptionExtractor() {}

    public static ExceptionResponse createErrorResponse(Exception exception) {

        // Handle ResponseException
        Throwable throwable = findResponseException(exception);
        if(throwable != null) {
            return ExceptionResponse.createFromThrowable(throwable, exception);
        }

        if(exception instanceof ConnectException || exception.getMessage().contains("Connection refused")) {
            return ExceptionResponse.createConnectionRefused(exception);
        }

        return ExceptionResponse.createUncategorizedCause(exception);
    }



    @Nullable
    private static Throwable findResponseException(Exception exception) {

        if(exception instanceof ResponseException) {
            return exception;
        }

        for(Throwable throwable : exception.getSuppressed()) {
            if(throwable instanceof ResponseException) {
                return throwable;
            }
        }
        return null;
    }
}
