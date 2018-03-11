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

package output.c010_Install;

import madog.core.Output;
import madog.core.Print;
import madog.markdown.List;
import madog.markdown.Syntax;


public class s00_Install extends Output {

    @Override
    public void addMarkDownAsCode() {

        Print.h1("Install");

        Print.wrapped("Clone the repository");
        Print.codeBlock("git clone https://github.com/53rg3/elsa.git", Syntax.BASH);

        Print.wrapped("Let Maven install it into the local repository. If you want to run tests you need a running " +
                "cluster at `http://localhost:9200`.");
        Print.codeBlock("mvn clean install -Dmaven.test.skip=true", Syntax.BASH);

        Print.wrapped("Add the dependency in the POM. Make sure the versions match (see pom.xml in repository)");
        Print.codeBlock("" +
                "<dependency>\n" +
                "    <groupId>io.github.53rg3</groupId>\n" +
                "    <artifactId>elsa</artifactId>\n" +
                "    <version>0.1</version>\n" +
                "</dependency>" +
                "", Syntax.XML);

    }

}
