package com.epam.eco.schemacatalog.client;

import org.junit.jupiter.api.Test;

import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MockExtendedSchemaRegistryClientTest {

    private static final ParsedSchema TEST_SCHEMA = new AvroSchema(
            """
            {
              "type": "record",
              "namespace": "com.epam.eco.schemacatalog.test",
              "name": "Test",
              "fields": [{ "name": "id", "type": "int" }]
            }
            """
    );

    @Test
    void registeredSubjectExists() throws Exception {
        ExtendedSchemaRegistryClient client = new MockExtendedSchemaRegistryClient();
        client.register("testSubject", TEST_SCHEMA);
        assertTrue(client.subjectExists("testSubject"));
    }
}