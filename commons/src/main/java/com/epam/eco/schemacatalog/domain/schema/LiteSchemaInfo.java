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

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;

/**
 * @author Raman_Babich
 */
public class LiteSchemaInfo extends IdentitySchemaInfo {

    private final String name;
    private final String namespace;
    private final String fullName;
    private final CompatibilityLevel compatibility;
    private final Mode mode;
    private final boolean versionLatest;
    private final boolean deleted;

    @JsonCreator
    public LiteSchemaInfo(
            @JsonProperty("subject") String subject,
            @JsonProperty("version") int version,
            @JsonProperty("schemaRegistryId") int schemaRegistryId,
            @JsonProperty("name") String name,
            @JsonProperty("namespace") String namespace,
            @JsonProperty("fullName") String fullName,
            @JsonProperty("compatibility") CompatibilityLevel compatibility,
            @JsonProperty("mode") Mode mode,
            @JsonProperty("versionLatest") boolean versionLatest,
            @JsonProperty("deleted") boolean deleted) {
        super(subject, version, schemaRegistryId);

        Validate.notNull(compatibility, "Compatibility is null");
        Validate.notNull(mode, "Mode is null");

        this.name = name;
        this.namespace = namespace;
        this.fullName = fullName;
        this.compatibility = compatibility;
        this.mode = mode;
        this.versionLatest = versionLatest;
        this.deleted = deleted;
    }

    public String getName() {
        return name;
    }
    public String getNamespace() {
        return namespace;
    }
    public String getFullName() {
        return fullName;
    }
    public CompatibilityLevel getCompatibility() {
        return compatibility;
    }
    public Mode getMode() {
        return mode;
    }
    public boolean isVersionLatest() {
        return versionLatest;
    }
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                name,
                namespace,
                fullName,
                compatibility,
                mode,
                versionLatest,
                deleted);
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        LiteSchemaInfo that = (LiteSchemaInfo)obj;
        return
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.namespace, that.namespace) &&
                Objects.equals(this.fullName, that.fullName) &&
                Objects.equals(this.compatibility, that.compatibility) &&
                Objects.equals(this.mode, that.mode) &&
                Objects.equals(this.versionLatest, that.versionLatest) &&
                Objects.equals(this.deleted, that.deleted);
    }

    @Override
    public String toString() {
        return
                "{subject: " + subject +
                ", version: " + version +
                ", schemaRegistryId: " + schemaRegistryId +
                ", name: " + name +
                ", namespace: " + namespace +
                ", fullName: " + fullName +
                ", compatibility: " + compatibility +
                ", mode: " + mode +
                ", versionLatest: " + versionLatest +
                ", deleted: " + deleted +
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

    public static Builder<? extends Builder<?>> builder(LiteSchemaInfo origin) {
        return new Builder<>(origin);
    }

    public static <T extends LiteSchemaInfo> LiteSchemaInfo cast(T schemaInfo) {
        return builder(schemaInfo).build();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>> extends IdentitySchemaInfo.Builder<T> {

        protected String name;
        protected String namespace;
        protected String fullName;
        protected CompatibilityLevel compatibility;
        protected Mode mode;
        protected boolean versionLatest;
        protected boolean deleted;

        protected Builder() {
        }

        protected Builder(LiteSchemaInfo origin) {
            super(origin);

            if (origin == null) {
                return;
            }

            this.name = origin.name;
            this.namespace = origin.namespace;
            this.fullName = origin.fullName;
            this.compatibility = origin.compatibility;
            this.mode = origin.mode;
            this.versionLatest = origin.versionLatest;
            this.deleted = origin.deleted;
        }

        public T name(String name) {
            this.name = name;
            return (T)this;
        }

        public T namespace(String namespace) {
            this.namespace = namespace;
            return (T)this;
        }

        public T fullName(String fullName) {
            this.fullName = fullName;
            return (T)this;
        }

        public T versionLatest(Boolean versionLatest) {
            this.versionLatest = versionLatest;
            return (T)this;
        }

        public T compatibility(String compatibility) {
            return compatibility(CompatibilityLevel.valueOf(compatibility));
        }

        public T compatibility(CompatibilityLevel compatibility) {
            this.compatibility = compatibility;
            return (T)this;
        }

        public T mode(String mode) {
            return mode(Mode.valueOf(mode));
        }

        public T mode(Mode mode) {
            this.mode = mode;
            return (T)this;
        }

        public T deleted(boolean deleted) {
            this.deleted = deleted;
            return (T)this;
        }

        @Override
        public LiteSchemaInfo build() {
            return new LiteSchemaInfo(
                    subject,
                    version,
                    schemaRegistryId,
                    name,
                    namespace,
                    fullName,
                    compatibility,
                    mode,
                    versionLatest,
                    deleted);
        }

    }

}
