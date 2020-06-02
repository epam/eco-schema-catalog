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
package com.epam.eco.schemacatalog.store.autoconfigure;

import java.io.IOException;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.epam.eco.schemacatalog.store.SchemaCatalogStore;
import com.epam.eco.schemacatalog.store.SchemaCatalogStoreImpl;
import com.epam.eco.schemacatalog.store.metadata.kafka.KafkaMetadataStoreConfiguration;
import com.epam.eco.schemacatalog.store.schema.kafka.KafkaSchemaRegistryStoreConfiguration;

/**
 * @author Andrei_Tytsik
 */
@Configuration
@Import({
    KafkaSchemaRegistryStoreConfiguration.class,
    KafkaMetadataStoreConfiguration.class})
@EnableConfigurationProperties(SchemaCatalogStoreProperties.class)
public class SchemaCatalogStoreAutoConfiguration {

    @Bean
    public SchemaCatalogStore schemaCatalogStore() {
        return new SchemaCatalogStoreImpl();
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000L);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(5);
        NeverRetryPolicy neverRetryPolicy = new NeverRetryPolicy();

        ExceptionClassifierRetryPolicy retryPolicy = new ExceptionClassifierRetryPolicy();
        retryPolicy.setExceptionClassifier(new Classifier<Throwable, RetryPolicy>() {

            private static final long serialVersionUID = 1L;

            @Override
            public RetryPolicy classify(Throwable classifiable) {
                if (classifiable instanceof IOException) {
                    return simpleRetryPolicy;
                }
                return neverRetryPolicy;
            }

        });

        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

}
