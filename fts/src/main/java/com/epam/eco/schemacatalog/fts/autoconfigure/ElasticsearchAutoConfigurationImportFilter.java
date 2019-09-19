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
package com.epam.eco.schemacatalog.fts.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;

/**
 * @author Andrei_Tytsik
 */
public class ElasticsearchAutoConfigurationImportFilter implements AutoConfigurationImportFilter {

    @Override
    public boolean[] match(
            String[] autoConfigurationClasses,
            AutoConfigurationMetadata autoConfigurationMetadata) {
        boolean[] match = new boolean[autoConfigurationClasses.length];
        for (int i = 0; i < autoConfigurationClasses.length; i++) {
            if (ElasticsearchAutoConfiguration.class.getName().equals(autoConfigurationClasses[i])) {
                match[i] = false;
            } else {
                match[i] = true;
            }
        }
        return match;
    }

}
