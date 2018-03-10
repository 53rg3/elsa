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

package madog.tools.scaffolding;

import madog.core.Config;
import madog.tools.cleaners.DeleteUtils;
import madog.tools.cleaners.ProjectPurger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class CreateScaffold {

    private static final String outputPath = "./src/main/java/output";
    private static final String displayTocStatement = "" +
            "Print.accessPrinter().displayCompleteTableOfContentOfAllPagesOnThisPage(true);\n" +
            "\t\tPrint.wrapped(\"\");";

    public static void main(final String[] args) {

        checkIfMadogIsUsedAsDocTool();

        final Scanner scanner = new Scanner(System.in);
        System.out.println("Dude, ATTENTION!!");
        System.out.println("Scaffolding deletes the output directory. If you did work and didn't commit, you'll lose it!");
        System.out.println("Sure you want to proceed? Type: yes");
        if(scanner.next().equals("yes")) {
            ProjectPurger.purge();
            DeleteUtils.deleteGitFolder();
        }
        createFolder(outputPath);
        createOutputFile("", "c00_Index", true);
        createChapter("c010_Boilerplate", "s00_Boilerplate");
        createChapter("c020_Basics", "s00_Basics");
        createChapter("c020_Workflow", "s00_Workflow");
        createChapter("c030_Cookbook", "s00_Cookbook");
        createChapter("c030_Cookbook", "s01_Conventions");
        createChapter("c030_Cookbook", "s02_Recipes");
        createChapter("c040_Glossary", "s00_Glossary");
        createChapter("c050_Bookmarks", "s00_Bookmarks");
    }

    private static void createChapter(final String folder, final String fileName) {
        if(!new File(outputPath+transformToValidFolderName(folder)).exists()) {
            createFolder(outputPath+transformToValidFolderName(folder));
        }
        createOutputFile(folder, fileName, false);
    }

    private static void createOutputFile(final String folder, final String fileName, final boolean shouldDisplayToc) {
        try {
            final String filePath = outputPath + transformToValidFolderName(folder) + transformToJavaFileName(fileName);
            Files.write(Paths.get(filePath), getBlankClassAsString(folder, fileName, shouldDisplayToc).getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static String transformToValidFolderName(final String fileName) {
        if(fileName == null || fileName.equals("")) {
            return "";
        }
        return "/"+fileName;
    }

    private static String transformToJavaFileName(final String fileName) {
        if(fileName == null || fileName.equals("")) {
            return "";
        }
        return "/"+fileName+".java";
    }

    private static void createFolder(final String folder) {
        try {
            Files.createDirectory(Paths.get(folder));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static String getBlankClassAsString(final String folder, final String className, final boolean shouldDisplayToc) {
        final String blankClass = "" +
                "package outputPACKAGE_NAME;\n" +
                "\n" +
                "import madog.markdown.Icon;\n" +
                "import madog.core.Output;\n" +
                "import madog.core.Print;\n" +
                "import madog.core.Ref;\n" +
                "\n" +
                "public class CLASS_NAME extends Output {\n" +
                "\n" +
                "    @Override\n" +
                "    public void addMarkDownAsCode() {\n\n" +
                "        DISPLAY_TOC\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "}";

        String result = blankClass.replace("CLASS_NAME", className);

        if(shouldDisplayToc) {
            result = result.replace("DISPLAY_TOC", displayTocStatement);
        } else {
            result = result.replace("DISPLAY_TOC", "");
        }

        if(folder == null || folder.equals("")) {
            result = result.replace("PACKAGE_NAME", "");
        } else {
            result = result.replace("PACKAGE_NAME", "."+folder);
        }

        return result;
    }

    private static void checkIfMadogIsUsedAsDocTool() {
        if(Config.USE_AS_DOC_TOOL) {
            throw new IllegalStateException("ProjectPurger shouldn't be used if Madog is used as doc tool. Ain't gonna waste more time on this shit.");
        }
    }

}
