/*
 * Copyright 2020 EPAM Systems
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

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.epam.eco.schemacatalog.domain.metadata.FieldMetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;
import com.epam.eco.schemacatalog.domain.metadata.format.HtmlPartFormatter;
import com.epam.eco.schemacatalog.domain.schema.Mode;
import com.epam.eco.schemacatalog.rest.view.FormattedMetadata;
import com.epam.eco.schemacatalog.rest.view.NamedSchemaFieldType;
import com.epam.eco.schemacatalog.rest.view.ParameterizedSchemaFieldType;
import com.epam.eco.schemacatalog.rest.view.PrimitiveSchemaFieldType;
import com.epam.eco.schemacatalog.rest.view.SchemaEntity;
import com.epam.eco.schemacatalog.rest.view.SchemaField;
import com.epam.eco.schemacatalog.rest.view.SchemaProfile;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Raman_Babich
 */
class SchemaProfileCreatorTest {

    private SchemaProfileCreator profileCreator;

    @BeforeEach
    public void setUp() {
        profileCreator = new SchemaProfileCreator();
    }

    @Test
    void testCreateSchemaProfile() {
        Date now = new Date();

        SchemaProfile profile = profileCreator.createSchemaProfile(
                SchemaInfoTestData.sample(now),
                HtmlPartFormatter.INSTANCE);

        Map<MetadataKey, MetadataValue> fieldMetadata = SchemaInfoTestData.getFieldMetadata(now);
        Map<MetadataKey, FormattedMetadata> formattedMetadata = fieldMetadata.entrySet().stream()
                .map(entry -> FormattedMetadata.with(entry.getKey(), entry.getValue(), HtmlPartFormatter.INSTANCE))
                .collect(Collectors.toMap(FormattedMetadata::getKey, entry -> entry));

        Set<SchemaEntity> schemaEntities = new HashSet<>();
        schemaEntities.add(SchemaEntity.builder()
                .name("TestPerson")
                .namespace("com.epam.eco.schemaregistry.client.avro.data")
                .addField(SchemaField.builder()
                        .name("age")
                        .type(ParameterizedSchemaFieldType.builder()
                                .type(Schema.Type.UNION)
                                .addParameter(PrimitiveSchemaFieldType.builder().type(Schema.Type.NULL).build())
                                .addParameter(PrimitiveSchemaFieldType.builder().type(Schema.Type.INT).build())
                                .build())
                        .defaultValue(null)
                        .nativeDoc(null)
                        .metadata(formattedMetadata.get(FieldMetadataKey.with(
                                SchemaInfoTestData.SUBJECT,
                                SchemaInfoTestData.VERSION,
                                "com.epam.eco.schemaregistry.client.avro.data.TestPerson",
                                "age")))
                        .build())
                .addField(SchemaField.builder()
                        .name("hobby")
                        .type(ParameterizedSchemaFieldType.builder()
                                .type(Schema.Type.UNION)
                                .addParameter(PrimitiveSchemaFieldType.builder().type(Schema.Type.NULL).build())
                                .addParameter(ParameterizedSchemaFieldType.builder()
                                        .type(Schema.Type.ARRAY)
                                        .addParameter(NamedSchemaFieldType.builder()
                                                .namespace(null)
                                                .name("TestHobby")
                                                .type(Schema.Type.RECORD)
                                                .build())
                                        .build())
                                .build())
                        .defaultValue(null)
                        .nativeDoc(null)
                        .metadata(formattedMetadata.get(FieldMetadataKey.with(
                                SchemaInfoTestData.SUBJECT,
                                SchemaInfoTestData.VERSION,
                                "com.epam.eco.schemaregistry.client.avro.data.TestPerson",
                                "hobby")))
                        .build())
                .addField(SchemaField.builder()
                        .name("job")
                        .type(NamedSchemaFieldType.builder()
                                .namespace(null)
                                .name("TestJob")
                                .type(Schema.Type.RECORD)
                                .build())
                        .defaultValue(null)
                        .nativeDoc(null)
                        .metadata(formattedMetadata.get(FieldMetadataKey.with(
                                SchemaInfoTestData.SUBJECT,
                                SchemaInfoTestData.VERSION,
                                "com.epam.eco.schemaregistry.client.avro.data.TestPerson",
                                "job")))
                        .build())
                .root(true)
                .build());
        schemaEntities.add(SchemaEntity.builder()
                .name("TestHobby")
                .namespace("com.epam.eco.schemaregistry.client.avro.data")
                .addField(SchemaField.builder()
                        .name("kind")
                        .type(PrimitiveSchemaFieldType.builder()
                                .type(Schema.Type.STRING)
                                .build())
                        .defaultValue(null)
                        .nativeDoc(null)
                        .metadata(formattedMetadata.get(FieldMetadataKey.with(
                                SchemaInfoTestData.SUBJECT,
                                SchemaInfoTestData.VERSION,
                                "com.epam.eco.schemaregistry.client.avro.data.TestHobby",
                                "kind")))
                        .build())
                .addField(SchemaField.builder()
                        .name("description")
                        .type(PrimitiveSchemaFieldType.builder()
                                .type(Schema.Type.STRING)
                                .build())
                        .defaultValue("")
                        .defaultValuePresent(true)
                        .nativeDoc(null)
                        .metadata(formattedMetadata.get(FieldMetadataKey.with(
                                SchemaInfoTestData.SUBJECT,
                                SchemaInfoTestData.VERSION,
                                "com.epam.eco.schemaregistry.client.avro.data.TestHobby",
                                "description")))
                        .build())
                .root(false)
                .build());
        schemaEntities.add(SchemaEntity.builder()
                .name("TestJob")
                .namespace("com.epam.eco.schemaregistry.client.avro.data")
                .addField(SchemaField.builder()
                        .name("position")
                        .type(NamedSchemaFieldType.builder()
                                .namespace(null)
                                .name("TestPosition")
                                .type(Schema.Type.RECORD)
                                .build())
                        .defaultValue(null)
                        .nativeDoc(null)
                        .metadata(formattedMetadata.get(FieldMetadataKey.with(
                                SchemaInfoTestData.SUBJECT,
                                SchemaInfoTestData.VERSION,
                                "com.epam.eco.schemaregistry.client.avro.data.TestJob",
                                "position")))
                        .build())
                .addField(SchemaField.builder()
                        .name("previousJob")
                        .type(ParameterizedSchemaFieldType.builder()
                                .type(Schema.Type.UNION)
                                .addParameter(PrimitiveSchemaFieldType.builder().type(Schema.Type.NULL).build())
                                .addParameter(NamedSchemaFieldType.builder()
                                        .namespace(null)
                                        .name("TestJob")
                                        .type(Schema.Type.RECORD)
                                        .build())
                                .build())
                        .defaultValue(null)
                        .nativeDoc(null)
                        .metadata(formattedMetadata.get(FieldMetadataKey.with(
                                SchemaInfoTestData.SUBJECT,
                                SchemaInfoTestData.VERSION,
                                "com.epam.eco.schemaregistry.client.avro.data.TestJob",
                                "previousJob")))
                        .build())
                .root(false)
                .build());
        schemaEntities.add(SchemaEntity.builder()
                .name("TestPosition")
                .namespace("com.epam.eco.schemaregistry.client.avro.data")
                .addField(SchemaField.builder()
                        .name("skill")
                        .type(ParameterizedSchemaFieldType.builder()
                                .type(Schema.Type.MAP)
                                .addParameter(PrimitiveSchemaFieldType.builder().type(Schema.Type.STRING).build())
                                .addParameter(NamedSchemaFieldType.builder()
                                        .namespace(null)
                                        .name("TestSkillLevel")
                                        .type(Schema.Type.RECORD)
                                        .build())
                                .build())
                        .defaultValue(null)
                        .nativeDoc(null)
                        .metadata(formattedMetadata.get(FieldMetadataKey.with(
                                SchemaInfoTestData.SUBJECT,
                                SchemaInfoTestData.VERSION,
                                "com.epam.eco.schemaregistry.client.avro.data.TestPosition",
                                "position")))
                        .build())
                .root(false)
                .build());
        schemaEntities.add(SchemaEntity.builder()
                .name("TestSkillLevel")
                .namespace("com.epam.eco.schemaregistry.client.avro.data")
                .addField(SchemaField.builder()
                        .name("level")
                        .type(PrimitiveSchemaFieldType.builder()
                                .type(Schema.Type.STRING)
                                .build())
                        .defaultValue(null)
                        .nativeDoc(null)
                        .metadata(formattedMetadata.get(FieldMetadataKey.with(
                                SchemaInfoTestData.SUBJECT,
                                SchemaInfoTestData.VERSION,
                                "com.epam.eco.schemaregistry.client.avro.data.TestSkillLevel",
                                "level")))
                        .build())
                .root(false)
                .build());

        SchemaProfile assertProfile = SchemaProfile.builder()
                .subject(SchemaInfoTestData.SUBJECT)
                .version(SchemaInfoTestData.VERSION)
                .versionLatest(SchemaInfoTestData.VERSION_LATEST)
                .schemaRegistryId(SchemaInfoTestData.SCHEMA_REGISTRY_ID)
                .compatibilityLevel(SchemaInfoTestData.AVRO_COMPATIBILITY_LEVEL)
                .mode(Mode.READWRITE)
                .schemaMetadata(FormattedMetadata.with(SchemaInfoTestData.getSchemaMetadata(now), HtmlPartFormatter.INSTANCE))
                .schemas(schemaEntities)
                .deleted(false)
                .build();

        assertEquals(assertProfile, profile);
    }

}
