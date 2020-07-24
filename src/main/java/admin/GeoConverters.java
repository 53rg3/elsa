package admin;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.geo.Point;
import org.springframework.util.NumberUtils;

/**
 * Set of {@link Converter converters} specific to Elasticsearch Geo types.
 *
 * @author Christoph Strobl
 * @author Peter-Josef Meisch
 * @since 3.2
 */
class GeoConverters {

	static Collection<Converter<?, ?>> getConvertersToRegister() {

		return Arrays.asList(PointToMapConverter.INSTANCE, MapToPointConverter.INSTANCE, GeoPointToMapConverter.INSTANCE,
				MapToGeoPointConverter.INSTANCE);
	}

	/**
	 * {@link Converter} to write a {@link Point} to {@link Map} using {@code lat/long} properties.
	 */
	@WritingConverter
	enum PointToMapConverter implements Converter<Point, Map<String, Object>> {

		INSTANCE;

		@Override
		public Map<String, Object> convert(Point source) {

			Map<String, Object> target = new LinkedHashMap<>();
			target.put("lat", source.getX());
			target.put("lon", source.getY());
			return target;
		}
	}

	/**
	 * {@link Converter} to write a {@link GeoPoint} to {@link Map} using {@code lat/long} properties.
	 */
	@WritingConverter
	enum GeoPointToMapConverter implements Converter<GeoPoint, Map<String, Object>> {

		INSTANCE;

		@Override
		public Map<String, Object> convert(GeoPoint source) {
			Map<String, Object> target = new LinkedHashMap<>();
			target.put("lat", source.getLat());
			target.put("lon", source.getLon());
			return target;
		}
	}

	/**
	 * {@link Converter} to read a {@link Point} from {@link Map} using {@code lat/long} properties.
	 */
	@ReadingConverter
	enum MapToPointConverter implements Converter<Map<String, Object>, Point> {

		INSTANCE;

		@Override
		public Point convert(Map<String, Object> source) {
			Double x = NumberUtils.convertNumberToTargetClass((Number) source.get("lat"), Double.class);
			Double y = NumberUtils.convertNumberToTargetClass((Number) source.get("lon"), Double.class);

			return new Point(x, y);
		}
	}

	/**
	 * {@link Converter} to read a {@link GeoPoint} from {@link Map} using {@code lat/long} properties.
	 */
	@ReadingConverter
	enum MapToGeoPointConverter implements Converter<Map<String, Object>, GeoPoint> {

		INSTANCE;

		@Override
		public GeoPoint convert(Map<String, Object> source) {
			Double x = NumberUtils.convertNumberToTargetClass((Number) source.get("lat"), Double.class);
			Double y = NumberUtils.convertNumberToTargetClass((Number) source.get("lon"), Double.class);

			return new GeoPoint(x, y);
		}
	}
}
