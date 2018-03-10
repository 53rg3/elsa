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

package madog.markdown;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Table {

    private final List<String> rowList = new ArrayList<>();
    private static int columns = -1;

    public void header(final String... cells) {
        validateAmountOfColumns(cells);
        createRow(cells);
        createRow(Stream.of(cells).map(cell -> "---").toArray(String[]::new));
    }

    public void row(final String... cells) {
        validateAmountOfColumns(cells);
        createRow(cells);
    }

    private void createRow(final String... cells) {
        final StringBuilder header = newRow();
        Stream.of(cells).forEach(cell -> header.append(" ").append(cell).append(" |"));
        this.rowList.add(header.toString());
    }

    private StringBuilder newRow() {
        return new StringBuilder().append("|");
    }

    private void validateAmountOfColumns(final String[] cells) {
        if(columns == -1) {
            columns = cells.length;
        }

        if(cells.length != columns) {
            throw new IllegalStateException("Amount of columns in table does not match with a previous row.");
        }
    }

    public String getAsMarkdown() {
        final StringBuilder stringBuilder = new StringBuilder();
        this.rowList.forEach(row -> stringBuilder.append(row).append("\n"));
        this.rowList.clear();
        return stringBuilder.toString();
    }

}
