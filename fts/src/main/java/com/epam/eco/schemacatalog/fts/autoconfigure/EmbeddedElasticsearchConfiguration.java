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

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.plugins.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Andrei_Tytsik
 */
@Configuration
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class EmbeddedElasticsearchConfiguration {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmbeddedElasticsearchConfiguration.class);

    private static final String PATH_HOME = "path.home";
    private static final String PATH_DATA = "path.data";
    private static final String CLUSTER_NAME = "cluster.name";
    private static final String TRANSPORT_TYPE = "transport.type";
    private static final String HTTP_ENABLED = "http.enabled";

    private static final Settings EMBEDDED_SETTINGS = Settings.builder().
            put(TRANSPORT_TYPE, "local").
            put(HTTP_ENABLED, false).
            build();

    private static final Settings DEFAULT_SETTINGS = Settings.builder().
            put(PATH_HOME, System.getProperty("user.dir")).
            put(PATH_DATA, System.getProperty("java.io.tmpdir") + "/sc_elastic_data").
            put(CLUSTER_NAME, "schema_catalog").
            build();

    private final ElasticsearchProperties properties;

    public EmbeddedElasticsearchConfiguration(ElasticsearchProperties properties) {
        this.properties = properties;

        cleanUpData();
    }

    @Bean(initMethod="start", destroyMethod="close")
    public Node node() throws Exception {
        Settings settings = buildSettings();
        Collection<Class<? extends Plugin>> plugins = Collections.emptyList();
        return new PluginConfigurableNode(settings, plugins);
    }

    @Bean(destroyMethod="close")
    public Client client(Node node) throws Exception {
        return node.client();
    }

    private Settings buildSettings() {
        Settings.Builder builder = Settings.builder();
        builder.put(DEFAULT_SETTINGS);
        builder.put(EMBEDDED_SETTINGS);
        for (Map.Entry<String, String> entry : properties.getProperties().entrySet()) {
            if (EMBEDDED_SETTINGS.hasValue(entry.getKey())) {
                LOGGER.warn("{} property is unchangeable, ignoring...", entry.getKey());
                continue;
            }
            builder.put(entry.getKey(), entry.getValue());
        }
        if (!StringUtils.isBlank(properties.getClusterName())) {
            builder.put(CLUSTER_NAME, properties.getClusterName());
        }
        return builder.build();
    }

    private void cleanUpData() {
        String path = properties.getProperties().get(PATH_DATA);
        path = !StringUtils.isBlank(path) ? path : DEFAULT_SETTINGS.get(PATH_DATA);

        FileUtils.deleteQuietly(new File(path));
        LOGGER.info("ElasticSearch data directory '{}' was cleaned-up", path);
    }

    private static class PluginConfigurableNode extends Node {
        PluginConfigurableNode(
                Settings settings,
                Collection<Class<? extends Plugin>> classpathPlugins) {
            super(
                    InternalSettingsPreparer.prepareEnvironment(settings, null),
                    classpathPlugins);
        }
    }

}
