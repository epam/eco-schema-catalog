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
package com.epam.eco.schemacatalog.fts.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import com.epam.eco.schemacatalog.fts.SchemaDocumentIndexer;

/**
 * @author Andrei_Tytsik
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = {"com.epam.eco.schemacatalog.fts.repo"})
@EnableConfigurationProperties(SchemaCatalogFtsProperties.class)
@Import(EmbeddedElasticsearchConfiguration.class)
public class SchemaCatalogFtsAutoConfiguration {

    @Bean
    public SchemaDocumentIndexer schemaDocumentIndexer() {
        return new SchemaDocumentIndexer();
    }

}
