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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.epam.eco.schemacatalog.client.CachedExtendedSchemaRegistryClient;
import com.epam.eco.schemacatalog.client.ExtendedSchemaRegistryClient;
import com.epam.eco.schemacatalog.store.autoconfigure.SchemaCatalogStoreProperties;
import com.epam.eco.schemacatalog.store.common.kafka.KafkaStoreProperties;
import com.epam.eco.schemacatalog.store.schema.SchemaRegistryStore;

/**
 * @author Andrei_Tytsik
 */
@EnableConfigurationProperties(KafkaStoreProperties.class)
public class KafkaSchemaRegistryStoreConfiguration {

    @Autowired
    private SchemaCatalogStoreProperties properties;

    @Bean
    public SchemaRegistryStore schemaRegistryStore() {
        return new KafkaSchemaRegistryStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExtendedSchemaRegistryClient schemaRegistryClient() {
        return new CachedExtendedSchemaRegistryClient(properties.getSchemaRegistryUrl());
    }

}
