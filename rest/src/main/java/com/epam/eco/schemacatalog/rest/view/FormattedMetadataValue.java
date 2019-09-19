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
package com.epam.eco.schemacatalog.rest.view;

import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;
import com.epam.eco.schemacatalog.domain.metadata.format.DocFormatter;
import com.epam.eco.schemacatalog.domain.metadata.format.PartFormatter;

/**
 * @author Raman_Babich
 */
public final class FormattedMetadataValue {

    private final String doc;
    private final String formattedDoc;
    private final Map<String, Object> attributes;
    private final Date updatedAt;
    private final String updatedBy;

    public FormattedMetadataValue(
            @JsonProperty("doc") String doc,
            @JsonProperty("formattedDoc") String formattedDoc,
            @JsonProperty("attributes") Map<String, Object> attributes,
            @JsonProperty("updateAt") Date updatedAt,
            @JsonProperty("updateBy") String updatedBy) {
        this.doc = doc;
        this.formattedDoc = formattedDoc;
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

    public String getFormattedDoc() {
        return formattedDoc;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormattedMetadataValue that = (FormattedMetadataValue) o;
        return Objects.equals(doc, that.doc) &&
                Objects.equals(formattedDoc, that.formattedDoc) &&
                Objects.equals(attributes, that.attributes) &&
                Objects.equals(updatedAt, that.updatedAt) &&
                Objects.equals(updatedBy, that.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doc, formattedDoc, attributes, updatedAt, updatedBy);
    }

    @Override
    public String toString() {
        return "FormattedMetadataValue{" +
                "doc='" + doc + '\'' +
                ", formattedDoc='" + formattedDoc + '\'' +
                ", attributes=" + attributes +
                ", updatedAt=" + updatedAt +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }

    @SuppressWarnings("rawtypes")
    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder<? extends Builder<?>> builder() {
        return builder(null);
    }

    public static Builder<? extends Builder<?>> builder(FormattedMetadataValue origin) {
        return new Builder<>(origin);
    }

    public static FormattedMetadataValue from(MetadataValue value, PartFormatter formatter) {
        if (value == null) {
            return null;
        }

        return FormattedMetadataValue.builder()
                .doc(value.getDoc(), formatter)
                .updatedAt(value.getUpdatedAt())
                .updatedBy(value.getUpdatedBy())
                .attributes(value.getAttributes())
                .build();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>> {

        protected String doc;
        protected String formattedDoc;
        protected Map<String, Object> attributes = new HashMap<>();
        protected Date updatedAt;
        protected String updatedBy;

        protected Builder() {
            this(null);
        }

        protected Builder(FormattedMetadataValue origin) {
            if (origin == null) {
                return;
            }

            this.doc = origin.doc;
            this.attributes.putAll(origin.attributes);
            this.updatedAt = origin.updatedAt != null ? (Date) origin.updatedAt.clone() : null;
            this.updatedBy = origin.updatedBy;
        }

        public T doc(String doc) {
            this.doc = doc;
            return (T) this;
        }

        public T doc(String doc, PartFormatter formatter) {
            this.doc = doc;
            this.formattedDoc = formatter == null ? null : new DocFormatter(this.doc).format(formatter);
            return (T) this;
        }

        public T formattedDoc(String formattedDoc) {
            this.formattedDoc = formattedDoc;
            return (T) this;
        }

        public T attribute(String key, Object value) {
            return attributes(Collections.singletonMap(key, value));
        }

        public T attributes(Map<String, ?> attributes) {
            this.attributes.clear();
            if (attributes != null) {
                this.attributes.putAll(attributes);
            }
            return (T) this;
        }

        public T appendAttribute(String key, Object value) {
            return appendAttributes(Collections.singletonMap(key, value));
        }

        public T appendAttributes(Map<String, ?> attributes) {
            if (attributes != null) {
                this.attributes.putAll(attributes);
            }
            return (T) this;
        }

        public T updatedAtNow() {
            return updatedAt(new Date());
        }

        public T updatedAt(Date updatedAt) {
            this.updatedAt = updatedAt != null ? (Date) updatedAt.clone() : null;
            return (T) this;
        }

        public T updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return (T) this;
        }

        public T updatedBy(Principal principal) {
            this.updatedBy = principal.getName();
            return (T) this;
        }

        public FormattedMetadataValue build() {
            return new FormattedMetadataValue(doc, formattedDoc, attributes, updatedAt, updatedBy);
        }

    }
}
