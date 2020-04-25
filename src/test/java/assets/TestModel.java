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

import model.ElsaModel;
import model.IndexConfig;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

public class TestModel implements ElsaModel {

    public static IndexConfig indexConfig = new IndexConfig(c -> c
            .indexName("elsa_test_index")
            .mappingClass(TestModel.class)
            .shards(1)
            .replicas(0)
            .refreshInterval(TimeValue.timeValueSeconds(1)));

    @Id
    private transient String id;

    @Field(type = FieldType.Text)
    private String stringField;

    @Field(type = FieldType.Integer)
    private int integerField;

    @Field(type = FieldType.Text)
    private List<String> arrayField;

    private transient String transientField;

    @Override
    public String toString() {
        return "TestModel{" +
                "id='" + this.id + '\'' +
                ", stringField='" + this.stringField + '\'' +
                ", integerField=" + this.integerField +
                ", arrayField=" + this.arrayField +
                ", transientField='" + this.transientField + '\'' +
                '}';
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(final String id) {
        this.id = id;
    }

    public String getStringField() {
        return this.stringField;
    }

    public void setStringField(final String stringField) {
        this.stringField = stringField;
    }

    public int getIntegerField() {
        return this.integerField;
    }

    public void setIntegerField(final int integerField) {
        this.integerField = integerField;
    }

    public List<String> getArrayField() {
        return this.arrayField;
    }

    public void setArrayField(final List<String> arrayField) {
        this.arrayField = arrayField;
    }

    public String getTransientField() {
        return this.transientField;
    }

    public void setTransientField(final String transientField) {
        this.transientField = transientField;
    }
}
