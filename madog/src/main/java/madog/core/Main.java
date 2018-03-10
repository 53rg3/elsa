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
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

public class Main {

    public static void main(final String[] args) {
        try (Stream<Path> paths = Files.walk(Paths.get(FileLocator.getMadogFolder()))) {
            paths
                    .filter(path -> path.toString().endsWith(".java"))
                    .sorted()
                    .map(Main::transformFilePathToClassPath)
                    .map(Main::createOutputClass)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Main::createOutputFromClassPath)
                    .forEach(Output::addMarkDownAsCode);

            Print.accessPrinter().printMarkdownFiles();

        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static Optional<Class<Output>> createOutputClass(final String classPath) {
        try {
            final Class<?> clazz = Class.forName(classPath);
            if(clazz.getSuperclass().equals(Output.class)) {
                return Optional.of((Class<Output>) clazz);
            }
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Optional.empty();
}

    private static Output createOutputFromClassPath(final Class<Output> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (final InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Couldn't create: " + clazz.getName());
    }

    private static String transformFilePathToClassPath(final Path path) {
        return path.toString()
                .replace(".java", "")
                .replace(FileLocator.getMadogFolder(), "")
                .replace("/", ".");
    }


}
