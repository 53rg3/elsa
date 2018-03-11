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

package output.c050_ElsaResponse;

import madog.core.Output;
import madog.core.Print;
import madog.core.Ref;
import madog.markdown.List;


public class s00_ElsaResponse extends Output {

    @Override
    public void addMarkDownAsCode() {

        Print.h1("ElsaResponse");

        Print.wrapped("All requests to Elasticsearch can throw exceptions in case the cluster is offline, an index " +
                "does not exist, a request is malformed, etc. These exceptions mostly have JSON inside the message with additional " +
                "information. ELSA tries to parse the message and to pass them back to the caller. `ElsaResponse` is an " +
                "imitation of Java's `Optional` and encapsulates successful responses as well as Exceptions and provides " +
                "a convenient way to react to both without try-catch blocks.\n\n" +
                "Example:");
        Print.codeBlock("" +
                "DeleteIndexResponse response = elsa.admin.deleteIndex(\"does_not_exist\")\n" +
                "            .orElseThrow(IllegalStateException::new);\n" +
                "ElsaResponse<DeleteIndexResponse> response = elsa.admin.deleteIndex(\"does_not_exist\");\n" +
                "if(response.hasException()) {\n" +
                "   ExceptionResponse exceptionResponse = response.getExceptionResponse();\n" +
                "}\n" +
                "if(response.isPresent()) {\n" +
                "   YourModel model = response.get();\n" +
                "}\n");

    }

}
