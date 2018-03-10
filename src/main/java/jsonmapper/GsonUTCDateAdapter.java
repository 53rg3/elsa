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

package jsonmapper;

import com.google.gson.*;
import statics.ElsaStatics;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;

public class GsonUTCDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    public GsonUTCDateAdapter() {
        // NO OP
    }

    @Override
    public synchronized JsonElement serialize(final Date date, final Type type, final JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(ElsaStatics.UTC_FORMAT.format(date));
    }

    @Override
    public synchronized Date deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) {
        try {
            return ElsaStatics.UTC_FORMAT.parse(jsonElement.getAsString());
        } catch (final ParseException e) {
            throw new JsonParseException(e);
        }
    }

}
