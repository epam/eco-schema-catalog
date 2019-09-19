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
package com.epam.eco.schemacatalog.serde.kafka;

import java.util.function.Consumer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.commons.lang3.Validate;

import com.epam.eco.schemacatalog.serde.kafka.VerificationResult.Status;

/**
 * @author Andrei_Tytsik
 */
public final class Verifiable<T extends GenericContainer> {

    private final T data;
    private final Schema originalSchema;
    private final Verifier<T> verifier;

    private VerificationResult result;

    public Verifiable(T data, Schema originalSchema, Verifier<T> verifier) {
        Validate.isTrue(
                (data != null && originalSchema != null) || (data == null && originalSchema == null) ,
                "Data and it's original schema are inconsistent");
        Validate.notNull(verifier, "Verifier is null");

        this.originalSchema = originalSchema;
        this.data = data;
        this.verifier = verifier;
    }

    public T get() {
        return data;
    }

    public VerificationResult verify() {
        return verifyIfPresentAndGetResult();
    }

    public void verifyAndAcceptIfPassed(Consumer<T> consumer) throws VerificationNotPassedException {
        VerificationResult result = verifyIfPresentAndGetResult();
        if (result.getStatus() == Status.SKIPPABLE) {
            return;
        } else if (result.getStatus() == Status.PASSED) {
            consumer.accept(data);
        } else if (result.getStatus() == Status.NOT_PASSED) {
            throw new VerificationNotPassedException(data, result.getComment());
        } else {
            throw new RuntimeException(
                    String.format("Unknown verification status = %s", result.getStatus()));
        }
    }

    private VerificationResult verifyIfPresentAndGetResult() {
        if (result == null) {
            result = verifier.verify(data, originalSchema);
        }
        return result;
    }

    public static <T extends GenericContainer> Verifiable<T> with(
            T data,
            Schema originalSchema,
            Verifier<T> verifier) {
        return new Verifiable<>(data, originalSchema, verifier);
    }

}
