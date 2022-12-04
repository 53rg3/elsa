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

package io.github.ss3rg3.elsa.admin.entities;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;

@Document(indexName = "test-index-geo", shards = 1, replicas = 0, refreshInterval = "-1")
public class GeoEntity {

	@Id
	private String id;

	//geo shape - Spring Data
	private Box box;
	private Circle circle;
	private Polygon polygon;

	//geo point - Custom implementation + Spring Data
	@GeoPointField
	private Point pointA;

	private GeoPoint pointB;

	@GeoPointField
	private String pointC;

	@GeoPointField
	private double[] pointD;
}
