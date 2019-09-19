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
package com.epam.eco.schemacatalog.domain.schema;

import java.util.List;
import java.util.Objects;

import org.apache.avro.Schema;
import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.epam.eco.commons.avro.AvroUtils;
import com.epam.eco.commons.avro.FieldExtractor;
import com.epam.eco.commons.avro.FieldInfo;

/**
 * @author Andrei_Tytsik
 */
public class BasicSchemaInfo extends IdentitySchemaInfo implements Schemafull {

    protected final String schemaJson;
    protected final Schema schemaAvro;

    public BasicSchemaInfo(
            @JsonProperty("subject") String subject,
            @JsonProperty("version") int version,
            @JsonProperty("schemaRegistryId") int schemaRegistryId,
            @JsonProperty("schemaJson") String schemaJson) {
        super(subject, version, schemaRegistryId);

        Validate.notBlank(schemaJson, "Schema (JSON) is blank");

        this.schemaJson = schemaJson;

        //disabling cache as it eats too much memory and should be optimized...
        //schemaAvro = CachedSchemaParser.parse(schemaJson);
        schemaAvro = AvroUtils.schemaFromJson(schemaJson);
    }

    @Override
    public String getSchemaJson() {
        return schemaJson;
    }

    @JsonIgnore
    @Override
    public Schema getSchemaAvro() {
        return schemaAvro;
    }

    @JsonIgnore
    @Override
    public List<FieldInfo> getSchemaFieldInfosAsList() {
        //disabling cache as it eats too much memory and should be optimized...
        //return CachedFieldExtractor.fromSchema(schemaAvro);
        return FieldExtractor.fromSchema(schemaAvro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), schemaJson);
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        BasicSchemaInfo that = (BasicSchemaInfo)obj;
        return
                Objects.equals(this.schemaJson, that.schemaJson);
    }

    @Override
    public String toString() {
        return
                "{subject: " + subject +
                ", version: " + version +
                ", schemaRegistryId: " + schemaRegistryId +
                ", schemaJson: " + schemaJson +
                "}";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder<? extends Builder<?>> builder() {
        return builder(null);
    }

    public static Builder<? extends Builder<?>> builder(BasicSchemaInfo origin) {
        return new Builder<>(origin);
    }

    public static <T extends BasicSchemaInfo> BasicSchemaInfo cast(T schemaInfo) {
        return builder(schemaInfo).build();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>> extends IdentitySchemaInfo.Builder<T> {

        protected String schemaJson;

        protected Builder() {
        }

        protected Builder(BasicSchemaInfo origin) {
            super(origin);

            if (origin == null) {
                return;
            }

            this.schemaJson = origin.schemaJson;
        }

        public T schemaJson(String schemaJson) {
            this.schemaJson = schemaJson;
            return (T)this;
        }

        @Override
        public BasicSchemaInfo build() {
            return new BasicSchemaInfo(
                    subject,
                    version,
                    schemaRegistryId,
                    schemaJson);
        }

    }

}
