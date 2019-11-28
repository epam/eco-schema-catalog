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
package com.epam.eco.schemacatalog.store.common.kafka;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Andrei_Tytsik
 */
@ConfigurationProperties(prefix = "eco.schemacatalog.store.kafka")
public class KafkaStoreProperties {

    private String bootstrapServers;
    private Map<String, Object> clientConfig;
    private long bootstrapTimeoutInMs = 60000;

    public String getBootstrapServers() {
        return bootstrapServers;
    }
    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }
    public Map<String, Object> getClientConfig() {
        return clientConfig;
    }
    public void setClientConfig(Map<String, Object> clientConfig) {
        this.clientConfig = clientConfig;
    }
    public long getBootstrapTimeoutInMs() {
        return bootstrapTimeoutInMs;
    }
    public void setBootstrapTimeoutInMs(long bootstrapTimeoutInMs) {
        this.bootstrapTimeoutInMs = bootstrapTimeoutInMs;
    }

}
