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

import assets.TestModel;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ElsaModelTest {

    @Rule
    public TestName name = new TestName();

    @Test
    public void constructor_accessIndexData_pass() {
        TestModel testModel = new TestModel();
        assertThat(testModel.getIndexConfig().getIndexName(), is("elsa_test_index"));
        assertThat(testModel.getIndexConfig().getShards(), is(1));
        assertThat(testModel.getIndexConfig().getReplicas(), is(0));
    }

    @Test
    public void twoModelInstances_indexDataIsStatic_pass() {
        TestModel testModel1 = new TestModel();
        TestModel testModel2 = new TestModel();
        assertTrue(testModel1.getIndexConfig().equals(testModel2.getIndexConfig()));
    }

//    @Test
//    @Ignore("In ElsaModelTest: Causes problems with index creation because of parallel execution, since indexName is static. " +
//            "We test this in IndexAdminTest.java, which runs sequentially.")
//    public void changeIndexName_appliesToInstancesOfModel_pass() {
//        TestModel testModel1 = new TestModel();
//        TestModel testModel2 = new TestModel();
//        assertThat(testModel1.getIndexConfig().getIndexName(), is("elsa_test_index"));
//        testModel2.getIndexConfig().setIndexName("new_name");
//        assertThat(testModel1.getIndexConfig().getIndexName(), is("new_name"));
//    }
}
