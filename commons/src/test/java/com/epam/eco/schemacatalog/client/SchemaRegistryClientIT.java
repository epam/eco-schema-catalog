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
package com.epam.eco.schemacatalog.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;
import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrei_Tytsik
 */
@Disabled("Manual, requires schema-registry running, see docker-compose in resources dir")
class SchemaRegistryClientIT {

    private static final String SCHEMA_JSON = "{\"type\": \"record\", \"name\": \"Name\", \"fields\": [{\"name\": \"%s\", \"type\": \"string\"}]}";

    private static final SchemaRegistryClient CLIENT = buildEcoCachedClient();

    private static SchemaRegistryClient buildEcoCachedClient() {
        try {
            Properties properties = new Properties();
            properties.load(SchemaRegistryClientIT.class.getResourceAsStream(
                    "/schemaregistry-client-it.properties"));
            String schemaRegistryUrl = properties.getProperty("schema.registry.url");
            return new EcoCachedSchemaRegistryClient(schemaRegistryUrl, 1_000);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Test
    void testSchemaRegisteredAndGotById() throws Exception {
        String subject = randomSubject();
        ParsedSchema schema = randomSchema();

        int id = CLIENT.register(subject, schema);
        assertTrue(id >= 0);

        ParsedSchema schemaActual = CLIENT.getSchemaBySubjectAndId(subject, id);
        assertEquals(schema, schemaActual);

        schemaActual = CLIENT.getSchemaById(id);
        assertEquals(schema, schemaActual);
    }

    @Test
    void testLatestSchemaMetadataGot() throws Exception {
        String subject = randomSubject();

        CLIENT.updateCompatibility(subject, CompatibilityLevel.NONE.name);

        ParsedSchema schema1 = randomSchema();
        ParsedSchema schema2 = randomSchema();
        ParsedSchema schema3 = randomSchema();

        CLIENT.register(subject, schema1);
        CLIENT.register(subject, schema2);
        int idLatest = CLIENT.register(subject, schema3);

        SchemaMetadata metadata = CLIENT.getLatestSchemaMetadata(subject);
        assertEquals(idLatest, metadata.getId());
    }

    @Test
    void testSchemaMetadataGot() throws Exception {
        String subject = randomSubject();

        CLIENT.updateCompatibility(subject, CompatibilityLevel.NONE.name);

        ParsedSchema schema1 = randomSchema();
        ParsedSchema schema2 = randomSchema();
        ParsedSchema schema3 = randomSchema();

        CLIENT.register(subject, schema1);
        int id2 = CLIENT.register(subject, schema2);
        CLIENT.register(subject, schema3);

        SchemaMetadata metadata = CLIENT.getSchemaMetadata(subject, 2);
        assertEquals(id2, metadata.getId());
    }

    @Test
    void testVersionGot() throws Exception {
        String subject = randomSubject();

        CLIENT.updateCompatibility(subject, CompatibilityLevel.NONE.name);

        ParsedSchema schema1 = randomSchema();
        ParsedSchema schema2 = randomSchema();
        ParsedSchema schema3 = randomSchema();

        CLIENT.register(subject, schema1);
        int version = CLIENT.getVersion(subject, schema1);
        assertEquals(1, version);

        CLIENT.register(subject, schema2);
        version = CLIENT.getVersion(subject, schema2);
        assertEquals(2, version);

        CLIENT.register(subject, schema3);
        version = CLIENT.getVersion(subject, schema3);
        assertEquals(3, version);
    }

    @Test
    void testAllVersionsGot() throws Exception {
        String subject = randomSubject();

        CLIENT.updateCompatibility(subject, CompatibilityLevel.NONE.name);

        ParsedSchema schema1 = randomSchema();
        ParsedSchema schema2 = randomSchema();
        ParsedSchema schema3 = randomSchema();

        CLIENT.register(subject, schema1);
        CLIENT.register(subject, schema2);
        CLIENT.register(subject, schema3);

        List<Integer> versionsActual = CLIENT.getAllVersions(subject);

        assertEquals(Arrays.asList(1, 2, 3), versionsActual);
    }

    @Test
    void testCompatibilityTested() throws Exception {
        String subject = randomSubject();

        CLIENT.updateCompatibility(subject, CompatibilityLevel.FULL.name);

        ParsedSchema schema1 = randomSchema();
        CLIENT.register(subject, schema1);
        assertTrue(CLIENT.testCompatibility(subject, schema1));

        ParsedSchema schema2 = randomSchema();
        assertFalse(CLIENT.testCompatibility(subject, schema2));
    }

    @Test
    void testCompatibilityUpdated() throws Exception {
        String subject = randomSubject();

        String compatibility = CLIENT.updateCompatibility(subject, CompatibilityLevel.FULL.name);
        assertEquals(CompatibilityLevel.FULL.name, compatibility);

        compatibility = CLIENT.updateCompatibility(subject, CompatibilityLevel.FORWARD_TRANSITIVE.name);
        assertEquals(CompatibilityLevel.FORWARD_TRANSITIVE.name, compatibility);
    }

    @Test
    void testCompatibilityGot() throws Exception {
        String subject = randomSubject();

        CLIENT.updateCompatibility(subject, CompatibilityLevel.FULL.name);

        String compatibility = CLIENT.getCompatibility(subject);
        assertEquals(CompatibilityLevel.FULL.name, compatibility);
    }

    @Test
    void testAllSubjectsGot() throws Exception {
        String subject = randomSubject();
        ParsedSchema schema = randomSchema();

        CLIENT.register(subject, schema);

        Collection<String> subjects = CLIENT.getAllSubjects();
        assertTrue(subjects.contains(subject));
    }

    @Test
    void testIdGot() throws Exception {
        String subject = randomSubject();
        ParsedSchema schema = randomSchema();

        int id = CLIENT.register(subject, schema);

        int idActual = CLIENT.getId(subject, schema);
        assertEquals(id, idActual);
    }

    @Test
    void testSubjectDeleted() throws Exception {
        String subject = randomSubject();
        ParsedSchema schema = randomSchema();

        CLIENT.register(subject, schema);

        CLIENT.deleteSubject(subject);

        Collection<String> subjects = CLIENT.getAllSubjects();
        assertFalse(subjects.contains(subject));
    }

    @Test
    void testSchemaVersionDeleted() throws Exception {
        String subject = randomSubject();

        CLIENT.updateCompatibility(subject, CompatibilityLevel.NONE.name);

        ParsedSchema schema1 = randomSchema();
        ParsedSchema schema2 = randomSchema();
        ParsedSchema schema3 = randomSchema();

        CLIENT.register(subject, schema1);
        CLIENT.register(subject, schema2);
        CLIENT.register(subject, schema3);

        CLIENT.deleteSchemaVersion(subject, "" + 1);
        List<Integer> versionsActual = CLIENT.getAllVersions(subject);

        assertEquals(Arrays.asList(2, 3), versionsActual);
    }

    @Test
    void testSameSchemaRegisteredMultipleTimes() throws Exception {
        String subject = randomSubject();
        ParsedSchema schema = randomSchema();

        assertEquals(
                CLIENT.register(subject, schema),
                CLIENT.register(subject, schema));
    }

    @Test
    void testSameSchemaRegisteredThenDeletedThenRegistered() throws Exception {
        String subject = randomSubject();
        ParsedSchema schema = randomSchema();

        CLIENT.register(subject, schema);
        int version1 = CLIENT.getVersion(subject, schema);

        CLIENT.deleteSchemaVersion(subject, "" + 1);

        CLIENT.register(subject, schema);
        int version2 = CLIENT.getVersion(subject, schema);

        assertTrue(version2 > version1);
    }

    private String randomSubject() {
        return "subj-" + randomString();
    }

    private String randomFieldName() {
        return "field_" + randomString();
    }

    private ParsedSchema randomSchema() {
        String randomFieldName = randomFieldName();
        String schemaJson = String.format(SCHEMA_JSON, randomFieldName);
        return new AvroSchema(schemaJson);
    }

    private String randomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
