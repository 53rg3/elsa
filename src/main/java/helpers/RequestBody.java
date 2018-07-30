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

package helpers;

import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import statics.Messages.ExceptionMsg;
import statics.ElsaStatics;

import java.io.IOException;

public class RequestBody {
    private RequestBody() {}

    private static final Logger logger = LoggerFactory.getLogger(RequestBody.class);


    public static NStringEntity asJson(final XContentBuilder xContentBuilder) {
        return new NStringEntity(Strings.toString(xContentBuilder), ContentType.APPLICATION_JSON);
    }

    public static NStringEntity asJson(final Object object) {
        return new NStringEntity(ElsaStatics.GSON.toJson(object), ContentType.APPLICATION_JSON);
    }

    public static NStringEntity asJson(final XJson xJson) {
        return new NStringEntity(xJson.toJson(), ContentType.APPLICATION_JSON);
    }

}
