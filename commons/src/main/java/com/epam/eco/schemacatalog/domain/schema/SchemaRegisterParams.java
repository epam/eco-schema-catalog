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
package com.epam.eco.schemacatalog.domain.schema;

import java.util.Objects;

import org.apache.avro.Schema;
import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.epam.eco.commons.avro.AvroUtils;

/**
 * @author Andrei_Tytsik
 */
public final class SchemaRegisterParams {

    private final String subject;
    private final String schemaJson;

    private final Schema schemaAvro;
    // + metadata later...

    public SchemaRegisterParams(
            @JsonProperty("subject") String subject,
            @JsonProperty("schemaJson") String schemaJson) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notBlank(schemaJson, "Schema (JSON) is blank");

        this.subject = subject;
        this.schemaJson = schemaJson;

        //disabling cache as it eats too much memory and should be optimized...
        //schemaAvro = CachedSchemaParser.parse(schemaJson);
        schemaAvro = AvroUtils.schemaFromJson(schemaJson);
    }

    public String getSubject() {
        return subject;
    }
    public String getSchemaJson() {
        return schemaJson;
    }
    @JsonIgnore
    public Schema getSchemaAvro() {
        return schemaAvro;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, schemaJson);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        SchemaRegisterParams that = (SchemaRegisterParams)obj;
        return
                Objects.equals(this.subject, that.subject) &&
                Objects.equals(this.schemaJson, that.schemaJson);
    }

    @Override
    public String toString() {
        return
                "{subject: " + subject +
                ", schemaJson: " + schemaJson +
                "}";
    }

    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder builder() {
        return builder(null);
    }

    public static Builder builder(SchemaRegisterParams origin) {
        return new Builder(origin);
    }

    public static final class Builder {

        private String subject;
        private String schemaJson;

        private Builder() {
            this(null);
        }

        private Builder(SchemaRegisterParams origin) {
            if (origin == null) {
                return;
            }

            this.subject = origin.subject;
            this.schemaJson = origin.schemaJson;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder schemaJson(String schemaJson) {
            this.schemaJson = schemaJson;
            return this;
        }

        public SchemaRegisterParams build() {
            return new SchemaRegisterParams(subject, schemaJson);
        }

    }

}
