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

package madog.tools;

import madog.core.FileLocator;
import madog.core.FileLocator.FilePojo;
import madog.core.FileLocator.FileType;

import java.util.Map.Entry;

public class ListAllFileNames {

    private static final FileLocator fileLocator = new FileLocator();

    public static void main(final String[] args) {
        fileLocator.getFileMap().entrySet()
                .stream()
                .filter(ListAllFileNames::shouldShow)
                .sorted(Entry.comparingByKey())
                .forEach(entry -> System.out.println("" +
                        "Key: " + entry.getKey() + " " +
                        "- FileType: " + entry.getValue().getFileType() + " " +
                        "- Path: "+entry.getValue().getPath()));
    }

    private static boolean shouldShow(Entry<String,FilePojo> entry) {

        if(entry.getValue().getFileType().equals(FileType.NOT_SUITABLE_FOR_REFERENCE)) {
           return false;
        }

        if(entry.getKey().contains(".git")) {
            return false;
        }

        return true;
    }

}
