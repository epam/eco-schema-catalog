/*
 * Copyright 2019 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.epam.eco.schemacatalog.rest.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.avro.JsonProperties;
import org.apache.avro.Schema;
import org.apache.commons.lang3.Validate;

import com.epam.eco.commons.avro.AvroUtils;
import com.epam.eco.commons.avro.FieldInfo;
import com.epam.eco.schemacatalog.domain.metadata.Metadata;
import com.epam.eco.schemacatalog.domain.metadata.MetadataBrowser;
import com.epam.eco.schemacatalog.domain.metadata.format.PartFormatter;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.rest.view.FormattedMetadata;
import com.epam.eco.schemacatalog.rest.view.NamedSchemaFieldType;
import com.epam.eco.schemacatalog.rest.view.ParameterizedSchemaFieldType;
import com.epam.eco.schemacatalog.rest.view.PrimitiveSchemaFieldType;
import com.epam.eco.schemacatalog.rest.view.SchemaEntity;
import com.epam.eco.schemacatalog.rest.view.SchemaField;
import com.epam.eco.schemacatalog.rest.view.SchemaFieldType;
import com.epam.eco.schemacatalog.rest.view.SchemaProfile;

/**
 * @author Raman_Babich
 */
public class SchemaProfileCreator {

    private static final SchemaFieldType STRING_FIELD_TYPE = PrimitiveSchemaFieldType.builder()
            .type(Schema.Type.STRING)
            .build();

    public SchemaProfile createSchemaProfile(FullSchemaInfo schemaInfo, PartFormatter metadataDocFormatter) {
        Validate.notNull(schemaInfo, "Schema info can't be null");

        Optional<Metadata> schemaMetadata = schemaInfo.getMetadataBrowser().getSchemaMetadata();

        return SchemaProfile.builder()
                .subject(schemaInfo.getSubject())
                .version(schemaInfo.getVersion())
                .schemaRegistryId(schemaInfo.getSchemaRegistryId())
                .compatibilityLevel(schemaInfo.getCompatibilityLevel())
                .mode(schemaInfo.getMode())
                .versionLatest(schemaInfo.isVersionLatest())
                .schemaMetadata(schemaMetadata.map(meta -> FormattedMetadata.with(meta, metadataDocFormatter))
                        .orElse(null))
                .schemas(getAllSchemaEntities(schemaInfo, metadataDocFormatter))
                .deleted(schemaInfo.isDeleted())
                .build();
    }

    private Set<SchemaEntity> getAllSchemaEntities(FullSchemaInfo schemaInfo, PartFormatter formatter) {
        Validate.notNull(schemaInfo, "Schema info can't be null");

        MetadataBrowser<FullSchemaInfo> metadataBrowser = schemaInfo.getMetadataBrowser();

        Map<String, SchemaEntity.Builder> builders = new HashMap<>();
        List<FieldInfo> fieldInfoList = schemaInfo.getSchemaFieldInfosAsList();
        for(FieldInfo fieldInfo : fieldInfoList) {
            Schema parent = fieldInfo.getParent();
            Schema.Field field = fieldInfo.getField();
            SchemaEntity.Builder builder = builders.get(parent.getFullName());
            if (builder == null) {
                builder = new SchemaEntity.Builder();
                builder.name(parent.getName()).namespace(parent.getNamespace());
                if (schemaInfo.getSchemaAvro().equals(parent)) {
                    builder.root(true);
                }
                builders.put(parent.getFullName(), builder);
            }
            builder.addField(SchemaField.builder()
                    .name(field.name())
                    .type(constructSchemaFieldType(parent, field.schema()))
                    .defaultValue(jsonNullToJava(field.defaultVal()))
                    .defaultValuePresent(field.defaultVal() != null)
                    .nativeDoc(field.doc())
                    .metadata(
                            FormattedMetadata.with(
                                    metadataBrowser.
                                        getFieldMetadata(parent.getFullName(), field.name()).orElse(null),
                                        formatter))
                    .build());
        }

        return builders.values().stream()
                .map(SchemaEntity.Builder::build)
                .collect(Collectors.toSet());
    }

    private SchemaFieldType constructSchemaFieldType(Schema parentSchema, Schema fieldSchema) {
        Schema.Type type = fieldSchema.getType();
        String logicalType =
                fieldSchema.getLogicalType() != null ?
                fieldSchema.getLogicalType().getName() :
                null;

        if (AvroUtils.isPrimitive(type)) {
            return PrimitiveSchemaFieldType.builder()
                    .type(type)
                    .logicalType(logicalType)
                    .build();
        }
        if (AvroUtils.isNamed(type)) {
            return NamedSchemaFieldType.builder()
                    .type(type)
                    .logicalType(logicalType)
                    .namespace(Objects.equals(
                            fieldSchema.getNamespace(),
                            parentSchema.getNamespace()) ? null : fieldSchema.getNamespace())
                    .name(fieldSchema.getName())
                    .build();
        }
        if (AvroUtils.isParametrized(type)) {
            if (type == Schema.Type.ARRAY) {
                return ParameterizedSchemaFieldType.builder()
                        .type(type)
                        .logicalType(logicalType)
                        .parameters(
                                Collections.singletonList(
                                        constructSchemaFieldType(parentSchema, fieldSchema.getElementType())))
                        .build();
            }
            if (type == Schema.Type.MAP) {
                List<SchemaFieldType> parameters = new ArrayList<>();
                parameters.add(STRING_FIELD_TYPE);
                parameters.add(constructSchemaFieldType(parentSchema, fieldSchema.getValueType()));
                return ParameterizedSchemaFieldType.builder()
                        .type(type)
                        .logicalType(logicalType)
                        .parameters(parameters)
                        .build();
            }
            if (type == Schema.Type.UNION) {
                List<Schema> types = fieldSchema.getTypes();
                List<SchemaFieldType> parameters = new ArrayList<>(types.size());
                for (Schema unionSchema : types) {
                    parameters.add(constructSchemaFieldType(parentSchema, unionSchema));
                }
                return ParameterizedSchemaFieldType.builder()
                        .type(type)
                        .logicalType(logicalType)
                        .parameters(parameters)
                        .build();
            }
        }
        throw new IllegalArgumentException("Schema type is unknown");
    }

    private Object jsonNullToJava(Object obj) {
        return obj == JsonProperties.NULL_VALUE ? null : obj;
    }

}