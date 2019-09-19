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

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Andrei_Tytsik
 */
public abstract class SchemaRegistrySerde {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private SchemaRegistrySerde() {
    }

    public static byte[] serializeKey(Key key) throws SchemaRegistrySerdeException {
        try {
            return MAPPER.writeValueAsBytes(key);
        } catch (IOException ioe) {
            throw new SchemaRegistrySerdeException(
                    String.format("Error while serializing schema key %s", key), ioe);
        }
    }

    public static byte[] serializeValue(Value value) throws SchemaRegistrySerdeException {
        try {
            return MAPPER.writeValueAsBytes(value);
        } catch (IOException ioe) {
            throw new SchemaRegistrySerdeException(
                    String.format("Error while serializing value schema value %s", value), ioe);
        }
    }

    public static Key deserializeKey(byte[] keyBytes) throws SchemaRegistrySerdeException {
        try {
            KeyType keyType = extractKeyType(keyBytes);
            if (keyType == KeyType.CONFIG) {
                return MAPPER.readValue(keyBytes, ConfigKey.class);
            } else if (keyType == KeyType.NOOP) {
                return MAPPER.readValue(keyBytes, NoopKey.class);
            } else if (keyType == KeyType.SCHEMA) {
                return MAPPER.readValue(keyBytes, SchemaKey.class);
            } else if (keyType == KeyType.DELETE_SUBJECT) {
                return MAPPER.readValue(keyBytes, DeleteSubjectKey.class);
            } else {
                throw new SchemaRegistrySerdeException(
                        String.format("Unsupported schema registry key type %s", keyType.name()));
            }
        } catch (IOException ioe) {
            throw new SchemaRegistrySerdeException("Failed to deserialize schema registry key", ioe);
        }
    }

    @SuppressWarnings("unchecked")
    private static KeyType extractKeyType(byte[] keyBytes) throws SchemaRegistrySerdeException, IOException {
        Map<Object, Object> keyAsMap = MAPPER.readValue(keyBytes, Map.class);
        String keyTypeName = (String) keyAsMap.get("keytype");

        try {
            return KeyType.valueOf(keyTypeName);
        } catch (IllegalArgumentException ex) {
            throw new SchemaRegistrySerdeException(
                    String.format("Unknown schema registry key type %s", keyTypeName), ex);
        }
    }

    public static Value deserializeValue(Key key, byte[] valueBytes) throws SchemaRegistrySerdeException {
        if (valueBytes == null) {
            return null;
        }

        try {
            if (KeyType.CONFIG == key.getKeytype()) {
                return MAPPER.readValue(valueBytes, ConfigValue.class);
            } else if (KeyType.SCHEMA == key.getKeytype()) {
                return MAPPER.readValue(valueBytes, SchemaValue.class);
            } else if (KeyType.DELETE_SUBJECT == key.getKeytype()) {
                return MAPPER.readValue(valueBytes, DeleteSubjectValue.class);
            } else {
                throw new SchemaRegistrySerdeException(
                        String.format(
                                "Can't deserialize schema registry value, key type %s not supported",
                                key.getKeytype().name()));
            }
        } catch (IOException ioe) {
            throw new SchemaRegistrySerdeException("Failed to deserialize schema registry value", ioe);
        }
    }

}
