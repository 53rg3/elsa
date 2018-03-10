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
import java.util.stream.Stream;

public class List {

    private final java.util.List<String> rowList = new ArrayList<>();
    private boolean isNumberedList = false;
    private int currentNumber = 0;

    public void entry(final String title, final String body) {
        this.rowList.add(""+
                this.nextNumber()+" **"+title+"**<br>\n" +
                body+"\n");
    }

    public void entry(final String body) {
        this.rowList.add(this.nextNumber()+" " + body+"\n");
    }

    public String getAsMarkdown() {
        final StringBuilder stringBuilder = new StringBuilder();
        this.rowList.forEach(stringBuilder::append);

        this.rowList.clear();
        this.isNumberedList = false;
        this.currentNumber = 1;

        return stringBuilder.toString();
    }

    private String nextNumber() {
        if(this.isNumberedList) {
            return ++currentNumber + ".";
        } else {
            return "*";
        }
    }

    public void isNumberedList(final boolean shouldBeNumbered) {
        this.isNumberedList = shouldBeNumbered;
    }
}
