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
import org.elasticsearch.common.xcontent.XContentBuilder;
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

    private final MappingBuilder mappingBuilder = new MappingContextCreator().getMappingBuilder();

    @Test
    public void testInfiniteLoopAvoidance() throws IOException {
        final String expected = "{\"properties\":{\"message\":{\"store\":true,\"type\":\"text\",\"index\":false,\"analyzer\":\"standard\"}}}";

        final XContentBuilder xContentBuilder = this.mappingBuilder.buildPropertyMapping(SampleTransientEntity.class);
        assertThat(Strings.toString(xContentBuilder), is(expected));
    }

    @Test
    public void shouldUseValueFromAnnotationType() throws IOException {
        //Given
        final String expected = "{\"properties\":{\"price\":{\"type\":\"double\"}}}";

        //When
        final XContentBuilder xContentBuilder = this.mappingBuilder.buildPropertyMapping(StockPrice.class);

        //Then
        assertThat(Strings.toString(xContentBuilder), is(expected));
    }

    @Test
    public void shouldCreateMappingForSpecifiedParentType() throws IOException {
        final String expected = "{\"properties\":{}}";
        final XContentBuilder xContentBuilder = this.mappingBuilder.buildPropertyMapping(MinimalEntity.class);
        assertThat(Strings.toString(xContentBuilder), is(expected));
    }

    @Test
    public void shouldBuildMappingWithSuperclass() throws IOException {
        final String expected = "{\"properties\":{\"message\":{\"store\":true,\"type\":\"text\",\"index\":false,\"analyzer\":\"standard\"},\"createdDate\":{\"type\":\"date\",\"format\":\"basic_date\",\"index\":false}}}";

        final XContentBuilder xContentBuilder = this.mappingBuilder.buildPropertyMapping(SampleInheritedEntity.class);
        assertThat(Strings.toString(xContentBuilder), is(expected));
    }

    @Test
    public void mappingBuilder_EntityHasNoDocumentAnnotation_mustPass() throws IOException {
        final XContentBuilder xContentBuilder = this.mappingBuilder.buildPropertyMapping(StockPrice.class);
    }

    @Test
    public void shouldBuildMappingsForGeoPoint() throws IOException {
        //given

        //when
        final XContentBuilder xContentBuilder = this.mappingBuilder.buildPropertyMapping(GeoEntity.class);

        //then
        final String result = Strings.toString(xContentBuilder);

        assertThat(result, containsString("\"pointA\":{\"type\":\"geo_point\""));
        assertThat(result, containsString("\"pointB\":{\"type\":\"geo_point\""));
        assertThat(result, containsString("\"pointC\":{\"type\":\"geo_point\""));
        assertThat(result, containsString("\"pointD\":{\"type\":\"geo_point\""));
    }
}
