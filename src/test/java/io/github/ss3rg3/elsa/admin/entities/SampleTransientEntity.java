/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.ss3rg3.elsa.admin.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.lang.Nullable;

import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

/**
 * @author Jakub Vavrik
 */
@Document(indexName = "test-index-recursive-mapping-mapping-builder", replicas = 0, refreshInterval = "-1")
public class SampleTransientEntity {

    @Nullable
    @Id
    private String id;

    @Nullable
    @Field(type = Text, index = false, store = true, analyzer = "standard")
    private String message;

    @Nullable
    @Transient
    private SampleTransientEntity.NestedEntity nested;

    @Nullable
    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @Nullable
    public String getMessage() {
        return this.message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    static class NestedEntity {

        @Field
        private static SampleTransientEntity.NestedEntity someField = new SampleTransientEntity.NestedEntity();
        @Nullable
        @Field
        private Boolean something;

        public SampleTransientEntity.NestedEntity getSomeField() {
            return someField;
        }

        public void setSomeField(final SampleTransientEntity.NestedEntity someField) {
            NestedEntity.someField = someField;
        }

        @Nullable
        public Boolean getSomething() {
            return this.something;
        }

        public void setSomething(final Boolean something) {
            this.something = something;
        }
    }
}
