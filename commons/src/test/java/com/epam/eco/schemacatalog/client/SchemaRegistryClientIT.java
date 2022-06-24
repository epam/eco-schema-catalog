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

import org.junit.Assert;
import org.junit.Test;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;
import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;

/**
 * @author Andrei_Tytsik
 */
public class SchemaRegistryClientIT {

    private static final String SCHEMA_JSON = "{\"type\": \"record\", \"name\": \"Name\", \"fields\": [{\"name\": \"%s\", \"type\": \"string\"}]}";

    private static SchemaRegistryClient CLIENT = buildEcoCachedClient();

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
    public void testSchemaRegisteredAndGotById() throws Exception {
        String subject = randomSubject();
        ParsedSchema schema = randomSchema();

        int id = CLIENT.register(subject, schema);
        Assert.assertTrue(id >= 0);

        ParsedSchema schemaActual = CLIENT.getSchemaBySubjectAndId(subject, id);
        Assert.assertEquals(schema, schemaActual);

        schemaActual = CLIENT.getSchemaById(id);
        Assert.assertEquals(schema, schemaActual);
    }

    @Test
    public void testLatestSchemaMetadataGot()  throws Exception {
        String subject = randomSubject();

        CLIENT.updateCompatibility(subject, CompatibilityLevel.NONE.name);

        ParsedSchema schema1 = randomSchema();
        ParsedSchema schema2 = randomSchema();
        ParsedSchema schema3 = randomSchema();

        CLIENT.register(subject, schema1);
        CLIENT.register(subject, schema2);
        int idLatest = CLIENT.register(subject, schema3);

        SchemaMetadata metadata = CLIENT.getLatestSchemaMetadata(subject);
        Assert.assertEquals(idLatest, metadata.getId());
    }

    @Test
    public void testSchemaMetadataGot()  throws Exception {
        String subject = randomSubject();

        CLIENT.updateCompatibility(subject, CompatibilityLevel.NONE.name);

        ParsedSchema schema1 = randomSchema();
        ParsedSchema schema2 = randomSchema();
        ParsedSchema schema3 = randomSchema();

        CLIENT.register(subject, schema1);
        int id2 = CLIENT.register(subject, schema2);
        CLIENT.register(subject, schema3);

        SchemaMetadata metadata = CLIENT.getSchemaMetadata(subject, 2);
        Assert.assertEquals(id2, metadata.getId());
    }

    @Test
    public void testVersionGot()  throws Exception {
        String subject = randomSubject();

        CLIENT.updateCompatibility(subject, CompatibilityLevel.NONE.name);

        ParsedSchema schema1 = randomSchema();
        ParsedSchema schema2 = randomSchema();
        ParsedSchema schema3 = randomSchema();

        CLIENT.register(subject, schema1);
        int version = CLIENT.getVersion(subject, schema1);
        Assert.assertEquals(1, version);

        CLIENT.register(subject, schema2);
        version = CLIENT.getVersion(subject, schema2);
        Assert.assertEquals(2, version);

        CLIENT.register(subject, schema3);
        version = CLIENT.getVersion(subject, schema3);
        Assert.assertEquals(3, version);
    }

    @Test
    public void testAllVersionsGot()  throws Exception {
        String subject = randomSubject();

        CLIENT.updateCompatibility(subject, CompatibilityLevel.NONE.name);

        ParsedSchema schema1 = randomSchema();
        ParsedSchema schema2 = randomSchema();
        ParsedSchema schema3 = randomSchema();

        CLIENT.register(subject, schema1);
        CLIENT.register(subject, schema2);
        CLIENT.register(subject, schema3);

        List<Integer> versionsActual = CLIENT.getAllVersions(subject);

        Assert.assertEquals(Arrays.asList(1,2,3), versionsActual);
    }

    @Test
    public void testCompatibilityTested()  throws Exception {
        String subject = randomSubject();

        CLIENT.updateCompatibility(subject, CompatibilityLevel.FULL.name);

        ParsedSchema schema1 = randomSchema();
        CLIENT.register(subject, schema1);
        Assert.assertTrue(CLIENT.testCompatibility(subject, schema1));

        ParsedSchema schema2 = randomSchema();
        Assert.assertFalse(CLIENT.testCompatibility(subject, schema2));
    }

    @Test
    public void testCompatibilityUpdated()  throws Exception {
        String subject = randomSubject();

        String compatibility = CLIENT.updateCompatibility(subject, CompatibilityLevel.FULL.name);
        Assert.assertEquals(CompatibilityLevel.FULL.name, compatibility);

        compatibility = CLIENT.updateCompatibility(subject, CompatibilityLevel.FORWARD_TRANSITIVE.name);
        Assert.assertEquals(CompatibilityLevel.FORWARD_TRANSITIVE.name, compatibility);
    }

    @Test
    public void testCompatibilityGot()  throws Exception {
        String subject = randomSubject();

        CLIENT.updateCompatibility(subject, CompatibilityLevel.FULL.name);

        String compatibility = CLIENT.getCompatibility(subject);
        Assert.assertEquals(CompatibilityLevel.FULL.name, compatibility);
    }

    @Test
    public void testAllSubjectsGot()  throws Exception {
        String subject = randomSubject();
        ParsedSchema schema = randomSchema();

        CLIENT.register(subject, schema);

        Collection<String> subjects = CLIENT.getAllSubjects();
        Assert.assertTrue(subjects.contains(subject));
    }

    @Test
    public void testIdGot()  throws Exception {
        String subject = randomSubject();
        ParsedSchema schema = randomSchema();

        int id = CLIENT.register(subject, schema);

        int idActual = CLIENT.getId(subject, schema);
        Assert.assertEquals(id, idActual);
    }

    @Test
    public void testSubjectDeleted()  throws Exception {
        String subject = randomSubject();
        ParsedSchema schema = randomSchema();

        CLIENT.register(subject, schema);

        CLIENT.deleteSubject(subject);

        Collection<String> subjects = CLIENT.getAllSubjects();
        Assert.assertFalse(subjects.contains(subject));
    }

    @Test
    public void testSchemaVersionDeleted()  throws Exception {
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

        Assert.assertEquals(Arrays.asList(2,3), versionsActual);
    }

    @Test
    public void testSameSchemaRegisteredMultipleTimes() throws Exception {
        String subject = randomSubject();
        ParsedSchema schema = randomSchema();

        Assert.assertEquals(
                CLIENT.register(subject, schema),
                CLIENT.register(subject, schema));
    }

    @Test
    public void testSameSchemaRegisteredThenDeletedThenRegistered() throws Exception {
        String subject = randomSubject();
        ParsedSchema schema = randomSchema();

        CLIENT.register(subject, schema);
        int version1 = CLIENT.getVersion(subject, schema);

        CLIENT.deleteSchemaVersion(subject, "" + 1);

        CLIENT.register(subject, schema);
        int version2 = CLIENT.getVersion(subject, schema);

        Assert.assertTrue(version2 > version1);
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
