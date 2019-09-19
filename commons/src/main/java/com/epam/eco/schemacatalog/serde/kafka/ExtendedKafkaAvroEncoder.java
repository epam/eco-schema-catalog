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

import com.epam.eco.commons.kafka.ScalaConversions;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroEncoder;
import kafka.utils.VerifiableProperties;

/**
 * @author Andrei_Tytsik
 */
public class ExtendedKafkaAvroEncoder extends KafkaAvroEncoder {

    public ExtendedKafkaAvroEncoder(SchemaRegistryClient schemaRegistry) {
        this(schemaRegistry, true);
    }

    public ExtendedKafkaAvroEncoder(
            SchemaRegistryClient schemaRegistry,
            boolean autoRegisterSchema) {
        super(schemaRegistry);
        this.autoRegisterSchema = autoRegisterSchema;
    }

    public ExtendedKafkaAvroEncoder(Map<String, Object> props) {
        this(ScalaConversions.asVerifiableProperties(props));
    }

    public ExtendedKafkaAvroEncoder(VerifiableProperties props) {
        super(props);
    }

    public byte[] toBytes(String subject, Object object) {
        return serializeImpl(subject, object);
    }

}
