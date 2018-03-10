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

package output.c020_Initialization;

import madog.core.Output;
import madog.core.Print;
import madog.core.Ref;


public class c00_Initialization extends Output {

    @Override
    public void addMarkDownAsCode() {
        Print.h1("Initialization");

        Print.wrapped("See "+ Ref.externalURL("https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-low-usage-initialization.html", "Elastic Docs") + " " +
                "for current version and more details.");

    }

}
