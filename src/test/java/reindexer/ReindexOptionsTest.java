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

package reindexer;

import org.junit.Test;
import reindexer.ReindexOptions.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ReindexOptionsTest {
    
    @Test
    public void allOptions() {
        assertThat(OpType.CREATE.toString(), is("create"));
        assertThat(Conflicts.PROCEED.toString(), is("proceed"));
        assertThat(VersionType.EXTERNAL.toString(), is("external"));
        assertThat(VersionType.INTERNAL.toString(), is("internal"));
        assertThat(ScriptingLanguage.PAINLESS.toString(), is("painless"));
    }

}
