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

package output.c080_FAQ;

import madog.core.Output;
import madog.core.Print;
import madog.markdown.List;


public class c00_FAQ extends Output {

    @Override
    public void addMarkDownAsCode() {

        Print.h1("FAQ (Undone)");
        List list = new List();

        list.entry("What's the difference between REST HLC & LLC? When to use which?", "" +
                "The high-level REST client, which is based on the low-level client, takes care of request marshalling and response un-marshalling. " +
                "The HLC is just a bunch of simplified methods decorated with a LLC. So it's actually just a wrapper which provides more functionality.");

        list.entry("TransportClient vs new REST Client?", "" +
                "Transport client uses TCP to communicate. It is used internally for communication in an Elasticsearch cluster. REST client uses HTTP. " +
                "REST client is slower, but the performance is acceptable. Basically, the company just wanted less effort and a single method for all users to " +
                "communicate with the cluster.");

        Print.wrapped(list.getAsMarkdown());

    }

}
