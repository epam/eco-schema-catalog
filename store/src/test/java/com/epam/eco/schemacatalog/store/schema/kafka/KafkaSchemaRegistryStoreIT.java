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
package com.epam.eco.schemacatalog.store.schema.kafka;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.epam.eco.commons.avro.AvroUtils;
import com.epam.eco.schemacatalog.store.schema.SchemaEntity;
import com.epam.eco.schemacatalog.store.schema.SchemaRegistryStore;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrei_Tytsik
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Config.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Disabled("Manual, requires schema-registry running, see docker-compose in resources dir")
class KafkaSchemaRegistryStoreIT {

    @Autowired
    private SchemaRegistryStore schemaRegistryStore;

    @Test
    void fetchesSchemaDataFromKafka() {
        List<String> allSubjects = schemaRegistryStore.getAllSubjects();
        assertNotNull(allSubjects);
        assertFalse(allSubjects.isEmpty());

        for (String subject : allSubjects) {
            List<SchemaEntity> subjectSchemas = schemaRegistryStore.getSchemas(subject);
            assertNotNull(subjectSchemas);

            SchemaEntity latestSchema = subjectSchemas.stream()
                    .max((o1, o2) -> ObjectUtils.compare(o1.getVersion(), o2.getVersion())).get();

            assertTrue(latestSchema.isVersionLatest());

            int latestCount = 0;
            for (SchemaEntity schema : subjectSchemas) {
                if (schema.isVersionLatest()) {
                    latestCount++;
                }
            }

            assertEquals(1, latestCount);
        }

        List<SchemaEntity> allSchemas = schemaRegistryStore.getAllSchemas();
        assertNotNull(allSchemas);
        assertFalse(allSchemas.isEmpty());

        String subject = allSubjects.get(new Random().nextInt(allSubjects.size()));
        SchemaEntity latestSchema = schemaRegistryStore.getLatestSchema(subject);
        assertNotNull(latestSchema);
        assertTrue(latestSchema.isVersionLatest());
        assertNotNull(latestSchema.getSchema());
        assertNotNull(latestSchema.getSubject());
        assertNotNull(latestSchema.getCompatibilityLevel());

        subject = allSubjects.get(new Random().nextInt(allSubjects.size()));
        List<SchemaEntity> schemas = schemaRegistryStore.getSchemas(subject);
        assertFalse(schemas.isEmpty());

        List<String> subjects = Arrays.asList(
                allSubjects.get(new Random().nextInt(allSubjects.size())),
                allSubjects.get(new Random().nextInt(allSubjects.size())),
                allSubjects.get(new Random().nextInt(allSubjects.size())));
        schemas = schemaRegistryStore.getSchemas(subjects);
        assertFalse(schemas.isEmpty());
    }

    @Test
    void testSchemaRegisteredAndDeleted() {
        String subject = "test_register_and_delete_schema_" + System.currentTimeMillis();
        Schema schema = Schema.create(Type.LONG);

        SchemaEntity schemaEntity = schemaRegistryStore.registerSchema(subject, schema);
        assertNotNull(schemaEntity);

        // and one more time
        schemaEntity = schemaRegistryStore.registerSchema(subject, schema);
        assertNotNull(schemaEntity);

        schemaRegistryStore.deleteSchema(subject, schemaEntity.getVersion());

        schemaEntity = schemaRegistryStore.getSchema(subject, schemaEntity.getVersion());
        assertNotNull(schemaEntity);
        assertTrue(schemaEntity.isDeleted());
    }

    @Test
    void testSubjectDeleted() {
        String schema1 = "{\"type\":\"record\",\"name\":\"Test\",\"fields\":[{\"name\":\"f1\",\"type\":\"int\"}]}";
        String schema2 = "{\"type\":\"record\",\"name\":\"Test\",\"fields\":[{\"name\":\"f1\",\"type\":\"int\"},{\"name\":\"f2\",\"type\":[\"null\",\"int\"],\"default\": null}]}";
        String schema3 = "{\"type\":\"record\",\"name\":\"Test\",\"fields\":[{\"name\":\"f1\",\"type\":\"int\"},{\"name\":\"f2\",\"type\":[\"null\",\"int\"],\"default\": null},{\"name\":\"f3\",\"type\":[\"null\",\"int\"],\"default\": null}]}";

        String subject = "test_delete_subject_" + System.currentTimeMillis();

        SchemaEntity schemaEntity1 =
                schemaRegistryStore.registerSchema(subject, AvroUtils.schemaFromJson(schema1));
        assertNotNull(schemaEntity1);

        SchemaEntity schemaEntity2 =
                schemaRegistryStore.registerSchema(subject, AvroUtils.schemaFromJson(schema2));
        assertNotNull(schemaEntity2);

        SchemaEntity schemaEntity3 =
                schemaRegistryStore.registerSchema(subject, AvroUtils.schemaFromJson(schema3));
        assertNotNull(schemaEntity3);

        schemaRegistryStore.deleteSubject(subject);

        List<SchemaEntity> schemas = schemaRegistryStore.getSchemas(subject);
        assertNotNull(schemas);
        assertEquals(3, schemas.size());
        assertTrue(schemas.get(0).isDeleted());
        assertTrue(schemas.get(1).isDeleted());
        assertTrue(schemas.get(2).isDeleted());
    }

    @Test
    void testSubjectCompatibilityUpdated() {
        String subject = "test_update_subject_compatibility_" + System.currentTimeMillis();
        CompatibilityLevel compatibilityLevel =
                CompatibilityLevel.values()[new Random().nextInt(CompatibilityLevel.values().length)];

        schemaRegistryStore.updateSubjectCompatibility(subject, compatibilityLevel);

        assertEquals(schemaRegistryStore.getSubjectCompatibility(subject), compatibilityLevel);
    }

}
