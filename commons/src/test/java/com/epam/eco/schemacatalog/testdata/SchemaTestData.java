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
package com.epam.eco.schemacatalog.testdata;

import java.util.Random;
import java.util.UUID;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;

import com.epam.eco.schemacatalog.domain.schema.BasicSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.IdentitySchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.LiteSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.Mode;

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

/**
 * @author Andrei_Tytsik
 */
public class SchemaTestData {

    public static final String SCHEMA1_JSON =
            "{\"type\": \"record\", \"name\": \"TestA\", \"fields\": [" +
                "{\"name\": \"f1\", \"type\": [\"null\", " +
                    "{\"type\": \"array\", \"items\": " +
                        "{\"name\": \"TestB\", \"type\": \"record\", \"fields\":[" +
                            "{\"name\": \"f2\", \"type\":" +
                                "{\"type\": \"map\", \"values\":" +
                                    "{\"type\": \"record\", \"name\": \"TestC\", \"fields\":[" +
                                        "{\"name\": \"f3\", \"type\": [\"null\"," +
                                            "{\"type\": \"record\", \"name\": \"TestD\", \"fields\":[" +
                                                "{\"name\": \"f4\", \"type\": \"int\"}" +
                                            "]}" +
                                        "]}" +
                                    "]}" +
                                "}" +
                            "}" +
                        "]}" +
                    "}" +
                "]}" +
            "]}";

    public static final Schema SCHEMA1 = new Schema.Parser().parse(SCHEMA1_JSON);

    public static final Field SCHEMA1_FIELD1 = SchemaTestData.SCHEMA1.getField("f1");
    public static final Field SCHEMA1_FIELD2 = SCHEMA1_FIELD1.schema().getTypes().
            get(1).getElementType().getField("f2");
    public static final Field SCHEMA1_FIELD3 = SCHEMA1_FIELD2.schema().getValueType().getField("f3");
    public static final Field SCHEMA1_FIELD4 = SCHEMA1_FIELD3.schema().getTypes().
            get(1).getField("f4");

    public static final String SCHEMA2_JSON = SCHEMA1_JSON.
            replace("TestA", "TestAA").
            replace("TestB", "TestCC").
            replace("TestD", "TestDD");

    public static final Schema SCHEMA2 = new Schema.Parser().parse(SCHEMA2_JSON);

    public static IdentitySchemaInfo randomIdentitySchemaInfo() {
        return randomIdentitySchemaInfo(null, null);
    }

    public static IdentitySchemaInfo randomIdentitySchemaInfo(String subject) {
        return randomIdentitySchemaInfo(subject, null);
    }

    public static IdentitySchemaInfo randomIdentitySchemaInfo(String subject, Integer version) {
        return IdentitySchemaInfo.builder().
                subject(subject != null ? subject : UUID.randomUUID().toString()).
                version(version != null ? version : new Random().nextInt(1000)).
                schemaRegistryId(new Random().nextInt(1000)).
                build();
    }

    public static BasicSchemaInfo randomBasicSchemaInfo() {
        return randomBasicSchemaInfo(null, null);
    }

    public static BasicSchemaInfo randomBasicSchemaInfo(String subject) {
        return randomBasicSchemaInfo(subject, null);
    }

    public static BasicSchemaInfo randomBasicSchemaInfo(String subject, Integer version) {
        return BasicSchemaInfo.builder().
                subject(subject != null ? subject : UUID.randomUUID().toString()).
                version(version != null ? version : new Random().nextInt(1000)).
                schemaRegistryId(new Random().nextInt(1000)).
                schemaJson(SchemaTestData.SCHEMA1_JSON).
                build();
    }

    public static LiteSchemaInfo randomLiteSchemaInfo() {
        return randomLiteSchemaInfo(null, null);
    }

    public static LiteSchemaInfo randomLiteSchemaInfo(String subject) {
        return randomLiteSchemaInfo(subject, null);
    }

    public static LiteSchemaInfo randomLiteSchemaInfo(String subject, Integer version) {
        return LiteSchemaInfo.builder().
                subject(subject != null ? subject : UUID.randomUUID().toString()).
                version(version != null ? version : new Random().nextInt(1000)).
                schemaRegistryId(new Random().nextInt(1000)).
                name(UUID.randomUUID().toString()).
                namespace(UUID.randomUUID().toString()).
                fullName(UUID.randomUUID().toString()).
                compatibility(AvroCompatibilityLevel.FULL).
                mode(Mode.READONLY).
                build();
    }

    public static FullSchemaInfo randomFullSchemaInfo() {
        return randomFullSchemaInfo(null, null);
    }

    public static FullSchemaInfo randomFullSchemaInfo(String subject) {
        return randomFullSchemaInfo(subject, null);
    }

    public static FullSchemaInfo randomFullSchemaInfo(String subject, Integer version) {
        return FullSchemaInfo.builder().
                subject(subject != null ? subject : UUID.randomUUID().toString()).
                version(version != null ? version : new Random().nextInt(1000)).
                schemaRegistryId(new Random().nextInt(1000)).
                schemaJson(SchemaTestData.SCHEMA1_JSON).
                compatibilityLevel(AvroCompatibilityLevel.BACKWARD).
                mode(Mode.READWRITE).
                deleted(false).
                versionLatest(true).
                appendMetadata(MetadataTestData.randomKey(), MetadataTestData.randomValue()).
                appendMetadata(MetadataTestData.randomKey(), MetadataTestData.randomValue()).
                appendMetadata(MetadataTestData.randomKey(), MetadataTestData.randomValue()).
                build();
    }

}
