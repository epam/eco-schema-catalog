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

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.epam.eco.commons.avro.AvroUtils;
import com.epam.eco.schemacatalog.store.schema.SchemaEntity;
import com.epam.eco.schemacatalog.store.schema.SchemaRegistryStore;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrei_Tytsik
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Config.class)
@ContextConfiguration(initializers = KafkaSchemaRegistryStoreIT.DynamicTimestampInitializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {
        "eco.schemacatalog.store.schema-registry.urls=<put_here_url>",
        "eco.schemacatalog.store.kafka.bootstrap-servers=<put_here_url>"
})
@Disabled("put kafka and schema-registry urls")
class KafkaSchemaRegistryStoreIT {

    public static class DynamicTimestampInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            Map<String, Object> props = new HashMap<>();
            //speedup bootstrap _scheme topic for test purpose
            props.put("eco.schemacatalog.store.kafka.bootstrap-start-timestamp-ms",
                    System.getProperty("BOOTSTRAP_START_TIMESTAMP_MS",
                            String.valueOf(Instant.now().toEpochMilli())));

            MapPropertySource propertySource = new MapPropertySource("dynamicProperties", props);
            environment.getPropertySources().addFirst(propertySource);
        }
    }

    @Autowired
    private SchemaRegistryStore schemaRegistryStore;

    @Test
    @Order(1)
    void testSchemaRegisteredAndDeleted() {
        String subject = "test_register_and_delete_schema_" + System.currentTimeMillis();
        Schema schema = Schema.create(Type.LONG);

        int initialSchemaVersion = schemaRegistryStore.registerSchema(subject, schema);
        assertTrue(initialSchemaVersion > 0);

        // and one more time
        //return cached response and the same scheme won't be created
        int skippedSchemaVersion = schemaRegistryStore.registerSchema(subject, schema);
        assertEquals(initialSchemaVersion, skippedSchemaVersion);

        schemaRegistryStore.deleteSchema(subject, initialSchemaVersion);

        //it was performed only soft delete by schema-registryv
        SchemaEntity schemaEntityDeleted = schemaRegistryStore.getSchema(subject, initialSchemaVersion);
        assertNotNull(schemaEntityDeleted);
        assertTrue(schemaEntityDeleted.isDeleted());
        assertNotNull(schemaEntityDeleted.getDeletedTimestamp());
        assertNotNull(schemaEntityDeleted.getCreatedTimestamp());
        assertTrue(schemaEntityDeleted.getDeletedTimestamp() > schemaEntityDeleted.getCreatedTimestamp());

        // the same scheme can be created only after deletion
        //schema-registry perform hard delete previous version with the creation the new the same schema
        int newVersion = schemaRegistryStore.registerSchema(subject, schema);
        assertTrue(initialSchemaVersion < newVersion);
        assertThat(schemaRegistryStore.getSchemas(subject)).hasSize(1);

        SchemaEntity schemaEntity = schemaRegistryStore.getSchema(subject, newVersion);
        assertNotNull(schemaEntity);
        assertFalse(schemaEntity.isDeleted());
        assertNull(schemaEntity.getDeletedTimestamp());
        assertNotNull(schemaEntity.getCreatedTimestamp());
    }

    @Test
    @Order(2)
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
    @Order(3)
    void testSubjectDeleted() {
        String schema1 = "{\"type\":\"record\",\"name\":\"Test\",\"fields\":[{\"name\":\"f1\",\"type\":\"int\"}]}";
        String schema2 = "{\"type\":\"record\",\"name\":\"Test\",\"fields\":[{\"name\":\"f1\",\"type\":\"int\"},{\"name\":\"f2\",\"type\":[\"null\",\"int\"],\"default\": null}]}";
        String schema3 = "{\"type\":\"record\",\"name\":\"Test\",\"fields\":[{\"name\":\"f1\",\"type\":\"int\"},{\"name\":\"f2\",\"type\":[\"null\",\"int\"],\"default\": null},{\"name\":\"f3\",\"type\":[\"null\",\"int\"],\"default\": null}]}";

        String subject = "test_delete_subject_" + System.currentTimeMillis();

        int version1 =
                schemaRegistryStore.registerSchema(subject, AvroUtils.schemaFromJson(schema1));
        assertNotNull(version1 > 0);

        int version2 =
                schemaRegistryStore.registerSchema(subject, AvroUtils.schemaFromJson(schema2));
        assertNotNull(version2 > version1);

        int version3 =
                schemaRegistryStore.registerSchema(subject, AvroUtils.schemaFromJson(schema3));
        assertNotNull(version3 > version2);

        schemaRegistryStore.deleteSubject(subject);

        List<SchemaEntity> schemas = schemaRegistryStore.getSchemas(subject);
        assertNotNull(schemas);
        assertEquals(3, schemas.size());
        assertTrue(schemas.get(0).isDeleted());
        assertTrue(schemas.get(1).isDeleted());
        assertTrue(schemas.get(2).isDeleted());
        assertNotNull(schemas.get(0).getDeletedTimestamp());
        assertNotNull(schemas.get(1).getDeletedTimestamp());
        assertNotNull(schemas.get(2).getDeletedTimestamp());
    }

    @Test
    @Order(4)
    void testSubjectCompatibilityUpdated() {
        String subject = "test_update_subject_compatibility_" + System.currentTimeMillis();
        CompatibilityLevel compatibilityLevel =
                CompatibilityLevel.values()[new Random().nextInt(CompatibilityLevel.values().length)];

        schemaRegistryStore.updateSubjectCompatibility(subject, compatibilityLevel);

        assertEquals(schemaRegistryStore.getSubjectCompatibility(subject), compatibilityLevel);
    }

}
