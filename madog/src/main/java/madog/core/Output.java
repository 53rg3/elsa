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

public abstract class Output {

    public static boolean shouldSetCurrentPath = true;

    public Output() {
        if (shouldSetCurrentPath) {
            Print.setCurrentPage(this.createOutputPathFromClassPath());
        }
    }

    public abstract void addMarkDownAsCode();

    public String createOutputPathFromClassPath() {
        final String classPath = this.getClass().getName();
        if (!classPath.startsWith("output")) {
            throw new IllegalArgumentException("All Output classes must reside in /output/ folder, found class outside. Check: " + classPath);
        }

        return this.transformClassPathToOutputPath(classPath);
    }

    private String transformClassPathToOutputPath(final String classPath) {
        String outputPath = replaceLast(classPath, "\\." + this.getClass().getSimpleName(), "/" + Config.MARKDOWN_OUTPUT_FILE_NAME);
        outputPath = Config.PACKAGE_OUTPUT_PATTERN.matcher(outputPath).replaceFirst("");
        outputPath = Config.CLASS_SORTING_PATTERN.matcher(outputPath).replaceAll("/");
        outputPath = Config.SUBPAGE_PATTERN.matcher(outputPath).replaceAll("/");
        if(outputPath.startsWith("/")) {
            outputPath = "." + outputPath;
        } else {
            outputPath = "./" + outputPath;
        }

        return outputPath;
    }

    private static String replaceLast(final String text, final String regex, final String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

}
