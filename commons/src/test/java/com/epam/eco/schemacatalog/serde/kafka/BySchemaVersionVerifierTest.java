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

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.epam.eco.schemacatalog.client.ExtendedSchemaRegistryClient;

import io.confluent.kafka.schemaregistry.ParsedSchema;


/**
 * @author Andrei_Tytsik
 */
public class BySchemaVersionVerifierTest {

    private static final GenericContainer TEST_RECORD = () -> null;

    private static final Schema TEST_SCHEMA = new Schema.Parser().parse(
            "{\"type\":\"record\",\"name\":\"x\",\"fields\":[]}"
            );

    @Test
    public void testGeneralLogicIsOk() throws Exception {
        Map<String, Object> config = new HashMap<>();
        config.put(BySchemaVersionVerifier.SCHEMA_VERSION_CONFIG, "[2,3]");

        ExtendedSchemaRegistryClient schemaRegistryClient = Mockito.mock(ExtendedSchemaRegistryClient.class);
        Mockito.
            when(schemaRegistryClient.getVersion(Mockito.anyString(), Mockito.any(ParsedSchema.class))).
            thenReturn(1,2,3,4);

        try (BySchemaVersionVerifier verifier = new BySchemaVersionVerifier()) {
            verifier.init("nomatter", schemaRegistryClient, config);

            VerificationResult result;

            result = verifier.verify(TEST_RECORD, TEST_SCHEMA);
            Assert.assertNotNull(result);
            Assert.assertEquals(VerificationResult.Status.SKIPPABLE, result.getStatus());

            result = verifier.verify(TEST_RECORD, TEST_SCHEMA);
            Assert.assertNotNull(result);
            Assert.assertEquals(VerificationResult.Status.PASSED, result.getStatus());

            result = verifier.verify(TEST_RECORD, TEST_SCHEMA);
            Assert.assertNotNull(result);
            Assert.assertEquals(VerificationResult.Status.PASSED, result.getStatus());

            result = verifier.verify(TEST_RECORD, TEST_SCHEMA);
            Assert.assertNotNull(result);
            Assert.assertEquals(VerificationResult.Status.NOT_PASSED, result.getStatus());
        }
    }

    @Test(expected=Exception.class)
    public void testInitFailedOnMissingConfigArgument() throws Exception {
        Map<String, Object> config = null;

        ExtendedSchemaRegistryClient schemaRegistryClient = Mockito.mock(ExtendedSchemaRegistryClient.class);

        try (BySchemaVersionVerifier verifier = new BySchemaVersionVerifier()) {
            verifier.init("nomatter", schemaRegistryClient, config);
        }
    }

    @Test(expected=Exception.class)
    public void testInitFailedOnInvalidVersionArgument() throws Exception {
        Map<String, Object> config = new HashMap<>();
        config.put(BySchemaVersionVerifier.SCHEMA_VERSION_CONFIG, "[3,2]");

        ExtendedSchemaRegistryClient schemaRegistryClient = Mockito.mock(ExtendedSchemaRegistryClient.class);

        try (BySchemaVersionVerifier verifier = new BySchemaVersionVerifier()) {
            verifier.init("nomatter", schemaRegistryClient, config);
        }
    }

}
