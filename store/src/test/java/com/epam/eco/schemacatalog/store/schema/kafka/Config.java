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
package com.epam.eco.schemacatalog.store.schema.kafka;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.support.RetryTemplate;

import com.epam.eco.schemacatalog.store.autoconfigure.SchemaCatalogStoreProperties;

/**
 * @author Andrei_Tytsik
 */
@Configuration
@ComponentScan
@Import(KafkaSchemaRegistryStoreConfiguration.class)
@EnableConfigurationProperties(SchemaCatalogStoreProperties.class)
public class Config {

    @Bean
    public RetryTemplate retryTemplate() {
        return new RetryTemplate();
    }
}
