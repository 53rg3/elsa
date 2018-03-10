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

import madog.core.FileLocator.FileType;

import java.util.regex.Pattern;

public class Ref {

    private final static FileLocator fileLocator = new FileLocator();
    private final static Pattern clearedPathToOutputPattern = Pattern.compile("^\\.");

    public static String externalURL(final String url) {
        return "[" + url + "](" + url + ")";
    }

    public static String externalURL(final String url, final String linkText) {
        return "[" + linkText + "](" + url + ")";
    }

    public static String internalPath(final String url) {
        return "[" + url + "](" + url + ")";
    }

    public static String internalPath(final String url, final String linkText) {
        return "[" + linkText + "](" + url + ")";
    }

    /**
     * Any file in resources with a ending of images files, i.e. gif, jpg, jpeg, png
     */
    public static String image(final String fileName) {
        return "![]("+fileLocator.getPathToFile(fileName, FileType.IMAGE)+"?raw=true)";
    }

    /**
     * Any file in resources with a ending of images files, i.e. gif, jpg, jpeg, png
     */
    public static String image(final String fileName, final String description) {
        return "**"+description+"**<br>" +
                "![]("+fileLocator.getPathToFile(fileName, FileType.IMAGE)+"?raw=true)";
    }

    /**
     * URL to image
     */
    public static String imageExternal(final String url) {
        return "![]("+url+")";
    }

    /**
     * URL to image
     */
    public static String imageExternal(final String url, final String description) {
        return "**"+description+"**<br>" +
                "![]("+url+")";
    }

    /**
     * Any folder name in the project, i.e. /src/main/test/
     */
    public static String folder(final String pathToFolder) {
        final String folder = fileLocator.getPathToFile(pathToFolder, FileType.FOLDER);
        return "["+pathToFolder+"]("+folder+")";
    }

    /**
     * Any folder name in the project, i.e. /src/main/test/
     */
    public static String folder(final String pathToFolder, final String linkText) {
        final String folder = fileLocator.getPathToFile(pathToFolder, FileType.FOLDER);
        return "["+linkText+"]("+folder+")";
    }

    /**
     * File must exist anywhere in ./_resources/
     */
    public static String resource(final String fileName) {
        final String resource = fileLocator.getPathToFile(fileName, FileType.RESOURCE);
        return "["+fileName+"]("+resource+"?raw=true)";
    }

    /**
     * File must exist anywhere in ./_resources/
     */
    public static String resource(final String fileName, final String linkText) {
        final String resource = fileLocator.getPathToFile(fileName, FileType.RESOURCE);
        return "["+linkText+"]("+resource+"?raw=true)";
    }

    /**
     * File name of any Java class. I.e. Something.java
     */
    public static String classFile(final String fileName) {
        final String classFile = fileLocator.getPathToFile(fileName, FileType.CLASS);
        return "["+classFile+"]("+classFile+")";
    }

    /**
     * File name of any Java class. I.e. Something.java
     */
    public static String classFile(final String fileName, final String linkText) {
        final String classFile = fileLocator.getPathToFile(fileName, FileType.CLASS);
        return "["+linkText+"]("+classFile+")";
    }

    /**
     * Class that extends Output, gets translated to Markdown file path. Auto-generated link text.
     */
    public static String outputClass(final Class<? extends Output> clazz) {
        final String pathToOutput = getPathToPut(clazz);
        return "["+pathToOutput+"]("+pathToOutput+")";
    }

    /**
     * Class that extends Output, gets translated to Markdown file path.
     */
    public static String outputClass(final Class<? extends Output> clazz, final String linkText) {
        return "["+linkText+"]("+getPathToPut(clazz)+")";
    }

    private static String getPathToPut(final Class<? extends Output> clazz) {
        Output.shouldSetCurrentPath = false;
        final Output output = createDummyOutput((Class<Output>) clazz);
        Output.shouldSetCurrentPath = true;
        String pathToOutput = clearedPathToOutputPattern.matcher(output.createOutputPathFromClassPath()).replaceFirst("");
        pathToOutput = pathToOutput.replace("/"+Config.MARKDOWN_OUTPUT_FILE_NAME, "");
        if(pathToOutput.equals("")) {
            return Config.MADOG_FOLDER_NAME+"/readme.md";
        }

        if(Config.MADOG_FOLDER_NAME.equals("")) {
            return pathToOutput;
        } else {
            return Config.MADOG_FOLDER_NAME+pathToOutput+"/readme.md";
        }

    }

    private static Output createDummyOutput(final Class<Output> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Couldn't create: " + clazz.getName());
    }

}
