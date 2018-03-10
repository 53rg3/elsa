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

package output.c091_FeatureIdeas;

import madog.core.Output;
import madog.core.Print;
import madog.core.Ref;
import madog.markdown.List;


public class c00_FeatureIdeas extends Output {

    @Override
    public void addMarkDownAsCode() {

        Print.h1("Ideas for more features");
        List list = new List();

        list.entry("Log / send notification when a node fails",
                "" +
                        "We can initialize the client with a `FailureListener`, see " +
                        ""+ Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-low-usage-initialization.html", "here")+ ". " +
                        "");

        Print.wrapped(list.getAsMarkdown());

    }

}
