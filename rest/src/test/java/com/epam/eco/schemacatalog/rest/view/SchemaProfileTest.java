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
package com.epam.eco.schemacatalog.rest.view;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.avro.Schema;
import org.junit.Assert;
import org.junit.Test;

import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.domain.metadata.SchemaMetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.format.ToStringPartFormatter;
import com.epam.eco.schemacatalog.domain.schema.Mode;

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

/**
 * @author Raman_Babich
 */
public class SchemaProfileTest {

    @Test
    public void testSerializedToJsonAndBack() throws Exception {
        Date now  = new Date();
        FormattedMetadataValue metadataValue = FormattedMetadataValue.builder()
                .doc("abcdefg", ToStringPartFormatter.INSTANCE)
                .updatedAt(now)
                .updatedBy("me")
                .attributes(Collections.singletonMap("a", "b"))
                .build();

        Set<SchemaEntity> schemas = new HashSet<>();

        Set<SchemaField> fields = new HashSet<>();
        fields.add(SchemaField.builder()
                .name("name-1")
                .nativeDoc("doc-1")
                .type(PrimitiveSchemaFieldType.builder().type(Schema.Type.STRING).build())
                .metadata(FormattedMetadata.with(SchemaMetadataKey.with("subject", 1), metadataValue))
                .defaultValue("")
                .build());
        fields.add(SchemaField.builder()
                .name("name-2")
                .nativeDoc("doc-2")
                .type(PrimitiveSchemaFieldType.builder().type(Schema.Type.INT).build())
                .metadata(FormattedMetadata.with(SchemaMetadataKey.with("subject", 1), metadataValue))
                .defaultValue(1)
                .build());
        fields.add(SchemaField.builder()
                .name("name-3")
                .nativeDoc("doc-3")
                .type(ParameterizedSchemaFieldType.builder()
                        .type(Schema.Type.UNION)
                        .addParameter(PrimitiveSchemaFieldType.builder().type(Schema.Type.NULL).build())
                        .addParameter(PrimitiveSchemaFieldType.builder().type(Schema.Type.DOUBLE).build())
                        .build())
                .metadata(FormattedMetadata.with(SchemaMetadataKey.with("subject", 1), metadataValue))
                .defaultValue(null)
                .build());
        schemas.add(SchemaEntity.builder()
                .name("name1")
                .namespace("namespace")
                .fields(fields)
                .root(true)
                .build());

        fields.add(SchemaField.builder()
                .name("name-1")
                .nativeDoc("doc-1")
                .type(PrimitiveSchemaFieldType.builder().type(Schema.Type.STRING).build())
                .metadata(FormattedMetadata.with(SchemaMetadataKey.with("subject", 1), metadataValue))
                .defaultValue("")
                .build());
        fields.add(SchemaField.builder()
                .name("name-2")
                .nativeDoc("doc-2")
                .type(PrimitiveSchemaFieldType.builder().type(Schema.Type.INT).build())
                .metadata(FormattedMetadata.with(SchemaMetadataKey.with("subject", 1), metadataValue))
                .defaultValue(1)
                .build());
        fields.add(SchemaField.builder()
                .name("name-3")
                .nativeDoc("doc-3")
                .type(ParameterizedSchemaFieldType.builder()
                        .type(Schema.Type.UNION)
                        .addParameter(PrimitiveSchemaFieldType.builder().type(Schema.Type.NULL).build())
                        .addParameter(PrimitiveSchemaFieldType.builder().type(Schema.Type.DOUBLE).build())
                        .build())
                .metadata(FormattedMetadata.with(SchemaMetadataKey.with("subject", 1), metadataValue))
                .defaultValue(null)
                .build());
        schemas.add(SchemaEntity.builder()
                .name("name2")
                .namespace("namespace")
                .fields(fields)
                .root(true)
                .build());

        SchemaProfile origin = SchemaProfile.builder()
                .subject("subject")
                .version(1)
                .compatibilityLevel(AvroCompatibilityLevel.BACKWARD)
                .mode(Mode.READWRITE)
                .schemaRegistryId(1)
                .versionLatest(true)
                .schemas(schemas)
                .deleted(false)
                .schemaMetadata(FormattedMetadata.with(SchemaMetadataKey.with("subject", 1), metadataValue))
                .build();

        String json = JsonMapper.toJson(origin);
        Assert.assertNotNull(json);

        SchemaProfile deserialized = JsonMapper.jsonToObject(json, SchemaProfile.class);
        Assert.assertNotNull(deserialized);
        Assert.assertEquals(origin, deserialized);
    }

}
