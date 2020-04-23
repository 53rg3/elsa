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

import com.github.javafaker.Faker;
import model.ElsaModel;
import model.IndexConfig;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DateModel implements ElsaModel {

    public static IndexConfig indexConfig = new IndexConfig(c->c
            .indexName("elsa_gson_test")
            .shards(1)
            .replicas(0)
            .refreshInterval(TimeValue.timeValueSeconds(1)));

    @Id
    private transient String id;

    @Field(type = FieldType.Date)
    private Date date;

    @Override
    public IndexConfig getIndexConfig() {
        return indexConfig;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
