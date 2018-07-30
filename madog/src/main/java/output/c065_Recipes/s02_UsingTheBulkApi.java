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

package output.c065_Recipes;

import madog.core.Output;
import madog.core.Print;
import madog.core.Ref;
import madog.markdown.Icon;


public class s02_UsingTheBulkApi extends Output {

    @Override
    public void addMarkDownAsCode() {

        Print.h2("Using the Bulk API");
        Print.wrapped("The BulkProcessor can only handle `IndexRequest` and `DeleteRequest`. If we need `UpdateRequest` " +
                "we need to use the `Bulk API`.");
        Print.wrapped(Icon.MAG_GLASS +" Note: If we have the model with an ID, then we can simply use `IndexRequest` for " +
                "updates, which will override the existing document.");
        Print.wrapped("Examples can be found here: "+Ref.classFile("BulkRequestTest.java"));

    }

}
