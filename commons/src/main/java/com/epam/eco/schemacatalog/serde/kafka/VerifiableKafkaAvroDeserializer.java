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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.errors.SerializationException;

import com.epam.eco.schemacatalog.client.CachedExtendedSchemaRegistryClient;
import com.epam.eco.schemacatalog.client.ExtendedSchemaRegistryClient;

import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;

/**
 * @author Andrei_Tytsik
 */
public final class VerifiableKafkaAvroDeserializer extends KafkaAvroDeserializer {

    public static final String KEY_VERIFIER_CONFIG_PREFIX = "key.deserializer.verifier.";
    public static final String VALUE_VERIFIER_CONFIG_PREFIX = "value.deserializer.verifier.";

    public static final String KEY_VERIFIER_CLASS_CONFIG = KEY_VERIFIER_CONFIG_PREFIX + "class";
    public static final String VALUE_VERIFIER_CLASS_CONFIG = VALUE_VERIFIER_CONFIG_PREFIX + "class";

    private Map<String, ?> configs;
    private boolean isKey;
    private Verifier<GenericContainer> verifier;

    public VerifiableKafkaAvroDeserializer() {
        super();
    }

    public VerifiableKafkaAvroDeserializer(
            ExtendedSchemaRegistryClient schemaRegistryClient,
            Map<String, ?> props) {
        super(schemaRegistryClient, props);
    }

    public VerifiableKafkaAvroDeserializer(ExtendedSchemaRegistryClient schemaRegistryClient) {
        super(schemaRegistryClient);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        this.configs = configs;
        this.isKey = isKey;

        super.configure(configs, isKey);
    }

    @Override
    protected void configureClientProperties(AbstractKafkaAvroSerDeConfig config) {
        try {
            List<String> urls = config.getSchemaRegistryUrls();
            int maxSchemaObject = config.getMaxSchemasPerSubject();
            if (null == schemaRegistry) {
                schemaRegistry = new CachedExtendedSchemaRegistryClient(urls, maxSchemaObject);
            }
        } catch (io.confluent.common.config.ConfigException e) {
            throw new ConfigException(e.getMessage());
        }
    }

    @Override
    public Object deserialize(String topic, byte[] bytes) {
        String subject = resolveSubjectName(topic);

        initVerifierIfNeeded(subject);

        GenericContainer data = (GenericContainer)super.deserialize(topic, bytes);
        Schema originalSchema = getOriginalSchema(bytes);
        return Verifiable.with(data, originalSchema, verifier);
    }

    private Schema getOriginalSchema(byte[] payload) {
        if (payload == null) {
            return null;
        }

        try {
            int schemaId = getSchemaId(payload);
            return schemaRegistry.getById(schemaId);
        } catch (IOException | RestClientException ex) {
            throw new RuntimeException("Failed to get schema", ex);
        }
    }

    private int getSchemaId(byte[] payload) {
        ByteBuffer buffer = getByteBuffer(payload);
        return buffer.getInt();
    }

    private ByteBuffer getByteBuffer(byte[] payload) {
        ByteBuffer buffer = ByteBuffer.wrap(payload);
        if (buffer.get() != MAGIC_BYTE) {
          throw new SerializationException("Unknown magic byte!");
        }
        return buffer;
    }

    private void initVerifierIfNeeded(String subject) {
        if (verifier != null) {
            return;
        }

        Map<String, ?> verifierConfig = extractVerifierConfig();
        verifier = instantiateVerifier(subject, verifierConfig);
    }

    private String resolveSubjectName(String topic) {
        if (geSchemaRegistryClient().subjectExists(topic)) {
            return topic;
        } else {
            return getSubjectName(topic, isKey);
        }
    }

    private Map<String, ?> extractVerifierConfig() {
        return configs.entrySet().stream().
            filter((entry) ->
                (isKey && entry.getKey().startsWith(KEY_VERIFIER_CONFIG_PREFIX)) ||
                (!isKey && entry.getKey().startsWith(VALUE_VERIFIER_CONFIG_PREFIX))).
            collect(Collectors.toMap(
                    (entry) ->
                        isKey ?
                        entry.getKey().substring(KEY_VERIFIER_CONFIG_PREFIX.length()) :
                        entry.getKey().substring(VALUE_VERIFIER_CONFIG_PREFIX.length()),
                    Map.Entry::getValue));
    }

    @SuppressWarnings("unchecked")
    private Verifier<GenericContainer> instantiateVerifier(
            String subject,
            Map<String, ?> verifierConfig) {
        try {
            String className =
                    isKey ?
                    (String)configs.get(KEY_VERIFIER_CLASS_CONFIG) :
                    (String)configs.get(VALUE_VERIFIER_CLASS_CONFIG);
            if (StringUtils.isBlank(className)) {
                throw new RuntimeException(
                        String.format(
                                "%s configuration is missing",
                                isKey ? KEY_VERIFIER_CLASS_CONFIG : VALUE_VERIFIER_CLASS_CONFIG));
            }

            Class<Verifier<GenericContainer>> clazz =
                    (Class<Verifier<GenericContainer>>)Class.forName(className);
            Verifier<GenericContainer> verifier = clazz.newInstance();
            verifier.init(subject, geSchemaRegistryClient(), verifierConfig);
            return verifier;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to instantiate verifier", ex);
        }
    }

    private ExtendedSchemaRegistryClient geSchemaRegistryClient() {
        return (ExtendedSchemaRegistryClient)schemaRegistry;
    }

}
