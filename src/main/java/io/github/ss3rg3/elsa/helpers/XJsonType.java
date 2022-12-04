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

package io.github.ss3rg3.elsa.helpers;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;

public class XJsonType {
    private XJsonType() {}

    public static final Type mapStringObjectType = new TypeToken<Map<String, Object>>(){}.getType();
    public static final Type arrayType = new TypeToken<Map<String, Object>[]>(){}.getType();
    public static final Type setType = new TypeToken<HashSet<Object>[]>(){}.getType();

}
