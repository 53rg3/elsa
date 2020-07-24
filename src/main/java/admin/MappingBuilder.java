/*
 * Copyright 2014-2019 the original author or authors.
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.ElasticsearchException;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ResourceUtil;
import org.springframework.data.elasticsearch.core.completion.Completion;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Iterator;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.springframework.util.StringUtils.hasText;

/**
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Artur Konczak
 * @author Kevin Leturc
 * @author Alexander Volz
 * @author Dennis Maaß
 * @author Pavel Luhin
 * @author Mark Paluch
 * @author Sascha Woo
 * @author Nordine Bittich
 * @author Robert Gruendler
 * @author Petr Kukral
 * @author Peter-Josef Meisch
 * @author Xiao Yu
 */
public class MappingBuilder {

    private static final String FIELD_INDEX = "index";
    private static final String FIELD_PROPERTIES = "properties";
    @Deprecated
    private static final String FIELD_PARENT = "_parent";
    private static final String FIELD_CONTEXT_NAME = "name";
    private static final String FIELD_CONTEXT_TYPE = "type";
    private static final String FIELD_CONTEXT_PATH = "path";
    private static final String FIELD_CONTEXT_PRECISION = "precision";
    private static final String FIELD_DYNAMIC_TEMPLATES = "dynamic_templates";
    private static final String FIELD_PARAM_STORE = "store";
    private static final String FIELD_PARAM_TYPE = "type";
    private static final String FIELD_PARAM_INDEX_ANALYZER = "analyzer";
    private static final String FIELD_PARAM_SEARCH_ANALYZER = "search_analyzer";

    private static final String COMPLETION_PRESERVE_SEPARATORS = "preserve_separators";
    private static final String COMPLETION_PRESERVE_POSITION_INCREMENTS = "preserve_position_increments";
    private static final String COMPLETION_MAX_INPUT_LENGTH = "max_input_length";
    private static final String COMPLETION_CONTEXTS = "contexts";

