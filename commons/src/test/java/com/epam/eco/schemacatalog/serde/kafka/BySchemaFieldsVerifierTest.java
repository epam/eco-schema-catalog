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
package com.epam.eco.schemacatalog.serde.kafka;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.epam.eco.schemacatalog.client.ExtendedSchemaRegistryClient;
import com.epam.eco.schemacatalog.domain.schema.BasicSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.SubjectSchemas;

import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrei_Tytsik
 */
class BySchemaFieldsVerifierTest {

    private static final GenericRecord TEST_RECORD = new GenericRecord() {
        @Override
        public Schema getSchema() {
            return null;
        }

        @Override
        public void put(int i, Object v) {
        }

        @Override
        public Object get(int i) {
            return null;
        }

        @Override
        public void put(String key, Object v) {
        }

        @Override
        public Object get(String key) {
            return null;
        }
    };

    private static SubjectSchemas<BasicSchemaInfo> testSubject(String[]... fieldsSet) {
        List<BasicSchemaInfo> schemas = new ArrayList<>();
        int version = 1;
        for (String[] fields : fieldsSet) {
            schemas.add(testSchema(version, fields));
            version++;
        }
        return SubjectSchemas.with(schemas);
    }

    private static BasicSchemaInfo testSchema(int version, String... fields) {
        return BasicSchemaInfo.builder().
                subject("nomatter").
                version(version).
                schemaRegistryId(version).
                schemaJson(testSchema(fields)).
                build();
    }

    private static String testSchema(String... fields) {
        StringBuilder fieldsBuilder = new StringBuilder();
        for (String field : fields) {
            if (fieldsBuilder.length() > 0) {
                fieldsBuilder.append(",");
            }
            fieldsBuilder.append(String.format("{\"name\":\"%s\",\"type\":\"int\"}", field));
        }
        return String.format(
                "{\"type\":\"record\",\"name\":\"testSchema\",\"fields\":[%s]}",
                fieldsBuilder
        );
    }

    @Test
    void testGeneralLogicIsOk() throws RestClientException, IOException {
        SubjectSchemas<BasicSchemaInfo> subjectSchemas = testSubject(
                new String[]{"a"},                     // 1
                new String[]{"a", "b"},                 // 2
                new String[]{"a", "b", "c"},            // 3
                new String[]{"a", "b", "c", "d"},       // 4
                new String[]{"a", "b", "c", "d", "e"},  // 5
                new String[]{"a", "b", "c", "e"},       // 6
                new String[]{"a", "b", "e"}             // 7
        );

        ExtendedSchemaRegistryClient schemaRegistryClient = Mockito.mock(ExtendedSchemaRegistryClient.class);
        Mockito.
                when(schemaRegistryClient.getVersion(Mockito.anyString(), Mockito.any(ParsedSchema.class))).
                thenReturn(1, 2, 3, 4, 5, 6, 7);
        Mockito.
                when(schemaRegistryClient.getSubjectSchemaInfos(Mockito.anyString())).
                thenReturn(subjectSchemas);

        Map<String, Object> config = new HashMap<>();
        config.put(BySchemaFieldsVerifier.SCHEMA_FIELDS_REQUIRED_CONFIG, "a,b");
        config.put(BySchemaFieldsVerifier.SCHEMA_FIELDS_EXPECTED_CONFIG, "c,d");

        try (BySchemaFieldsVerifier verifier = new BySchemaFieldsVerifier()) {
            verifier.init("nomatter", schemaRegistryClient, config);

            VerificationResult result;

            result = verifier.verify(TEST_RECORD, subjectSchemas.getSchema(1).getSchemaAvro());
            assertNotNull(result);
            assertEquals(VerificationResult.Status.SKIPPABLE, result.getStatus());

            result = verifier.verify(TEST_RECORD, subjectSchemas.getSchema(2).getSchemaAvro());
            assertNotNull(result);
            assertEquals(VerificationResult.Status.PASSED, result.getStatus());

            result = verifier.verify(TEST_RECORD, subjectSchemas.getSchema(3).getSchemaAvro());
            assertNotNull(result);
            assertEquals(VerificationResult.Status.PASSED, result.getStatus());

            result = verifier.verify(TEST_RECORD, subjectSchemas.getSchema(4).getSchemaAvro());
            assertNotNull(result);
            assertEquals(VerificationResult.Status.PASSED, result.getStatus());

            result = verifier.verify(TEST_RECORD, subjectSchemas.getSchema(5).getSchemaAvro());
            assertNotNull(result);
            assertEquals(VerificationResult.Status.PASSED, result.getStatus());

            result = verifier.verify(TEST_RECORD, subjectSchemas.getSchema(6).getSchemaAvro());
            assertNotNull(result);
            assertEquals(VerificationResult.Status.NOT_PASSED, result.getStatus());

            result = verifier.verify(TEST_RECORD, subjectSchemas.getSchema(7).getSchemaAvro());
            assertNotNull(result);
            assertEquals(VerificationResult.Status.NOT_PASSED, result.getStatus());
        }
    }

    @Test
    void testInitFailedOnUnfeasibleConfig() {
        assertThrows(
                Exception.class,
                () -> {
                    SubjectSchemas<BasicSchemaInfo> subjectSchemas = testSubject(
                            new String[]{"a"},                 // 1
                            new String[]{"a", "b"},             // 2
                            new String[]{"b", "c"},            // 3
                            new String[]{"b", "e"}             // 4
                    );

                    ExtendedSchemaRegistryClient schemaRegistryClient = Mockito.mock(ExtendedSchemaRegistryClient.class);
                    Mockito.
                            when(schemaRegistryClient.getSubjectSchemaInfos(Mockito.anyString())).
                            thenReturn(subjectSchemas);

                    Map<String, Object> config = new HashMap<>();
                    config.put(BySchemaFieldsVerifier.SCHEMA_FIELDS_REQUIRED_CONFIG, "a");
                    config.put(BySchemaFieldsVerifier.SCHEMA_FIELDS_EXPECTED_CONFIG, "c");

                    try (BySchemaFieldsVerifier verifier = new BySchemaFieldsVerifier()) {
                        verifier.init("nomatter", schemaRegistryClient, config);
                    }
                }
        );
    }

    @Test
    void testInitFailedOnMissingConfigArgument() {
        assertThrows(
                Exception.class,
                () -> {
                    Map<String, Object> config = null;

                    ExtendedSchemaRegistryClient schemaRegistryClient = Mockito.mock(ExtendedSchemaRegistryClient.class);

                    try (BySchemaFieldsVerifier verifier = new BySchemaFieldsVerifier()) {
                        verifier.init("nomatter", schemaRegistryClient, config);
                    }
                }
        );
    }

    @Test
    void testInitFailedOnInvalidRequiredFieldsArgument() {
        assertThrows(
                Exception.class,
                () -> {
                    Map<String, Object> config = new HashMap<>();
                    config.put(BySchemaFieldsVerifier.SCHEMA_FIELDS_REQUIRED_CONFIG, "");

                    ExtendedSchemaRegistryClient schemaRegistryClient = Mockito.mock(ExtendedSchemaRegistryClient.class);

                    try (BySchemaFieldsVerifier verifier = new BySchemaFieldsVerifier()) {
                        verifier.init("nomatter", schemaRegistryClient, config);
                    }
                }
        );
    }
}
