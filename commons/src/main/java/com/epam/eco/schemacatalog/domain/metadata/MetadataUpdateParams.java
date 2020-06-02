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
package com.epam.eco.schemacatalog.domain.metadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Andrei_Tytsik
 */
public final class MetadataUpdateParams {

    private final MetadataKey key;
    private final String doc;
    private final Map<String, Object> attributes;

    public MetadataUpdateParams(
            @JsonProperty("key") MetadataKey key,
            @JsonProperty("doc") String doc,
            @JsonProperty("attributes") Map<String, ?> attributes) {
        Validate.notNull(key, "Key is null");

        this.key = key;
        this.doc = doc;
        this.attributes =
                attributes != null ?
                Collections.unmodifiableMap(new TreeMap<>(attributes)) :
                Collections.emptyMap();
    }

    public MetadataKey getKey() {
        return key;
    }
    public String getDoc() {
        return doc;
    }
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, doc, attributes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        MetadataUpdateParams that = (MetadataUpdateParams)obj;
        return
                Objects.equals(this.key, that.key) &&
                Objects.equals(this.doc, that.doc) &&
                Objects.equals(this.attributes, that.attributes);
    }

    @Override
    public String toString() {
        return
                "{key: " + key +
                ", doc: " + doc +
                ", attributes: " + attributes +
                "}";
    }

    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder builder() {
        return builder(null);
    }

    public static Builder builder(MetadataUpdateParams origin) {
        return new Builder(origin);
    }

    public static final class Builder {

        private MetadataKey key;
        private String doc;
        private Map<String, Object> attributes = new HashMap<>();

        private Builder() {
            this(null);
        }

        private Builder(MetadataUpdateParams origin) {
            if (origin == null) {
                return;
            }

            this.key = origin.key;
            this.doc = origin.doc;
            this.attributes.putAll(origin.attributes);
        }

        public Builder key(MetadataKey key) {
            this.key = key;
            return this;
        }

        public Builder doc(String doc) {
            this.doc = doc;
            return this;
        }

        public Builder attribute(String key, Object value) {
            return attributes(Collections.singletonMap(key, value));
        }

        public Builder attributes(Map<String, ?> attributes) {
            this.attributes.clear();
            if (attributes != null) {
                this.attributes.putAll(attributes);
            }
            return this;
        }

        public Builder appendAttribute(String key, Object value) {
            return appendAttributes(Collections.singletonMap(key, value));
        }

        public Builder appendAttributes(Map<String, ?> attributes) {
            if (attributes != null) {
                this.attributes.putAll(attributes);
            }
            return this;
        }

        public MetadataUpdateParams build() {
            return new MetadataUpdateParams(key, doc, attributes);
        }

    }

}
