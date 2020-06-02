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
package com.epam.eco.schemacatalog.rest.view;

import java.util.Objects;

import org.apache.avro.Schema;
import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.epam.eco.commons.avro.AvroUtils;

/**
 * @author Raman_Babich
 */
public final class PrimitiveSchemaFieldType implements SchemaFieldType {

    private final Schema.Type type;
    private final String logicalType;

    private final String fullName;

    public PrimitiveSchemaFieldType(
            @JsonProperty("type") Schema.Type type,
            @JsonProperty("logicalType") String logicalType) {
        Validate.notNull(type, "Type can't be null");
        Validate.isTrue(AvroUtils.isPrimitive(type), "Type should be primitive");

        this.type = type;
        this.logicalType = logicalType;
        this.fullName = createFullName(this.type, this.logicalType);
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

    private String createFullName(Schema.Type type, String logicalType) {
        if (logicalType == null) {
            return type.getName();
        }
        return String.format(TYPE_NAME_WITH_LOGICAL_TYPE_FORMAT, type.getName(), logicalType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrimitiveSchemaFieldType that = (PrimitiveSchemaFieldType) o;
        return type == that.type &&
                Objects.equals(logicalType, that.logicalType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, logicalType);
    }

    @Override
    public String toString() {
        return "PrimitiveSchemaFieldType{" +
                "type=" + type +
                ", logicalType='" + logicalType + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }

    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder builder() {
        return builder(null);
    }

    public static Builder builder(PrimitiveSchemaFieldType type) {
        return new Builder(type);
    }

    public static class Builder {

        private Schema.Type type;
        private String logicalType;

        public Builder(PrimitiveSchemaFieldType type) {
            if (type == null) {
                return;
            }

            this.type = type.type;
            this.logicalType = type.logicalType;
        }

        public Builder type(Schema.Type type) {
            this.type = type;
            return this;
        }

        public Builder logicalType(String logicalType) {
            this.logicalType = logicalType;
            return this;
        }

        public PrimitiveSchemaFieldType build() {
            return new PrimitiveSchemaFieldType(type, logicalType);
        }
    }
}
