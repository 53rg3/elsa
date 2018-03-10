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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileLocator {

    private final Pattern outputFilePattern = Pattern.compile("\\..+/.+\\.md");
    private final Pattern outputClassPattern = Pattern.compile("^.*output/.*\\.java$");
    private final Set<String> gitIgnoreSet = getGitIgnoreAsSet();
    private final Map<String, FilePojo> fileMap = loadAllFilesIntoMap();

    public String getPathToFile(final String fileName, final FileType expectedFileType) {
        final FilePojo filePojo = this.fileMap.get(fileName);
        if (filePojo == null) {
            throw new IllegalStateException("Can't find file: " + fileName);
        }

        if (filePojo.getFileType().equals(FileType.FILE_IN_GIT_IGNORE)) {
            throw new IllegalStateException("Linked file is in .gitignore - File: " + filePojo.getPath());
        }

        if (filePojo.getFileType().equals(FileType.DUPLICATE_FILENAME)) {
            throw new IllegalStateException("Trying to link ambiguous with file name \""+fileName+"\". Use Ref.externalURL() with relative path.\nOr use this if it's the correct file: Ref.externalURL(\""+filePojo.getPath()+"\")");
        }

        if (!filePojo.getFileType().equals(expectedFileType)) {
            throw new IllegalStateException("File type doesn't match expected type. Type: " + filePojo.getFileType() + ". Expected: " + expectedFileType);
        }
        return filePojo.getPath();
    }

    private Map<String, FilePojo> loadAllFilesIntoMap() {
        try (Stream<Path> stream = Files.walk(Paths.get("./"))) {
            final List<Path> list = stream
                    .filter(this::fileFilter)
                    .collect(Collectors.toList());

            final Map<String, FilePojo> fileMap = new HashMap<>();
            for (final Path path : list) {
                final String key = this.createFileNameFromPath(path);
                Path pathWithMadogFolder = Paths.get(FileLocator.getFilePathWithMadogFolder(path.toString(), true));
                if (!fileMap.containsKey(key)) {
                    fileMap.put(key, new FilePojo(pathWithMadogFolder, this.determineFileType(path)));
                } else {
                    if (Config.USE_AS_DOC_TOOL) {
                        fileMap.put(key, new FilePojo(pathWithMadogFolder, FileType.DUPLICATE_FILENAME));
                    } else {
                        throw new IllegalStateException(
                                "Found duplicate file name: " + key + " - Path: " + path + "\n" +
                                        "Other file: " + fileMap.get(key).getPath() + "\n " +
                                        "If you want to use Madog as doc tool, then edit the config variable for it."
                        );
                    }
                }
            }
            return fileMap;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Couldn't create fileMap");
    }

    private boolean fileFilter(final Path path) {

        final String pathAsString = path.toString();

        if (pathAsString.startsWith("./.git") || pathAsString.equals(".") || pathAsString.endsWith(".md")) {
            return false;
        }

        if (this.outputFilePattern.matcher(pathAsString).matches()) {
            return false;
        }

        return true;
    }

    private String createFileNameFromPath(final Path filePath) {
        if (filePath.toFile().isDirectory()) {
            return filePath.toString().replaceFirst("^\\.", "");
        } else {
            return filePath.toFile().getName().replaceFirst("^\\.", "");
        }
    }

    private FileType determineFileType(final Path filePath) {

        if (this.isFileInGitIgnore(filePath.toString())) {
            return FileType.FILE_IN_GIT_IGNORE;
        }

        if (this.outputClassPattern.matcher(filePath.toString()).matches() && isOutputClass(filePath)) {
            return FileType.OUTPUT_CLASS;
        }

        if (filePath.toString().endsWith(".java")) {
            return FileType.CLASS;
        }

        if (filePath.toFile().isDirectory()) {
            return FileType.FOLDER;
        }

        if (filePath.toString().contains("_resources")) {
            if (this.isFileAnImage(filePath.toString())) {
                return FileType.IMAGE;
            }
            return FileType.RESOURCE;
        }

        return FileType.NOT_SUITABLE_FOR_REFERENCE;
    }

    private boolean isOutputClass(final Path filePath) {
        final String classPath = filePath.toString()
                .replace(".java", "")
                .replace(getMadogFolder(), "")
                .replace("/", ".");
        try {
            final Class<?> clazz = Class.forName(classPath);
            if (clazz.getSuperclass().equals(Output.class)) {
                return true;
            }
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isFileInGitIgnore(String filePath) {

        filePath = filePath.replace("./", "");

        for (final String gitIgnoreEntry : gitIgnoreSet) {
            if (gitIgnoreEntry.startsWith("*")) {
                if (filePath.endsWith(gitIgnoreEntry.replace("*", ""))) {
                    return true;
                }
            }

            if (filePath.startsWith(gitIgnoreEntry.replace("*", "")) || filePath.endsWith(gitIgnoreEntry.replace("/*", ""))) {
                return true;
            }
        }
        return false;
    }

    private Set<String> getGitIgnoreAsSet() {
        try {
            return new HashSet<>(Files.readAllLines(Paths.get(".gitignore")));
        } catch (final IOException e) {
            // All cool if it doesn't exist
        }
        return new HashSet<>();
    }

    private boolean isFileAnImage(final String fileName) {
        return fileName.endsWith(".jpeg") || fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif");
    }

    public class FilePojo {
        private final Path path;
        private final FileType fileType;

        public FilePojo(final Path path, final FileType fileType) {
            this.path = path;
            this.fileType = fileType;
        }

        public String getPath() {
            return path.toString().replaceFirst("\\.", "");
        }

        public FileType getFileType() {
            return fileType;
        }
    }

    public enum FileType {
        RESOURCE,
        IMAGE,
        CLASS,
        OUTPUT_CLASS,
        FOLDER,
        NOT_SUITABLE_FOR_REFERENCE,
        FILE_IN_GIT_IGNORE,
        DUPLICATE_FILENAME;
    }

    public Map<String, FilePojo> getFileMap() {
        return fileMap;
    }

    public static String getMadogFolder() {
        if(Config.USE_AS_DOC_TOOL) {
            return "." + Config.MADOG_FOLDER_NAME + "/src/main/java/";
        } else {
            return "./src/main/java/";
        }
    }

    public static String getFilePathWithMadogFolder(String pathToFile, boolean withSubFolder) {

        if(withSubFolder && !pathToFile.contains(Config.MADOG_FOLDER_NAME)) {
            return pathToFile.replaceFirst("\\./", "." + Config.MADOG_FOLDER_NAME + "/");
        } else {
            return pathToFile;
        }
    }
}
