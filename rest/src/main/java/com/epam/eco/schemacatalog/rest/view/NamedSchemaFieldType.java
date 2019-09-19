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

import org.apache.avro.Schema;
import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.epam.eco.commons.avro.AvroUtils;

/**
 * @author Raman_Babich
 */
public final class NamedSchemaFieldType implements SchemaFieldType {

    private static final String NAME_NAMESPACE_FORMAT = "%s.%s";

    private final Schema.Type type;
    private final String logicalType;
    private final String name;
    private final String namespace;

    private final String fullName;

    public NamedSchemaFieldType(
            @JsonProperty("type") Schema.Type type,
            @JsonProperty("logicalType") String logicalType,
            @JsonProperty("name") String name,
            @JsonProperty("namespace") String namespace) {
        Validate.notNull(type, "Type can't be null");
        Validate.isTrue(AvroUtils.isNamed(type), "Type should be named");
        Validate.notBlank(name, "Name can't be blank");

        this.type = type;
        this.logicalType = logicalType;
        this.name = name;
        this.namespace = namespace;
        this.fullName = createFullName(this.logicalType, this.name, this.namespace);
    }

    @Override
    public Schema.Type getType() {
        return type;
    }

    @Override
    public String getLogicalType() {
        return logicalType;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    private String createFullName(String logicalType, String name, String namespace) {
        String namePart = name;
        if (namespace != null) {
            namePart = String.format(NAME_NAMESPACE_FORMAT, namespace, name);
        }
        if (logicalType != null) {
            return String.format(TYPE_NAME_WITH_LOGICAL_TYPE_FORMAT, namePart, logicalType);
        }
        return namePart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamedSchemaFieldType that = (NamedSchemaFieldType) o;
        return type == that.type &&
                Objects.equals(logicalType, that.logicalType) &&
                Objects.equals(name, that.name) &&
                Objects.equals(namespace, that.namespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, logicalType, name, namespace);
    }

    @Override
    public String toString() {
        return "NamedSchemaFieldType{" +
                "type=" + type +
                ", logicalType='" + logicalType + '\'' +
                ", name='" + name + '\'' +
                ", namespace='" + namespace + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }

    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder builder() {
        return builder(null);
    }

    public static Builder builder(NamedSchemaFieldType type) {
        return new Builder(type);
    }

    public static class Builder {

        private Schema.Type type;
        private String logicalType;
        private String name;
        private String namespace;

        public Builder(NamedSchemaFieldType type) {
            if (type == null) {
                return;
            }

            this.type = type.type;
            this.logicalType = type.logicalType;
            this.name = type.name;
            this.namespace = type.namespace;
        }

        public Builder type(Schema.Type type) {
            this.type = type;
            return this;
        }

        public Builder logicalType(String logicalType) {
            this.logicalType = logicalType;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder namespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public NamedSchemaFieldType build() {
            return new NamedSchemaFieldType(type, logicalType, name, namespace);
        }
    }
}
