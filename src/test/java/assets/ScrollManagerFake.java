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

package assets;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.search.Scroll;
import scroller.ScrollManager;

import java.util.Objects;

public class ScrollManagerFake extends ScrollManager {

    private final Scroll scroll;
    private String scrollId;

    public ScrollManagerFake(TimeValue timeout) {
        super(timeout);
        this.scroll = new Scroll(timeout);
    }

    public void updateScrollId(SearchResponse searchResponse) {
        Objects.requireNonNull(searchResponse.getScrollId());
        this.scrollId = "non_existing_id";
    }

    public Scroll getScroll() {
        return scroll;
    }

    public String getScrollId() {
        return scrollId;
    }
}
