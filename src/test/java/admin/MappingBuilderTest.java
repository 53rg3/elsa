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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import java.io.IOException;

import admin.entities.*;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.junit.Test;


/**
 * @author Stuart Stevenson
 * @author Jakub Vavrik
 * @author Mohsin Husen
 * @author Keivn Leturc
 */

public class MappingBuilderTest {

	@Test
	public void testInfiniteLoopAvoidance() throws IOException {
		final String expected = "{\"mapping\":{\"properties\":{\"message\":{\"store\":true,\"" +
				"type\":\"text\",\"index\":false," +
				"\"analyzer\":\"standard\"}}}}";

		XContentBuilder xContentBuilder = MappingBuilder.buildMapping(SampleTransientEntity.class, "mapping", "id", null);
		assertThat(xContentBuilder.string(), is(expected));
	}

	@Test
	public void shouldUseValueFromAnnotationType() throws IOException {
		//Given
		final String expected = "{\"mapping\":{\"properties\":{\"price\":{\"store\":false,\"type\":\"double\"}}}}";

		//When
		XContentBuilder xContentBuilder = MappingBuilder.buildMapping(StockPrice.class, "mapping", "id", null);

		//Then
		assertThat(xContentBuilder.string(), is(expected));
	}

	@Test
	public void shouldCreateMappingForSpecifiedParentType() throws IOException {
		final String expected = "{\"mapping\":{\"_parent\":{\"type\":\"parentType\"},\"properties\":{}}}";
		XContentBuilder xContentBuilder = MappingBuilder.buildMapping(MinimalEntity.class, "mapping", "id", "parentType");
		assertThat(xContentBuilder.string(), is(expected));
	}

	@Test
	public void shouldBuildMappingWithSuperclass() throws IOException {
		final String expected = "{\"mapping\":{\"properties\":{\"message\":{\"store\":true,\"" +
				"type\":\"text\",\"index\":false,\"analyzer\":\"standard\"}" +
				",\"createdDate\":{\"store\":false," +
				"\"type\":\"date\",\"index\":false}}}}";

		XContentBuilder xContentBuilder = MappingBuilder.buildMapping(SampleInheritedEntity.class, "mapping", "id", null);
		assertThat(xContentBuilder.string(), is(expected));
	}

	@Test
	public void mappingBuilder_EntityHasNoDocumentAnnotation_mustPass() throws IOException {
		XContentBuilder xContentBuilder = MappingBuilder.buildMapping(StockPrice.class, "mapping", "id", "parentType");
	}

	@Test
	public void shouldBuildMappingsForGeoPoint() throws IOException {
		//given

		//when
		XContentBuilder xContentBuilder = MappingBuilder.buildMapping(GeoEntity.class, "mapping", "id", null);

		//then
		final String result = xContentBuilder.string();

		assertThat(result, containsString("\"pointA\":{\"type\":\"geo_point\""));
		assertThat(result, containsString("\"pointB\":{\"type\":\"geo_point\""));
		assertThat(result, containsString("\"pointC\":{\"type\":\"geo_point\""));
		assertThat(result, containsString("\"pointD\":{\"type\":\"geo_point\""));
	}
}
