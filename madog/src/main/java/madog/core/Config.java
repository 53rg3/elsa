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

package madog.core;

import java.util.regex.Pattern;

public class Config {

    // User config parameter
    public static final String DEFAULT_HIGHLIGHTING = "JAVA";
    public static final String MADOG_FOLDER_NAME = "/madog"; // Should be "/madog" if used as doc tool or sub-project, otherwise ""
    public static final boolean USE_AS_DOC_TOOL = true; // This will enable possibility for duplicate file names. Makes linking in rare cases slightly harder.
    public static final String MARKDOWN_OUTPUT_FILE_NAME = "readme.md";


    // Internal config
    public static final Pattern CLASS_SORTING_PATTERN = Pattern.compile("\\.?[a-zA-Z]\\d?\\w?\\d?_");
    public static final Pattern PACKAGE_OUTPUT_PATTERN = Pattern.compile("^output(\\.|/)");
    public static final Pattern SUBPAGE_PATTERN = Pattern.compile("\\.(?!md$)");
    public static final String INTERNAL_LAST_COMMIT_FILE_LOCATION = "./_resources/.last_commit";

}
