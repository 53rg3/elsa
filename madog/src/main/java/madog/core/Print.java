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

import java.util.regex.Pattern;

public class Print {

    private static final Printer printer = new Printer();
    private final static Pattern specialChars = Pattern.compile("[^\\d\\w\\s-]");

    public static void h1(final String markdown) {
        printer.appendToRespectiveTableOfContents(
                Char.get(0, "&nbsp;") +
                        "[" + markdown + "]" +
                        "(#" + createAnchor(markdown) + ")"
                        + Char.get(1, "<br>"),
                Depth.ONE
        );
        printer.appendToRespectiveTextSection(
                Char.get(1, "#") +
                        Char.whitespace() +
                        markdown
        );
    }

    public static void h1ButDontAddToTableOfContents(final String markdown) {
        printer.appendToRespectiveTextSection(
                Char.get(1, "#") +
                        Char.whitespace() +
                        markdown
        );
    }

    public static void h2(final String markdown) {
        printer.appendToRespectiveTableOfContents(
                Char.get(6, "&nbsp;") +
                        "[" + markdown + "]" +
                        "(#" + createAnchor(markdown) + ")"
                        + Char.get(1, "<br>"), Depth.TWO);
        printer.appendToRespectiveTextSection(
                Char.get(2, "#") +
                        Char.whitespace() +
                        markdown
        );
    }

    public static void h2ButDontAddToTableOfContents(final String markdown) {
        printer.appendToRespectiveTextSection(
                Char.get(2, "#") +
                        Char.whitespace() +
                        markdown
        );
    }

    public static void h3(final String markdown) {
        printer.appendToRespectiveTableOfContents(
                Char.get(12, "&nbsp;") +
                        "[" + markdown + "]" +
                        "(#" + createAnchor(markdown) + ")"
                        + Char.get(1, "<br>"), Depth.THREE);
        printer.appendToRespectiveTextSection(
                Char.get(3, "#") +
                        Char.whitespace() +
                        markdown
        );
    }

    public static void h3ButDontAddToTableOfContents(final String markdown) {
        printer.appendToRespectiveTextSection(
                Char.get(3, "#") +
                        Char.whitespace() +
                        markdown
        );
    }

    /**
     * Wrapped by newlines i.e. "\n"
     */
    public static void wrapped(final String markdown) {
        printer.appendToRespectiveTextSection(
                Char.wrapWithNewlines(markdown)
        );
    }

    /**
     * Not wrapped by newlines, i.e. there will be no separating empty line. Markdown
     */
    public static void inline(final String markdown) {
        printer.appendToRespectiveTextSection(
                markdown
        );
    }

    /**
     * Wrapped by ```
     * Use constants from Syntax class
     */
    public static void codeBlock(final String markdown, final String syntaxHighlighting) {
        printer.appendToRespectiveTextSection(
                Char.wrapWithCodeBlock(markdown, syntaxHighlighting)
        );
    }

    /**
     * Wrapped by ```
     * Using the default highlighting configured in Config.java
     */
    public static void codeBlock(final String markdown) {
        printer.appendToRespectiveTextSection(
                Char.wrapWithCodeBlock(markdown, Config.DEFAULT_HIGHLIGHTING)
        );
    }

    public static void emptyLine() {
        printer.appendToRespectiveTextSection(
            Char.get(1, "\n")
        );
    }

    public static void separator() {
        printer.appendToRespectiveTextSection(
                Char.get(3, "-")
        );
    }

    public static void setCurrentPage(final String currentPage) {
        printer.setCurrentPage(currentPage);
    }

    public static Printer accessPrinter() {
        return printer;
    }

    private static String createAnchor(String heading) {
        heading = specialChars.matcher(heading).replaceAll("");
        return heading
                .replace(".", "")
                .replace(" ", "-")
                .toLowerCase();
    }
}