    private static final String TYPE_DYNAMIC = "dynamic";
    private static final String TYPE_VALUE_KEYWORD = "keyword";
    private static final String TYPE_VALUE_GEO_POINT = "geo_point";
    private static final String TYPE_VALUE_COMPLETION = "completion";

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchRestTemplate.class);

    private final ElasticsearchConverter elasticsearchConverter;

    public MappingBuilder(final ElasticsearchConverter elasticsearchConverter) {
        this.elasticsearchConverter = elasticsearchConverter;
    }

    /**
     * builds the Elasticsearch mapping for the given clazz.
     *
     * @return JSON string
     * @throws ElasticsearchException on errors while building the mapping
     */
    public XContentBuilder buildPropertyMapping(final Class<?> clazz) throws ElasticsearchException {

        try {
            final ElasticsearchPersistentEntity<?> entity = this.elasticsearchConverter.getMappingContext()
                    .getRequiredPersistentEntity(clazz);

            final XContentBuilder builder = jsonBuilder().startObject();

            // Dynamic templates
            this.addDynamicTemplatesMapping(builder, entity);

            // Parent
            final String parentType = entity.getParentType();
            if (hasText(parentType)) {
                builder.startObject(FIELD_PARENT).field(FIELD_PARAM_TYPE, parentType).endObject();
            }

            this.mapEntity(builder, entity, true, "", false, FieldType.Auto, null, entity.findAnnotation(DynamicMapping.class));

            builder.endObject() // root object
                    .close();

            return builder;
        } catch (final MappingException | IOException e) {
            throw new ElasticsearchException("could not build mapping", e);
        }
    }

    private void mapEntity(final XContentBuilder builder, @Nullable final ElasticsearchPersistentEntity entity, final boolean isRootObject,
                           final String nestedObjectFieldName, final boolean nestedOrObjectField, final FieldType fieldType,
                           @Nullable final Field parentFieldAnnotation, @Nullable final DynamicMapping dynamicMapping) throws IOException {

        final boolean writeNestedProperties = !isRootObject && (this.isAnyPropertyAnnotatedWithField(entity) || nestedOrObjectField);
        if (writeNestedProperties) {

            final String type = nestedOrObjectField ? fieldType.toString().toLowerCase()
                    : FieldType.Object.toString().toLowerCase();
            builder.startObject(nestedObjectFieldName).field(MappingParametersX.FIELD_PARAM_TYPE, type);

            if (nestedOrObjectField && FieldType.Nested == fieldType && parentFieldAnnotation != null
                    && parentFieldAnnotation.includeInParent()) {
                builder.field("include_in_parent", parentFieldAnnotation.includeInParent());
            }
        }

        if (dynamicMapping != null) {
            builder.field(TYPE_DYNAMIC, dynamicMapping.value().name().toLowerCase());
        }

        builder.startObject(FIELD_PROPERTIES);

        if (entity != null) {
            entity.doWithProperties((PropertyHandler<ElasticsearchPersistentProperty>) property -> {
                try {
                    if (property.isAnnotationPresent(Transient.class) || this.isInIgnoreFields(property, parentFieldAnnotation)) {
                        return;
                    }

                    if (property.isSeqNoPrimaryTermProperty()) {
                        if (property.isAnnotationPresent(Field.class)) {
                            logger.warn("Property {} of {} is annotated for inclusion in mapping, but its type is " + //
                                            "SeqNoPrimaryTerm that is never mapped, so it is skipped", //
                                    property.getFieldName(), entity.getType());
                        }
                        return;
                    }

                    this.buildPropertyMapping(builder, isRootObject, property);
                } catch (final IOException e) {
                    logger.warn("error mapping property with name {}", property.getName(), e);
                }
            });
        }

        builder.endObject();

        if (writeNestedProperties) {
            builder.endObject();
        }

    }

    private void buildPropertyMapping(final XContentBuilder builder, final boolean isRootObject,
                                      final ElasticsearchPersistentProperty property) throws IOException {

        if (property.isAnnotationPresent(Mapping.class)) {

            final String mappingPath = property.getRequiredAnnotation(Mapping.class).mappingPath();
            if (!StringUtils.isEmpty(mappingPath)) {

                final ClassPathResource mappings = new ClassPathResource(mappingPath);
                if (mappings.exists()) {
                    builder.rawField(property.getFieldName(), mappings.getInputStream(), XContentType.JSON);
                    return;
                }
            }
        }

        if (this.isGeoPointProperty(property)) {
            this.applyGeoPointFieldMapping(builder, property);
            return;
        }

        final Field fieldAnnotation = property.findAnnotation(Field.class);
        final boolean isCompletionProperty = this.isCompletionProperty(property);
        final boolean isNestedOrObjectProperty = this.isNestedOrObjectProperty(property);

        if (!isCompletionProperty && property.isEntity() && this.hasRelevantAnnotation(property)) {

            if (fieldAnnotation == null) {
                return;
            }

            if (isNestedOrObjectProperty) {
                final Iterator<? extends TypeInformation<?>> iterator = property.getPersistentEntityTypes().iterator();
                final ElasticsearchPersistentEntity<?> persistentEntity = iterator.hasNext()
                        ? this.elasticsearchConverter.getMappingContext().getPersistentEntity(iterator.next())
                        : null;

                this.mapEntity(builder, persistentEntity, false, property.getFieldName(), isNestedOrObjectProperty,
                        fieldAnnotation.type(), fieldAnnotation, property.findAnnotation(DynamicMapping.class));
                return;
            }
        }

        final MultiField multiField = property.findAnnotation(MultiField.class);

        if (isCompletionProperty) {
            final CompletionField completionField = property.findAnnotation(CompletionField.class);
            this.applyCompletionFieldMapping(builder, property, completionField);
        }

        if (isRootObject && fieldAnnotation != null && property.isIdProperty()) {
            this.applyDefaultIdFieldMapping(builder, property);
        } else if (multiField != null) {
            this.addMultiFieldMapping(builder, property, multiField, isNestedOrObjectProperty);
        } else if (fieldAnnotation != null) {
            this.addSingleFieldMapping(builder, property, fieldAnnotation, isNestedOrObjectProperty);
        }
    }

    private boolean hasRelevantAnnotation(final ElasticsearchPersistentProperty property) {

        return property.findAnnotation(Field.class) != null || property.findAnnotation(MultiField.class) != null
                || property.findAnnotation(GeoPointField.class) != null
                || property.findAnnotation(CompletionField.class) != null;
    }

    private void applyGeoPointFieldMapping(final XContentBuilder builder, final ElasticsearchPersistentProperty property)
            throws IOException {

        builder.startObject(property.getFieldName()).field(FIELD_PARAM_TYPE, TYPE_VALUE_GEO_POINT).endObject();
    }

    private void applyCompletionFieldMapping(final XContentBuilder builder, final ElasticsearchPersistentProperty property,
                                             @Nullable final CompletionField annotation) throws IOException {

        builder.startObject(property.getFieldName());
        builder.field(FIELD_PARAM_TYPE, TYPE_VALUE_COMPLETION);

        if (annotation != null) {

            builder.field(COMPLETION_MAX_INPUT_LENGTH, annotation.maxInputLength());
            builder.field(COMPLETION_PRESERVE_POSITION_INCREMENTS, annotation.preservePositionIncrements());
            builder.field(COMPLETION_PRESERVE_SEPARATORS, annotation.preserveSeparators());
            if (!StringUtils.isEmpty(annotation.searchAnalyzer())) {
                builder.field(FIELD_PARAM_SEARCH_ANALYZER, annotation.searchAnalyzer());
            }
            if (!StringUtils.isEmpty(annotation.analyzer())) {
                builder.field(FIELD_PARAM_INDEX_ANALYZER, annotation.analyzer());
            }

            if (annotation.contexts().length > 0) {

                builder.startArray(COMPLETION_CONTEXTS);
                for (final CompletionContext context : annotation.contexts()) {

                    builder.startObject();
                    builder.field(FIELD_CONTEXT_NAME, context.name());
                    builder.field(FIELD_CONTEXT_TYPE, context.type().name().toLowerCase());

                    if (context.precision().length() > 0) {
                        builder.field(FIELD_CONTEXT_PRECISION, context.precision());
                    }

                    if (StringUtils.hasText(context.path())) {
                        builder.field(FIELD_CONTEXT_PATH, context.path());
                    }

                    builder.endObject();
                }
                builder.endArray();
            }

        }
        builder.endObject();
    }

    private void applyDefaultIdFieldMapping(final XContentBuilder builder, final ElasticsearchPersistentProperty property)
            throws IOException {

        builder.startObject(property.getFieldName()).field(FIELD_PARAM_TYPE, TYPE_VALUE_KEYWORD).field(FIELD_INDEX, true)
                .endObject();
    }

    /**
     * Add mapping for @Field annotation
     *
     * @throws IOException
     */
    private void addSingleFieldMapping(final XContentBuilder builder, final ElasticsearchPersistentProperty property,
                                       final Field annotation, final boolean nestedOrObjectField) throws IOException {

        // build the property json, if empty skip it as this is no valid mapping
        final XContentBuilder propertyBuilder = jsonBuilder().startObject();
        this.addFieldMappingParameters(propertyBuilder, annotation, nestedOrObjectField);
        propertyBuilder.endObject().close();

        if ("{}".equals(propertyBuilder.getOutputStream().toString())) {
            return;
        }

        builder.startObject(property.getFieldName());
        this.addFieldMappingParameters(builder, annotation, nestedOrObjectField);
        builder.endObject();
    }

    /**
     * Add mapping for @MultiField annotation
     *
     * @throws IOException
     */
    private void addMultiFieldMapping(final XContentBuilder builder, final ElasticsearchPersistentProperty property,
                                      final MultiField annotation, final boolean nestedOrObjectField) throws IOException {

        // main field
        builder.startObject(property.getFieldName());
        this.addFieldMappingParameters(builder, annotation.mainField(), nestedOrObjectField);

        // inner fields
        builder.startObject("fields");
        for (final InnerField innerField : annotation.otherFields()) {
            builder.startObject(innerField.suffix());
            this.addFieldMappingParameters(builder, innerField, false);
            builder.endObject();
        }
        builder.endObject();

        builder.endObject();
    }

    private void addFieldMappingParameters(final XContentBuilder builder, final Annotation annotation, final boolean nestedOrObjectField)
            throws IOException {

        final MappingParametersX mappingParameters = MappingParametersX.from(annotation);

        if (!nestedOrObjectField && mappingParameters.isStore()) {
            builder.field(FIELD_PARAM_STORE, mappingParameters.isStore());
        }
        mappingParameters.writeTypeAndParametersTo(builder);
    }

    /**
     * Apply mapping for dynamic templates.
     *
     * @throws IOException
     */
    private void addDynamicTemplatesMapping(final XContentBuilder builder, final ElasticsearchPersistentEntity<?> entity)
            throws IOException {

        if (entity.isAnnotationPresent(DynamicTemplates.class)) {
            final String mappingPath = entity.getRequiredAnnotation(DynamicTemplates.class).mappingPath();
            if (hasText(mappingPath)) {

                final String jsonString = ResourceUtil.readFileFromClasspath(mappingPath);
                if (hasText(jsonString)) {

                    final ObjectMapper objectMapper = new ObjectMapper();
                    final JsonNode jsonNode = objectMapper.readTree(jsonString).get("dynamic_templates");
                    if (jsonNode != null && jsonNode.isArray()) {
                        final String json = objectMapper.writeValueAsString(jsonNode);
                        builder.rawField(FIELD_DYNAMIC_TEMPLATES, new ByteArrayInputStream(json.getBytes()), XContentType.JSON);
                    }
                }
            }
        }
    }

    private boolean isAnyPropertyAnnotatedWithField(@Nullable final ElasticsearchPersistentEntity entity) {

        return entity != null && entity.getPersistentProperty(Field.class) != null;
    }

    private boolean isInIgnoreFields(final ElasticsearchPersistentProperty property, @Nullable final Field parentFieldAnnotation) {

        if (null != parentFieldAnnotation) {

            final String[] ignoreFields = parentFieldAnnotation.ignoreFields();
            return Arrays.asList(ignoreFields).contains(property.getFieldName());
        }
        return false;
    }

    private boolean isNestedOrObjectProperty(final ElasticsearchPersistentProperty property) {

        final Field fieldAnnotation = property.findAnnotation(Field.class);
        return fieldAnnotation != null
                && (FieldType.Nested == fieldAnnotation.type() || FieldType.Object == fieldAnnotation.type());
    }

    private boolean isGeoPointProperty(final ElasticsearchPersistentProperty property) {
        return property.getActualType() == GeoPoint.class || property.isAnnotationPresent(GeoPointField.class);
    }

    private boolean isCompletionProperty(final ElasticsearchPersistentProperty property) {
        return property.getActualType() == Completion.class;
    }
}
