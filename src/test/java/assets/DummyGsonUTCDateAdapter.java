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

import com.google.gson.*;
import statics.ElsaStatics;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DummyGsonUTCDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    private final DateFormat UTC_FORMAT;

    public DummyGsonUTCDateAdapter(String format, Locale locale, String timezone) {
        UTC_FORMAT = new SimpleDateFormat(format, locale);
        UTC_FORMAT.setTimeZone(TimeZone.getTimeZone(timezone));
    }

    @Override
    public synchronized JsonElement serialize(final Date date, final Type type, final JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(this.UTC_FORMAT.format(date));

    }

    @Override
    public synchronized Date deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) {
        try {
            return this.UTC_FORMAT.parse(jsonElement.getAsString());
        } catch (final ParseException e) {
            throw new JsonParseException(e);
        }
    }

}
