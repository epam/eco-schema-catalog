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

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.epam.eco.schemacatalog.utils.EcoIdUtils;

/**
 * @author Andrei_Tytsik
 */
public class IdentitySchemaInfo implements SchemaInfo {

    protected final String subject;
    protected final int version;
    protected final int schemaRegistryId;
    protected final String ecoId;

    public IdentitySchemaInfo(
            @JsonProperty("subject") String subject,
            @JsonProperty("version") int version,
            @JsonProperty("schemaRegistryId") int schemaRegistryId) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.isTrue(version >= 0, "Version is invalid");
        Validate.isTrue(schemaRegistryId >= 0, "SchemaRegistry id is invalid");

        this.subject = subject;
        this.version = version;
        this.schemaRegistryId = schemaRegistryId;

        ecoId = EcoIdUtils.formatId(subject, version);
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public int getSchemaRegistryId() {
        return schemaRegistryId;
    }

    @Override
    public String getEcoId() {
        return ecoId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, version, schemaRegistryId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        IdentitySchemaInfo that = (IdentitySchemaInfo)obj;
        return
                Objects.equals(this.subject, that.subject) &&
                Objects.equals(this.version, that.version) &&
                Objects.equals(this.schemaRegistryId, that.schemaRegistryId);
    }

    @Override
    public String toString() {
        return
                "{subject: " + subject +
                ", version: " + version +
                ", schemaRegistryId: " + schemaRegistryId +
                "}";
    }

    @SuppressWarnings("rawtypes")
    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder<? extends Builder<?>> builder() {
        return builder(null);
    }

    public static Builder<? extends Builder<?>> builder(IdentitySchemaInfo origin) {
        return new Builder<>(origin);
    }

    public static <T extends IdentitySchemaInfo> IdentitySchemaInfo cast(T schemaInfo) {
        return builder(schemaInfo).build();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>> {

        protected String subject;
        protected int version;
        protected int schemaRegistryId;

        protected Builder() {
            this(null);
        }

        protected Builder(IdentitySchemaInfo origin) {
            if (origin == null) {
                return;
            }

            this.subject = origin.subject;
            this.version = origin.version;
            this.schemaRegistryId = origin.schemaRegistryId;
        }

        public T subject(String subject) {
            this.subject = subject;
            return (T)this;
        }

        public T version(int version) {
            this.version = version;
            return (T)this;
        }

        public T schemaRegistryId(int schemaRegistryId) {
            this.schemaRegistryId = schemaRegistryId;
            return (T)this;
        }

        public IdentitySchemaInfo build() {
            return new IdentitySchemaInfo(subject, version, schemaRegistryId);
        }

    }

}
