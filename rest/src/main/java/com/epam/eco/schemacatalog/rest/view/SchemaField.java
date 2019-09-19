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

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Raman_Babich
 */
public final class SchemaField {

    private final String name;
    private final SchemaFieldType type;
    private final Object defaultValue;
    private final boolean defaultValuePresent;
    private final String nativeDoc;
    private final FormattedMetadata metadata;

    public SchemaField(
            @JsonProperty("name") String name,
            @JsonProperty("type") SchemaFieldType type,
            @JsonProperty("defaultValue") Object defaultValue,
            @JsonProperty("defaultValuePresent") boolean defaultValuePresent,
            @JsonProperty("nativeDoc") String nativeDoc,
            @JsonProperty("metadata") FormattedMetadata metadata) {
        Validate.notBlank(name, "Name can't be null");
        Validate.notNull(type, "Type can't be null");

        if (!defaultValuePresent && defaultValue != null) {
            throw new IllegalArgumentException("Inconsistent default value");
        }

        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.defaultValuePresent = defaultValuePresent;
        this.nativeDoc = nativeDoc;
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }
    public SchemaFieldType getType() {
        return type;
    }
    public Object getDefaultValue() {
        return defaultValue;
    }
    public boolean isDefaultValuePresent() {
        return defaultValuePresent;
    }
    public String getNativeDoc() {
        return nativeDoc;
    }
    public FormattedMetadata getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchemaField that = (SchemaField) o;
        return
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(defaultValue, that.defaultValue) &&
                Objects.equals(defaultValuePresent, that.defaultValuePresent) &&
                Objects.equals(nativeDoc, that.nativeDoc) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, defaultValue, defaultValuePresent, nativeDoc, metadata);
    }

    @Override
    public String toString() {
        return "SchemaField{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", defaultValue=" + defaultValue +
                ", defaultValuePresent=" + defaultValuePresent +
                ", nativeDoc='" + nativeDoc + '\'' +
                ", metadata=" + metadata +
                '}';
    }

    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder builder() {
        return builder(null);
    }

    public static Builder builder(SchemaField field) {
        return new Builder(field);
    }

    public static class Builder {

        private String name;
        private SchemaFieldType type;
        private Object defaultValue;
        private boolean defaultValuePresent = false;
        private String nativeDoc;
        private FormattedMetadata metadata;

        public Builder(SchemaField field) {
            if (field == null) {
                return;
            }

            this.name = field.name;
            this.type = field.type;
            this.defaultValue = field.defaultValue;
            this.defaultValuePresent = field.defaultValuePresent;
            this.nativeDoc = field.nativeDoc;
            this.metadata = field.metadata;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(SchemaFieldType type) {
            this.type = type;
            return this;
        }

        public Builder defaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
            if (defaultValue != null) {
                defaultValuePresent(true);
            }
            return this;
        }

        public Builder defaultValuePresent(boolean defaultValuePresent) {
            this.defaultValuePresent = defaultValuePresent;
            return this;
        }

        public Builder nativeDoc(String nativeDoc) {
            this.nativeDoc = nativeDoc;
            return this;
        }

        public Builder metadata(FormattedMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public SchemaField build() {
            return new SchemaField(
                    name,
                    type,
                    defaultValue,
                    defaultValuePresent,
                    nativeDoc,
                    metadata);
        }
    }

}