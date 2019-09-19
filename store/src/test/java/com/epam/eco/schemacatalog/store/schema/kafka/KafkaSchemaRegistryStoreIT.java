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
package com.epam.eco.schemacatalog.store.schema.kafka;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.epam.eco.commons.avro.AvroUtils;
import com.epam.eco.schemacatalog.store.schema.SchemaEntity;
import com.epam.eco.schemacatalog.store.schema.SchemaRegistryStore;

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

/**
 * @author Andrei_Tytsik
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=Config.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class KafkaSchemaRegistryStoreIT {

    @Autowired
    private SchemaRegistryStore schemaRegistryStore;

    @Test
    public void testAllWorksFine() throws Exception {
        List<String> allSubjects = schemaRegistryStore.getAllSubjects();
        Assert.assertNotNull(allSubjects);
        Assert.assertFalse(allSubjects.isEmpty());

        for (String subject : allSubjects) {
            List<SchemaEntity> subjectSchemas = schemaRegistryStore.getSchemas(subject);
            Assert.assertNotNull(subjectSchemas);

            SchemaEntity latestSchema = subjectSchemas.stream()
                    .max((o1, o2) -> ObjectUtils.compare(o1.getVersion(), o2.getVersion())).get();

            Assert.assertTrue(latestSchema.isVersionLatest());

            int latestCount = 0;
            for (SchemaEntity schema : subjectSchemas) {
                if (schema.isVersionLatest()) {
                    latestCount++;
                }
            }

            Assert.assertEquals(1, latestCount);
        }

        List<SchemaEntity> allSchemas = schemaRegistryStore.getAllSchemas();
        Assert.assertNotNull(allSchemas);
        Assert.assertFalse(allSchemas.isEmpty());

        String subject = allSubjects.get(new Random().nextInt(allSubjects.size()));
        SchemaEntity latestSchema = schemaRegistryStore.getLatestSchema(subject);
        Assert.assertNotNull(latestSchema);
        Assert.assertTrue(latestSchema.isVersionLatest());
        Assert.assertNotNull(latestSchema.getSchema());
        Assert.assertNotNull(latestSchema.getSubject());
        Assert.assertNotNull(latestSchema.getCompatibilityLevel());

        subject = allSubjects.get(new Random().nextInt(allSubjects.size()));
        List<SchemaEntity> schemas = schemaRegistryStore.getSchemas(subject);
        Assert.assertFalse(schemas.isEmpty());

        List<String> subjects = Arrays.asList(
                allSubjects.get(new Random().nextInt(allSubjects.size())),
                allSubjects.get(new Random().nextInt(allSubjects.size())),
                allSubjects.get(new Random().nextInt(allSubjects.size())));
        schemas = schemaRegistryStore.getSchemas(subjects);
        Assert.assertFalse(schemas.isEmpty());
    }

    @Test
    public void testSchemaRegisteredAndDeleted() throws Exception {
        String subject = "test_register_and_delete_schema_" + System.currentTimeMillis();
        Schema schema = Schema.create(Type.LONG);

        SchemaEntity schemaEntity = schemaRegistryStore.registerSchema(subject, schema);
        Assert.assertNotNull(schemaEntity);

        // and one more time
        schemaEntity = schemaRegistryStore.registerSchema(subject, schema);
        Assert.assertNotNull(schemaEntity);

        schemaRegistryStore.deleteSchema(subject, schemaEntity.getVersion());

        schemaEntity = schemaRegistryStore.getSchema(subject, schemaEntity.getVersion());
        Assert.assertNotNull(schemaEntity);
        Assert.assertTrue(schemaEntity.isDeleted());
    }

    @Test
    public void testSubjectDeleted() throws Exception {
        String schema1 = "{\"type\":\"record\",\"name\":\"Test\",\"fields\":[{\"name\":\"f1\",\"type\":\"int\"}]}";
        String schema2 = "{\"type\":\"record\",\"name\":\"Test\",\"fields\":[{\"name\":\"f1\",\"type\":\"int\"},{\"name\":\"f2\",\"type\":[\"null\",\"int\"],\"default\": null}]}";
        String schema3 = "{\"type\":\"record\",\"name\":\"Test\",\"fields\":[{\"name\":\"f1\",\"type\":\"int\"},{\"name\":\"f2\",\"type\":[\"null\",\"int\"],\"default\": null},{\"name\":\"f3\",\"type\":[\"null\",\"int\"],\"default\": null}]}";

        String subject = "test_delete_subject_" + System.currentTimeMillis();

        SchemaEntity schemaEntity1 =
                schemaRegistryStore.registerSchema(subject, AvroUtils.schemaFromJson(schema1));
        Assert.assertNotNull(schemaEntity1);

        SchemaEntity schemaEntity2 =
                schemaRegistryStore.registerSchema(subject, AvroUtils.schemaFromJson(schema2));
        Assert.assertNotNull(schemaEntity2);

        SchemaEntity schemaEntity3 =
                schemaRegistryStore.registerSchema(subject, AvroUtils.schemaFromJson(schema3));
        Assert.assertNotNull(schemaEntity3);

        schemaRegistryStore.deleteSubject(subject);

        List<SchemaEntity> schemas = schemaRegistryStore.getSchemas(subject);
        Assert.assertNotNull(schemas);
        Assert.assertEquals(3, schemas.size());
        Assert.assertTrue(schemas.get(0).isDeleted());
        Assert.assertTrue(schemas.get(1).isDeleted());
        Assert.assertTrue(schemas.get(2).isDeleted());
    }

    @Test
    public void testSubjectCompatibilityUpdated() throws Exception {
        String subject = "test_update_subject_compatibility_" + System.currentTimeMillis();
        AvroCompatibilityLevel compatibilityLevel =
                AvroCompatibilityLevel.values()[new Random().nextInt(AvroCompatibilityLevel.values().length)];

        schemaRegistryStore.updateSubjectCompatibility(subject, compatibilityLevel);

        Assert.assertEquals(schemaRegistryStore.getSubjectCompatibility(subject), compatibilityLevel);
    }

}
