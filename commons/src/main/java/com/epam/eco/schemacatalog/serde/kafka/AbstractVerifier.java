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
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.commons.lang3.StringUtils;

import com.epam.eco.schemacatalog.client.ExtendedSchemaRegistryClient;

import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;

/**
 * @author Andrei_Tytsik
 */
public abstract class AbstractVerifier<T extends GenericContainer> implements Verifier<T> {

    protected String subject;
    protected ExtendedSchemaRegistryClient schemaRegistryClient;
    protected Map<String, ?> config;

    @Override
    public void init(
            String subject,
            ExtendedSchemaRegistryClient schemaRegistryClient,
            Map<String, ?> config) {
        this.subject = subject;
        this.schemaRegistryClient = schemaRegistryClient;
        this.config = config;
    }

    @Override
    public void close() {
    }

    protected int getSchemaVersion(Schema schema) {
        try {
            return schemaRegistryClient.getVersion(subject, schema);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException("Failed to get schema version", ex);
        }
    }

    protected String readStringConfig(String key, boolean required) {
        String value = config != null ? (String)config.get(key) : null;
        value = StringUtils.stripToNull(value);
        if (value == null && required) {
            throw new RuntimeException(
                    String.format("Config value for %s is missing", key));
        }
        return value;
    }

}
