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

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.epam.eco.schemacatalog.client.ExtendedSchemaRegistryClient;
import com.epam.eco.schemacatalog.serde.kafka.VerificationResult.Status;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrei_Tytsik
 */
public class VerifiableTest {

    private static final Schema TEST_SCHEMA = new Schema.Parser().parse(
            "{\"type\":\"record\",\"name\":\"x\",\"fields\":[]}"
            );

    private static final GenericContainer TEST_RECORD = () -> null;

    private Verifier<GenericContainer> testVerifier(VerificationResult result) {
        return new Verifier<GenericContainer>() {
            @Override
            public void init(String subject, ExtendedSchemaRegistryClient schemaRegistryClient, Map<String, ?> config) {
            }
            @Override
            public VerificationResult verify(GenericContainer data, Schema originalSchema) {
                return result;
            }
            @Override
            public void close() {
            }
        };
    }

    @Test
    public void testDataIsGet() {
        Verifiable<GenericContainer> verifiable = Verifiable.with(
                TEST_RECORD,
                TEST_SCHEMA,
                testVerifier(null));

        GenericContainer data = verifiable.get();

        Assertions.assertNotNull(data);
        Assertions.assertEquals(TEST_RECORD, data);
    }

    @Test
    public void testDataIsVerified() {
        VerificationResult resultExpected = VerificationResult.with(Status.PASSED);

        Verifiable<GenericContainer> verifiable = Verifiable.with(
                TEST_RECORD,
                TEST_SCHEMA,
                testVerifier(resultExpected));

        VerificationResult resultActual = verifiable.verify();

        Assertions.assertNotNull(resultActual);
        Assertions.assertEquals(resultExpected, resultActual);
    }

    @Test
    public void testDataIsVerifiedAndAccepted() throws Exception {
        Verifiable<GenericContainer> verifiable = Verifiable.with(
                TEST_RECORD,
                TEST_SCHEMA,
                testVerifier(VerificationResult.with(Status.PASSED)));

        AtomicReference<GenericContainer> accepted = new AtomicReference<>();
        verifiable.verifyAndAcceptIfPassed(accepted::set);

        Assertions.assertNotNull(accepted.get());
        Assertions.assertEquals(TEST_RECORD, accepted.get());
    }

    @Test
    public void testDataIsVerifiedAndNotAccepted() throws Exception {
        Verifiable<GenericContainer> verifiable = Verifiable.with(
                TEST_RECORD,
                TEST_SCHEMA,
                testVerifier(VerificationResult.with(Status.SKIPPABLE)));

        AtomicReference<GenericContainer> accepted = new AtomicReference<>();
        verifiable.verifyAndAcceptIfPassed(accepted::set);

        Assertions.assertNull(accepted.get());
    }

    @Test
    public void testDataIsVerifiedAndFailed() {
        assertThrows(
                Exception.class,
                () -> {
                    Verifiable<GenericContainer> verifiable = Verifiable.with(
                            TEST_RECORD,
                            TEST_SCHEMA,
                            testVerifier(VerificationResult.with(Status.NOT_PASSED)));

                    AtomicReference<GenericContainer> accepted = new AtomicReference<>();
                    verifiable.verifyAndAcceptIfPassed(accepted::set);
                }
        );
    }

    @Test
    public void testInitFailedOnInconsistentDataAndSchemaArguments1() {
        assertThrows(
                Exception.class,
                () -> Verifiable.with(null, TEST_SCHEMA, testVerifier(null))
        );
    }

    @Test
    public void testInitFailedOnInconsistentDataAndSchemaArguments2() {
        assertThrows(
                Exception.class,
                () -> Verifiable.with(TEST_RECORD, null, testVerifier(null))
        );
    }

    @Test
    public void testInitFailedOnMissingVerifierArgument() {
        assertThrows(
                Exception.class,
                () -> Verifiable.with(TEST_RECORD, TEST_SCHEMA, null)
        );
    }
}
