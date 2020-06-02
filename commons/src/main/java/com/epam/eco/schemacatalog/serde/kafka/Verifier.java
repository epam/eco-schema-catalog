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

import java.io.Closeable;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;

import com.epam.eco.schemacatalog.client.ExtendedSchemaRegistryClient;

/**
 * @author Andrei_Tytsik
 */
public interface Verifier<T extends GenericContainer> extends Closeable {
    void init(
            String subject,
            ExtendedSchemaRegistryClient schemaRegistryClient,
            Map<String, ?> config);
    VerificationResult verify(T data, Schema originalSchema);
    @Override
    void close();
}
