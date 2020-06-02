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
package com.epam.eco.schemacatalog.rest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.epam.eco.schemacatalog.rest.utils.SchemaProfileCreator;
import com.epam.eco.schemacatalog.store.autoconfigure.SchemaCatalogStoreProperties;

/**
 * @author Andrei_Tytsik
 */
@Configuration
@EnableConfigurationProperties(SchemaCatalogRestProperties.class)
public class SchemaCatalogRestConfiguration implements WebMvcConfigurer {

    @SuppressWarnings("unused")
    @Autowired
    private SchemaCatalogStoreProperties storeProperties;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(Boolean.FALSE);
    }

    @Bean
    public SchemaProfileCreator schemaProfileCreator() {
        return new SchemaProfileCreator();
    }

}
