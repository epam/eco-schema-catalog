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
package com.epam.eco.schemacatalog.client;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Andrei_Tytsik
 */
public final class SchemaRegistryServiceInfo {

    private static final String PROPS_PATH = "/schemaregistry-info.properties";
    private static final String PROP_VERSION = "version";

    private static final String VERSION;
    static {
        try (InputStream props = SchemaRegistryServiceInfo.class.getResourceAsStream(PROPS_PATH)){
            Properties properties = new Properties();
            properties.load(props);
            VERSION = properties.getProperty(PROP_VERSION);
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to read schemaregistry serviceinfo properties", ex);
        }
    }

    private final String version;
    private final List<String> baseUrls;

    private SchemaRegistryServiceInfo(
            @JsonProperty("version") String version,
            @JsonProperty("baseUrls") List<String> baseUrls) {
        Validate.notBlank(version, "Version is blank");
        Validate.notEmpty(baseUrls, "List of base URLs is null or empty");
        Validate.noNullElements(baseUrls, "List of base URLs contains null elements");

        this.version = version;
        this.baseUrls = Collections.unmodifiableList(new ArrayList<>(baseUrls));
    }

    public String getVersion() {
        return version;
    }
    public List<String> getBaseUrls() {
        return baseUrls;
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, baseUrls);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        SchemaRegistryServiceInfo that = (SchemaRegistryServiceInfo)obj;
        return
                Objects.equals(this.version, that.version) &&
                Objects.equals(this.baseUrls, that.baseUrls);
    }

    @Override
    public String toString() {
        return
                "{version: " + version +
                ", baseUrls: " + baseUrls +
                "}";
    }

    public static SchemaRegistryServiceInfo with(String baseUrl) {
        return new SchemaRegistryServiceInfo(VERSION, Collections.singletonList(baseUrl));
    }

    public static SchemaRegistryServiceInfo with(List<String> baseUrls) {
        return new SchemaRegistryServiceInfo(VERSION, baseUrls);
    }

}
