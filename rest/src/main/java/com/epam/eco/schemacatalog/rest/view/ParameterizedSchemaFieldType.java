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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.apache.avro.Schema;
import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.epam.eco.commons.avro.AvroUtils;

/**
 * @author Raman_Babich
 */
public final class ParameterizedSchemaFieldType implements SchemaFieldType {

    private final Schema.Type type;
    private final String logicalType;
    private final List<SchemaFieldType> parameters;

    private final String fullName;

    public ParameterizedSchemaFieldType(
            @JsonProperty("type") Schema.Type type,
            @JsonProperty("logicalType") String logicalType,
            @JsonProperty("parameters") List<SchemaFieldType> parameters) {
        Validate.notNull(type, "Type can't be null");
        Validate.isTrue(AvroUtils.isParametrized(type), "Type should be parametrized");
        Validate.notNull(parameters, "Parameters can't be null");
        Validate.noNullElements(parameters, "Parameters can't contains null elements");
        Validate.notEmpty(parameters, "Parameters can't be empty");

        this.type = type;
        this.logicalType = logicalType;
        this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters));
        this.fullName = createFullName(this.type, this.logicalType, this.parameters);
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

    public List<SchemaFieldType> getParameters() {
        return parameters;
    }

    private String createFullName(Schema.Type type, String logicalType, List<SchemaFieldType> parameters) {

        String typePart;
        if (logicalType == null) {
            typePart = type.getName() ;
        } else {
            typePart = String.format(TYPE_NAME_WITH_LOGICAL_TYPE_FORMAT, type.getName(), logicalType);
        }
        StringJoiner paramJoiner = new StringJoiner(", ", typePart + "<", ">");
        parameters.forEach(param -> paramJoiner.add(param.getFullName()));
        return paramJoiner.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParameterizedSchemaFieldType that = (ParameterizedSchemaFieldType) o;
        return type == that.type &&
                Objects.equals(logicalType, that.logicalType) &&
                Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, logicalType, parameters);
    }

    @Override
    public String toString() {
        return "ParameterizedSchemaFieldType{" +
                "type=" + type +
                ", logicalType='" + logicalType + '\'' +
                ", parameters=" + parameters +
                ", fullName='" + fullName + '\'' +
                '}';
    }

    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder builder() {
        return builder(null);
    }

    public static Builder builder(ParameterizedSchemaFieldType type) {
        return new Builder(type);
    }

    public static class Builder {

        private Schema.Type type;
        private String logicalType;
        private List<SchemaFieldType> parameters;

        public Builder(ParameterizedSchemaFieldType type) {
            if (type == null) {
                return;
            }

            this.type = type.type;
            this.logicalType = type.logicalType;
            this.parameters = new ArrayList<>(type.parameters);
        }

        public Builder type(Schema.Type type) {
            this.type = type;
            return this;
        }

        public Builder logicalType(String logicalType) {
            this.logicalType = logicalType;
            return this;
        }

        public Builder addParameter(SchemaFieldType parameter) {
            if (this.parameters == null) {
                this.parameters = new ArrayList<>();
            }
            this.parameters.add(parameter);
            return this;
        }

        public Builder parameters(List<SchemaFieldType> parameters) {
            if (parameters == null) {
                this.parameters = null;
                return this;
            }
            if (this.parameters == null) {
                this.parameters = new ArrayList<>();
            } else {
                this.parameters.clear();
            }
            this.parameters.addAll(parameters);
            return this;
        }

        public ParameterizedSchemaFieldType build() {
            return new ParameterizedSchemaFieldType(type, logicalType, parameters);
        }
    }
}
