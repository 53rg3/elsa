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

import assets.FakerModel;
import assets.TestModel;
import assets.TestModelWithAddedMappings;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IndexNameTest {

    private final FakerModel fakerModel = new FakerModel();
    private final String expected = fakerModel.getIndexConfig().getIndexName();

    @Test
    public void of_WithModel() {
        assertThat(IndexName.of(fakerModel), is(expected));
    }

    @Test
    public void of_withClass() {
        assertThat(IndexName.of(FakerModel.class), is(expected));
    }
}
