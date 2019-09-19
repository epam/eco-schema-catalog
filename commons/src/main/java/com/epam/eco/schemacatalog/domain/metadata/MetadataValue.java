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
package com.epam.eco.schemacatalog.domain.metadata;

import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.epam.eco.schemacatalog.domain.metadata.format.DocFormatter;
import com.epam.eco.schemacatalog.domain.metadata.format.PartFormatter;

/**
 * @author Andrei_Tytsik
 */
public final class MetadataValue {

    private final String doc;
    private final Map<String, Object> attributes;
    private final Date updatedAt;
    private final String updatedBy;

    public MetadataValue(
            @JsonProperty("doc") String doc,
            @JsonProperty("attributes") Map<String, ?> attributes,
            @JsonProperty("updatedAt") Date updatedAt,
            @JsonProperty("updatedBy") String updatedBy) {
        this.doc = doc;
        this.attributes =
                attributes != null ?
                Collections.unmodifiableMap(new TreeMap<>(attributes)) :
                Collections.emptyMap();
        this.updatedAt = updatedAt != null ? (Date) updatedAt.clone() : null;
        this.updatedBy = updatedBy;
    }

    public String getDoc() {
        return doc;
    }
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    public Date getUpdatedAt() {
        return updatedAt != null ? (Date) updatedAt.clone() : null;
    }
    public String getUpdatedBy() {
        return updatedBy;
    }
    public Object getAttribute(String name) {
        Validate.notNull(name, "Name is null");

        return attributes.get(name);
    }

    public MetadataValue format() {
        return format(null);
    }

    public MetadataValue format(PartFormatter partFormatter) {
        return toBuilder().
                doc(DocFormatter.format(doc, partFormatter)).
                build();
    }

    @Override
    public int hashCode() {
        return Objects.hash(doc, attributes, updatedAt, updatedBy);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        MetadataValue that = (MetadataValue)obj;
        return
                Objects.equals(this.doc, that.doc) &&
                Objects.equals(this.attributes, that.attributes) &&
                Objects.equals(this.updatedAt, that.updatedAt) &&
                Objects.equals(this.updatedBy, that.updatedBy);
    }

    @Override
    public String toString() {
        return
                "{doc: " + doc +
                ", attributes: " + attributes +
                ", updatedAt: " + updatedAt +
                ", updatedBy: " + updatedBy +
                "}";
    }


    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder builder() {
        return builder(null);
    }

    public static Builder builder(MetadataValue origin) {
        return new Builder(origin);
    }

    public static class Builder {

        private String doc;
        private Map<String, Object> attributes = new HashMap<>();
        private Date updatedAt;
        private String updatedBy;

        public Builder() {
            this(null);
        }

        public Builder(MetadataValue origin) {
            if (origin == null) {
                return;
            }

            this.doc = origin.doc;
            this.attributes.putAll(origin.attributes);
            this.updatedAt = origin.updatedAt != null ? (Date) origin.updatedAt.clone() : null;
            this.updatedBy = origin.updatedBy;
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

        public Builder updatedAtNow() {
            return updatedAt(new Date());
        }

        public Builder updatedAt(Date updatedAt) {
            this.updatedAt = updatedAt != null ? (Date) updatedAt.clone() : null;
            return this;
        }

        public Builder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public Builder updatedBy(Principal principal) {
            this.updatedBy = principal.getName();
            return this;
        }

        public MetadataValue build() {
            return new MetadataValue(doc, attributes, updatedAt, updatedBy);
        }

    }

}
