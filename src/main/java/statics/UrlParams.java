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

package statics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class UrlParams {

    public static final Map<String,String> NONE = Collections.emptyMap();
    public static final Map<String,String> WAIT_FOR_COMPLETION_TRUE = createMap("wait_for_completion", "true");
    public static final Map<String,String> WAIT_FOR_COMPLETION_FALSE = createMap("wait_for_completion", "false");
    // TODO Maybe this needs a builder to concat multiple params?

    private static Map<String,String> createMap(final String key, final String value) {
        final Map<String,String> map = new HashMap<>();
        map.putIfAbsent(key, value);
        return map;
    }

}
