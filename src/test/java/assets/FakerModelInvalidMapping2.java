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

import com.github.javafaker.Faker;
import model.ElsaModel;
import model.IndexConfig;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FakerModelInvalidMapping2 implements ElsaModel {

    private static final Faker faker = new Faker();

    private static IndexConfig indexConfig = new IndexConfig(c->c
            .indexName("elsa_bulk_test")
            .shards(1)
            .replicas(0)
            .allowDynamicIndexNaming(true)
            .refreshInterval(TimeValue.timeValueSeconds(1)));

    @Id
    private transient String id;

    @Field(type = FieldType.text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.text)
    private List<String> favoritePokemons;

    @Field(type = FieldType.Integer)
    private List<String> age;

    @Field(type = FieldType.text)
    private String biography;


    public static FakerModelInvalidMapping2 createModelWithRandomData() {
        final FakerModelInvalidMapping2 bulkModel = new FakerModelInvalidMapping2();

        bulkModel.setName(faker.name().fullName());
        bulkModel.setFavoritePokemons(Arrays.asList(faker.pokemon().name(), faker.pokemon().name(), faker.pokemon().name()));
        bulkModel.setAge(Arrays.asList("qwer", "asdf", "yxcv"));
        bulkModel.setBiography(faker.lorem().sentence(createRandomInteger(50, 100)));
        return bulkModel;
    }

    private static int createRandomInteger(final int min, final int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static String getIndexName() {
        return indexConfig.getIndexName();
    }

    @Override
    public IndexConfig getIndexConfig() {
        return indexConfig;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public List<String> getFavoritePokemons() {
        return favoritePokemons;
    }

    public List<String> getAge() {
        return age;
    }

    public String getBiography() {
        return biography;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFavoritePokemons(List<String> favoritePokemons) {
        this.favoritePokemons = favoritePokemons;
    }

    public void setAge(List<String> age) {
        this.age = age;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Override
    public String toString() {
        return "FakerModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", favoritePokemons=" + favoritePokemons +
                ", age=" + age +
                ", biography='" + biography + '\'' +
                '}';
    }
}
