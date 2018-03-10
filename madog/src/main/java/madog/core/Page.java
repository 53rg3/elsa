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

import madog.core.Printer.Depth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Page {
    private final List<String> textSection = new ArrayList<>();
    private final List<String> tableOfContents = new ArrayList<>();
    private boolean hasContent;
    private final Map<String,Page> outputMap;
    public static final Pattern numberingPattern = Pattern.compile("(\\[\\d+\\.)(\\d+\\.?)?(\\d+)?\\s");
    public static final Pattern anchorPattern = Pattern.compile("\\]\\(");
    private final PageType pageType;

    private int levelOne = 1;
    private int depthCountOne = 0;
    private int levelTwo = 1;
    private int depthCountTwo = 0;
    private int levelThree = 1;
    private int depthCountThree = 0;


    public Page(final PageType pageType, final Map<String,Page> outputMap) {
        this.pageType = pageType;
        this.outputMap = outputMap;
        this.tableOfContents.add("## Table of Contents");
    }

    public void appendToTextSection(final String markDown) {
        this.hasContent = true;
        this.textSection.add(markDown);
    }
    public void appendToTableOfContents(final String markDown, final Depth depth) {
        this.tableOfContents.add(markDown.replaceFirst("\\[", "["+this.getNextNumbering(depth)));
    }

    public List<String> getPageAsList() {
        if(this.pageType.equals(PageType.COMPLETE_NOTEBOOK)) {
            return this.getCompleteNoteBookAsPage();
        } else {
            if (this.tableOfContents.size() > 1 || this.outputMap != null) {
                return Stream
                        .concat(this.transformTableOfContents().stream(), this.textSection.stream())
                        .collect(Collectors.toList());
            } else {
                return this.textSection;
            }
        }
    }

    private List<String> getCompleteNoteBookAsPage() {
        if(this.outputMap == null)
            throw new IllegalStateException("OutputMap is null");

        final List<String> completeNoteBook = new ArrayList<>();
        completeNoteBook.addAll(this.transformTableOfContents());
        completeNoteBook.add("\n");
        this.outputMap.values().stream().forEach(page -> completeNoteBook.addAll(page.getTextSection()));

        return completeNoteBook;
    }

    /**
     * So dirty ...
     */
    private List<String> transformTableOfContents() {
        if(this.outputMap == null) {
            return this.tableOfContents;
        } else {
            this.resetTableOfContentsNumbering();
            final List<String> completeToc = new ArrayList<>();

            for(final Entry<String,Page> page : this.outputMap.entrySet()) {
                for(final String entry : page.getValue().getTableOfContents()) {
                    if(!entry.contains("# Table of Contents")) {
                        final Matcher matcher = Page.numberingPattern.matcher(entry);
                        if(!matcher.find()) {
                            throw new IllegalStateException("This extremely dirty thing has failed. Haha, have fun figuring that out. Fuck you.");
                        }

                        final String newTocEntry;
                        if(this.pageType.equals(PageType.COMPLETE_NOTEBOOK)) {
                            newTocEntry = entry;
                        } else {
                            newTocEntry = anchorPattern.matcher(entry).replaceFirst("]("+Config.MADOG_FOLDER_NAME + page.getKey().replaceFirst("\\./", "/"));
                        }

                        if(matcher.group(3) != null) {
                            completeToc.add(numberingPattern.matcher(newTocEntry).replaceFirst("[" + this.getNextNumbering(Depth.THREE)));
                            continue;
                        }
                        if(matcher.group(2) != null) {
                            completeToc.add(numberingPattern.matcher(newTocEntry).replaceFirst("[" + this.getNextNumbering(Depth.TWO)));
                            continue;
                        }
                        if(matcher.group(1) != null) {
                            completeToc.add(numberingPattern.matcher(newTocEntry).replaceFirst("[" + this.getNextNumbering(Depth.ONE)));
                        }
                    }
                }
            }
            return completeToc;
        }
    }

    public List<String> getTableOfContents() {
        return tableOfContents;
    }

    public List<String> getTextSection() {
        return textSection;
    }

    private void resetTableOfContentsNumbering() {
        this.levelOne = 1;
        this.depthCountOne = 0;
        this.levelTwo = 1;
        this.depthCountTwo = 0;
        this.levelThree = 1;
        this.depthCountThree = 0;
    }

    public boolean hasContent() {
        return this.hasContent;
    }

    public String getNextNumbering(final Depth depth) {
        if (depth.equals(Depth.ONE)) {
            depthCountOne++;
            if (depthCountOne > levelOne) {
                this.levelTwo = 0;
                this.levelThree = 0;
                levelOne++;
            }
            return String.valueOf(this.levelOne) + ". ";
        }
        if (depth.equals(Depth.TWO)) {
            depthCountTwo++;
            if (depthCountTwo > levelTwo) {
                this.levelThree = 0;
                levelTwo++;
            }
            return String.valueOf(this.levelOne) + "." + String.valueOf(this.levelTwo) + " ";
        }
        if (depth.equals(Depth.THREE)) {
            depthCountThree++;
            if (depthCountThree > levelThree) {
                levelThree++;
            }
            return String.valueOf(this.levelOne) + "." + String.valueOf(this.levelTwo) + "." + String.valueOf(this.levelThree) + " ";
        }
        throw new IllegalArgumentException("Depth level not implemented yet, got: " + depth);
    }

    public enum PageType {
        COMPLETE_TOC,
        COMPLETE_NOTEBOOK,
        STANDARD
    }
}
