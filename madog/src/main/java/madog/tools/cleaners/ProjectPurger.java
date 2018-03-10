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

package madog.tools.cleaners;

import java.io.File;
import java.io.IOException;

/**
 * Deletes output folder, markdown files, Madog tests and example resources
 */
public class ProjectPurger {

    public static void purge() {
        DeleteUtils.deleteFile("./_resources/documents/example.pdf");
        DeleteUtils.deleteFile("./_resources/images/example.jpg");
        DeleteUtils.deleteLastCommit();
        DeleteUtils.deleteFolder("./src/test/java/madog");
        DeleteUtils.deleteFolder("./src/main/java/output");
    }

}
