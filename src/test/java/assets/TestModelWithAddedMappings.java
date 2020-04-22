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

import model.IndexConfig;
import model.ElsaModel;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

public class TestModelWithAddedMappings implements ElsaModel {

    public static IndexConfig indexConfig = new IndexConfig(c->c
            .indexName("elsa_test_index")
            .shards(1)
            .replicas(0)
            .refreshInterval(TimeValue.timeValueSeconds(1)));

    private String id;

    @Field(type = FieldType.Text)
    private String stringField;

    @Field(type = FieldType.Integer)
    private int integerField;

    @Field(type = FieldType.Text)
    private List<String> arrayField;

    private transient String transientField;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String addedField;

    @Field(type = FieldType.Nested)
    private TestSubType addedNestedObject;

    @Override
    public String toString() {
        return "TestModel{" +
                "id='" + id + '\'' +
                ", stringField='" + stringField + '\'' +
                ", integerField=" + integerField +
                ", arrayField=" + arrayField +
                ", transientField='" + transientField + '\'' +
                '}';
    }

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

}
