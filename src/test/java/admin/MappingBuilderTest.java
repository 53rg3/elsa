/*
 * Copyright 2013-2016 the original author or authors.
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

package admin;

import admin.entities.*;
import org.elasticsearch.common.Strings;
import org.elasticsearch.xcontent.XContentBuilder;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


/**
 * @author Stuart Stevenson
 * @author Jakub Vavrik
 * @author Mohsin Husen
 * @author Keivn Leturc
 */

public class MappingBuilderTest {

    private final MappingBuilder mappingBuilder = new MappingBuilder();

    @Test
    public void testInfiniteLoopAvoidance() throws IOException {
        final String expected = "{\"properties\":{\"_class\":{\"type\":\"keyword\",\"index\":false,\"doc_values\":false},\"message\":{\"store\":true,\"type\":\"text\",\"index\":false,\"analyzer\":\"standard\"}}}";

        final String mapping = this.mappingBuilder.createMapping(SampleTransientEntity.class);
        assertThat(mapping, is(expected));
    }

    @Test
    public void shouldUseValueFromAnnotationType() throws IOException {
        //Given
        final String expected = "{\"properties\":{\"_class\":{\"type\":\"keyword\",\"index\":false,\"doc_values\":false},\"price\":{\"type\":\"double\"}}}";

        //When
        final String mapping = this.mappingBuilder.createMapping(StockPrice.class);

        //Then
        assertThat(mapping, is(expected));
    }

    @Test
    public void shouldCreateMappingForSpecifiedParentType() throws IOException {
        final String expected = "{\"properties\":{\"_class\":{\"type\":\"keyword\",\"index\":false,\"doc_values\":false}}}";
        final String mapping = this.mappingBuilder.createMapping(MinimalEntity.class);
        assertThat(mapping, is(expected));
    }

    @Test
    public void shouldBuildMappingWithSuperclass() throws IOException {
        final String expected = "{\"properties\":{\"_class\":{\"type\":\"keyword\",\"index\":false,\"doc_values\":false},\"message\":{\"store\":true,\"type\":\"text\",\"index\":false,\"analyzer\":\"standard\"},\"createdDate\":{\"type\":\"date\",\"format\":\"basic_date\",\"index\":false}}}";

        final String mapping = this.mappingBuilder.createMapping(SampleInheritedEntity.class);
        assertThat(mapping, is(expected));
    }

    @Test
    public void mappingBuilder_EntityHasNoDocumentAnnotation_mustPass() throws IOException {
        final String mapping = this.mappingBuilder.createMapping(StockPrice.class);
    }

    @Test
    public void shouldBuildMappingsForGeoPoint() throws IOException {
        //given

        //when
        final String result = this.mappingBuilder.createMapping(GeoEntity.class);

        //then
        assertThat(result, containsString("\"pointA\":{\"type\":\"geo_point\""));
        assertThat(result, containsString("\"pointB\":{\"type\":\"geo_point\""));
        assertThat(result, containsString("\"pointC\":{\"type\":\"geo_point\""));
        assertThat(result, containsString("\"pointD\":{\"type\":\"geo_point\""));
    }
}
