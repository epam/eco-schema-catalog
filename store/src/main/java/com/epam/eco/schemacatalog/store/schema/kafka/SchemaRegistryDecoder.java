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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.eco.commons.kafka.serde.KeyValueDecoder;

/**
 * @author Andrei_Tytsik
 */
public class SchemaRegistryDecoder implements KeyValueDecoder<Key, Value> {

    private final static Logger LOGGER = LoggerFactory.getLogger(SchemaRegistryDecoder.class);

    @Override
    public Key decodeKey(byte[] keyBytes) {
        try {
            return SchemaRegistrySerde.deserializeKey(keyBytes);
        } catch (SchemaRegistrySerdeException srse) {
            LOGGER.warn("", srse);
            return null;
        }
    }

    @Override
    public Value decodeValue(Key key, byte[] valueBytes) {
        try {
            return SchemaRegistrySerde.deserializeValue(key, valueBytes);
        } catch (SchemaRegistrySerdeException srse) {
            LOGGER.warn("", srse);
            return null;
        }
    }

}
