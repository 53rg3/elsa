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
import model.IndexConfig;
import model.ElsaModel;
import org.elasticsearch.core.TimeValue;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FakerModel implements ElsaModel {

    private static final Faker faker = new Faker();

    public static IndexConfig indexConfig = new IndexConfig(c->c
            .indexName("elsa_bulk_test")
            .mappingClass(FakerModel.class)
            .shards(1)
            .replicas(0)
            .refreshInterval(TimeValue.timeValueSeconds(1)));

    @Id
    private transient String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text)
    private List<String> favoritePokemons;

    @Field(type = FieldType.Integer)
    private int age;

    @Field(type = FieldType.Text)
    private String biography;


    public static FakerModel createModelWithRandomData() {
        final FakerModel fakerModel = new FakerModel();
        fakerModel.setName(faker.name().fullName());
        fakerModel.setFavoritePokemons(Arrays.asList(faker.pokemon().name(), faker.pokemon().name(), faker.pokemon().name()));
        fakerModel.setAge(createRandomInteger(14, 90));
        fakerModel.setBiography(faker.lorem().sentence(createRandomInteger(50, 100)));
        return fakerModel;
    }

    private static int createRandomInteger(final int min, final int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static String getIndexName() {
        return indexConfig.getIndexName();
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

    public int getAge() {
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

    public void setAge(int age) {
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
