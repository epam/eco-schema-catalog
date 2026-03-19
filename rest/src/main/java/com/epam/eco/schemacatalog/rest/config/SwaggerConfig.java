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

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Dzmitry_Krivolap
 */
@Configuration
public class SwaggerConfig {

    private static final String BASE_PACKAGE = "com.epam.eco.schemacatalog.rest.controller";
    private static final String GROUP_NAME = "schema-catalog-rest";
    private static final String TITLE = "Eco Schema Catalog API";
    private static final String DESCRIPTION = "Service that provides additional features on top of" +
            " the Schema Registry (https://docs.confluent.io/platform/current/schema-registry/index.html)";
    private static final String VERSION = "0.1.0";
    private static final String LICENSE = "Apache License Version 2.0";
    private static final String LICENSE_URL = "http://www.apache.org/licenses/LICENSE-2.0";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                              .title(TITLE)
                              .description(DESCRIPTION)
                              .version(VERSION)
                              .license(new License().name(LICENSE).url(LICENSE_URL))
                );
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group(GROUP_NAME)
                .packagesToScan(BASE_PACKAGE)
                .build();
    }
}
