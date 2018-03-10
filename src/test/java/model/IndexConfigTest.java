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

package model;

import org.junit.Test;

public class IndexConfigTest {

    @Test(expected = IllegalStateException.class)
    public void elsaIndexDataBuilder_indexNameNotSet_throw() {
        new IndexConfig(c->c
                .indexName(null)
                .shards(1)
                .replicas(1));
    }

    @Test(expected = IllegalStateException.class)
    public void elsaIndexDataBuilder_indexNameEmpty_throw() {
        new IndexConfig(c->c
                .indexName("")
                .shards(1)
                .replicas(1));
    }

    @Test(expected = IllegalStateException.class)
    public void elsaIndexDataBuilder_shardsIsZero_throw() {
        new IndexConfig(c->c
                .indexName("yxcv")
                .shards(0)
                .replicas(1));
    }

    @Test(expected = IllegalStateException.class)
    public void elsaIndexDataBuilder_replicasIsNegative_throw() {
        new IndexConfig(c->c
                .indexName("yxcv")
                .shards(1)
                .replicas(-1));
    }

}
