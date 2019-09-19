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

import java.util.Map;
import java.util.Properties;

import com.epam.eco.commons.kafka.ScalaConversions;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDecoder;
import kafka.utils.VerifiableProperties;

/**
 * @author Andrei_Tytsik
 */
public class ExtendedKafkaAvroDecoder extends KafkaAvroDecoder {

    public ExtendedKafkaAvroDecoder(
            SchemaRegistryClient schemaRegistry,
            Map<String, Object> props) {
        this(schemaRegistry, ScalaConversions.asVerifiableProperties(props));
    }

    public ExtendedKafkaAvroDecoder(
            SchemaRegistryClient schemaRegistry,
            Properties props) {
        this(schemaRegistry, ScalaConversions.asVerifiableProperties(props));
    }

    public ExtendedKafkaAvroDecoder(
            SchemaRegistryClient schemaRegistry,
            VerifiableProperties props) {
        super(schemaRegistry, props);
    }

    public ExtendedKafkaAvroDecoder(SchemaRegistryClient schemaRegistry) {
        super(schemaRegistry);
    }

    public ExtendedKafkaAvroDecoder(VerifiableProperties props) {
        super(props);
    }

}
