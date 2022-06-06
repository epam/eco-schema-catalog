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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.avro.Schema;

import com.epam.eco.commons.avro.AvroUtils;
import com.epam.eco.schemacatalog.domain.metadata.FieldMetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.Metadata;
import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;
import com.epam.eco.schemacatalog.domain.metadata.SchemaMetadataKey;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.Mode;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;

/**
 * @author Raman_Babich
 */
public class SchemaInfoTestData {

    public final static String SCHEMA_JSON =
            "{\"type\": \"record\"," +
                    "\"name\": \"TestPerson\"," +
                    "\"namespace\" : \"com.epam.eco.schemaregistry.client.avro.data\"," +
                    "\"fields\": [" +
                    "{\"name\": \"age\", \"type\": [\"null\", \"int\"]}," +
                    "{\"name\": \"hobby\", \"type\": [\"null\", " +
                    "{\"type\": \"array\", \"items\": " +
                    "{\"name\": \"TestHobby\", \"type\": \"record\", \"fields\":[" +
                    "{\"name\": \"kind\", \"type\": \"string\"}," +
                    "{\"name\": \"description\", \"type\": \"string\", \"default\":\"\"}" +
                    "]}" +
                    "}" +
                    "]}," +
                    "{\"name\": \"job\", \"type\":" +
                    "{\"type\": \"record\", \"name\": \"TestJob\", \"fields\":[" +
                    "{\"name\": \"position\", \"type\":" +
                    "{\"type\": \"record\", \"name\": \"TestPosition\", \"fields\":[" +
                    "{\"name\": \"skill\", \"type\":" +
                    "{\"type\": \"map\", \"values\":" +
                    "{\"type\": \"record\", \"name\": \"TestSkillLevel\", \"fields\":[" +
                    "{\"name\": \"level\", \"type\": \"string\"}" +
                    "]}" +
                    "}" +
                    "}" +
                    "]}" +
                    "}," +
                    "{\"name\": \"previousJob\", \"type\":  [\"null\", \"TestJob\"]}" +
                    "]}" +
                    "}]" +
                    "}";

    public final static Schema SCHEMA = new Schema.Parser().parse(SCHEMA_JSON);

    public final static Object SCHEMA_GENERIC = AvroUtils.schemaToGeneric(SCHEMA);

    public static final int RECORD_COUNT = 6;
    public static final int UNION_COUNT = 3;
    public static final int NULL_COUNT = 3;
    public static final int ARRAY_COUNT = 1;
    public static final int MAP_COUNT = 1;
    public static final int INT_COUNT = 1;
    public static final int STRING_COUNT = 3;
    public static final Set<String> FIELD_PATHS = new HashSet<>(Arrays.asList(
            "age", "hobby", "hobby.kind", "hobby.description", "job", "job.position", "job.position.skill", "job.position.skill.level", "job.previousJob"
    ));

    public static final String DESIRED_PATH = "job.position.skill.level";
    public static final int DESIRED_PATH_RECORD_COUNT = 4;
    public static final int DESIRED_PATH_UNION_COUNT = 0;
    public static final int DESIRED_PATH_NULL_COUNT = 0;
    public static final int DESIRED_PATH_ARRAY_COUNT = 0;
    public static final int DESIRED_PATH_MAP_COUNT = 1;
    public static final int DESIRED_PATH_INT_COUNT = 0;
    public static final int DESIRED_PATH_STRING_COUNT = 1;
    public static final Set<String> DESIRED_PATH_FIELD_PATHS = new HashSet<>(Arrays.asList(
            "job", "job.position", "job.position.skill", "job.position.skill.level"
    ));

    public static final String SUBJECT = "my-own-super-subject";
    public static final int VERSION = 1;
    public static final CompatibilityLevel AVRO_COMPATIBILITY_LEVEL = CompatibilityLevel.BACKWARD;
    public static final Mode MODE = Mode.READWRITE;
    public static final boolean DELETED = false;
    public static final boolean VERSION_LATEST = true;
    public static final int SCHEMA_REGISTRY_ID = 1243534266;

    public static FullSchemaInfo sample(Date now) {
        return FullSchemaInfo.builder()
                .subject(SUBJECT)
                .compatibilityLevel(AVRO_COMPATIBILITY_LEVEL)
                .mode(MODE)
                .deleted(DELETED)
                .versionLatest(VERSION_LATEST)
                .metadata(getFieldMetadata(now))
                .appendMetadata(getSchemaMetadata(now))
                .schemaJson(SCHEMA_JSON)
                .schemaRegistryId(SCHEMA_REGISTRY_ID)
                .version(VERSION)
                .build();
    }

    public static FullSchemaInfo sample() {
        return sample(new Date());
    }

    public static Metadata getSchemaMetadata() {
        return getSchemaMetadata(new Date());
    }

    public static Metadata getSchemaMetadata(Date now) {
        MetadataKey key1 = SchemaMetadataKey.with(SUBJECT, VERSION);
        String doc1 = "Real schema bish bash. {@link barabam|httpz://superhost.com}";
        String updatedBy1 = "Turtle Leonardo";
        MetadataValue value1 = MetadataValue.builder().
                doc(doc1).
                attributes(Collections.singletonMap("a", "a")).
                updatedAt(now).
                updatedBy(updatedBy1).
                build();
        return Metadata.with(key1, value1);
    }

    public static Map<MetadataKey, MetadataValue> getFieldMetadata() {
        return getFieldMetadata(new Date());
    }

    public static Map<MetadataKey, MetadataValue> getFieldMetadata(Date now) {
        Map<MetadataKey, MetadataValue> metadata = new HashMap<>();

        MetadataKey key2 = FieldMetadataKey.with(SUBJECT, VERSION, "com.epam.eco.schemaregistry.client.avro.data.TestHobby", "description");
        String doc2 = "Ok let's go.";
        String updatedBy2 = "Turtle Donatello";
        MetadataValue value2 = MetadataValue.builder().
                doc(doc2).
                attributes(Collections.singletonMap("b", "b")).
                updatedAt(now).
                updatedBy(updatedBy2).
                build();

        MetadataKey key3 = FieldMetadataKey.with(SUBJECT, VERSION, "com.epam.eco.schemaregistry.client.avro.data.TestJob", "position");
        String doc3 = "Some very usefully information. {@link zzzzz|httpz://sleep.com}";
        String updatedBy3 = "Turtle Michelangelo";
        MetadataValue value3 = MetadataValue.builder().
                doc(doc3).
                attributes(Collections.singletonMap("c", "c")).
                updatedAt(now).
                updatedBy(updatedBy3).
                build();

        MetadataKey key4 = FieldMetadataKey.with(SUBJECT, VERSION, "com.epam.eco.schemaregistry.client.avro.data.TestJob", "previousJob");
        String doc4 = "agsddfa asdasd asdads asdapl";
        String updatedBy4 = "Turtle Raphael";
        MetadataValue value4 = MetadataValue.builder().
                doc(doc4).
                attributes(Collections.singletonMap("d", "d")).
                updatedAt(now).
                updatedBy(updatedBy4).
                build();

        metadata.put(key2, value2);
        metadata.put(key3, value3);
        metadata.put(key4, value4);

        return metadata;
    }

}
