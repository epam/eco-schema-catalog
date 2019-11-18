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

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.epam.eco.schemacatalog.domain.schema.Mode;

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

/**
 * @author Raman_Babich
 */
public final class SchemaProfile {

    private final String subject;
    private final int version;
    private final int schemaRegistryId;
    private final AvroCompatibilityLevel compatibilityLevel;
    private final Mode mode;
    private final boolean versionLatest;
    private final boolean deleted;
    private final FormattedMetadata schemaMetadata;
    private final Set<SchemaEntity> schemas;

    public SchemaProfile(
            @JsonProperty("subject") String subject,
            @JsonProperty("version") int version,
            @JsonProperty("schemaRegistryId") int schemaRegistryId,
            @JsonProperty("compatibilityLevel") AvroCompatibilityLevel compatibilityLevel,
            @JsonProperty("mode") Mode mode,
            @JsonProperty("versionLatest") boolean versionLatest,
            @JsonProperty("schemaMetadata") FormattedMetadata schemaMetadata,
            @JsonProperty("schemas") Set<SchemaEntity> schemas,
            @JsonProperty("deleted") boolean deleted) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.isTrue(version >= 0, "Version is invalid");
        Validate.isTrue(schemaRegistryId >= 0, "SchemaRegistry id is invalid");
        Validate.notNull(compatibilityLevel, "Compatibility level is null");
        Validate.notNull(mode, "Mode is null");
        if (schemas != null) {
            Validate.noNullElements(schemas, "Schemas can't contain null element");
        }

        this.subject = subject;
        this.version = version;
        this.schemaRegistryId = schemaRegistryId;
        this.compatibilityLevel = compatibilityLevel;
        this.mode = mode;
        this.versionLatest = versionLatest;
        this.deleted = deleted;
        this.schemaMetadata = schemaMetadata;
        this.schemas = schemas == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<>(schemas));
    }

    public String getSubject() {
        return subject;
    }

    public int getVersion() {
        return version;
    }

    public int getSchemaRegistryId() {
        return schemaRegistryId;
    }

    public AvroCompatibilityLevel getCompatibilityLevel() {
        return compatibilityLevel;
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

    public FormattedMetadata getSchemaMetadata() {
        return schemaMetadata;
    }

    public Set<SchemaEntity> getSchemas() {
        return schemas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchemaProfile that = (SchemaProfile) o;
        return version == that.version &&
                schemaRegistryId == that.schemaRegistryId &&
                versionLatest == that.versionLatest &&
                deleted == that.deleted &&
                Objects.equals(subject, that.subject) &&
                compatibilityLevel == that.compatibilityLevel &&
                mode == that.mode &&
                Objects.equals(schemaMetadata, that.schemaMetadata) &&
                Objects.equals(schemas, that.schemas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, version, schemaRegistryId,
                compatibilityLevel, mode, versionLatest, deleted, schemaMetadata, schemas);
    }

    @Override
    public String toString() {
        return "SchemaProfile{" +
                "subject='" + subject + '\'' +
                ", version=" + version +
                ", schemaRegistryId=" + schemaRegistryId +
                ", compatibilityLevel=" + compatibilityLevel +
                ", mode=" + mode +
                ", versionLatest=" + versionLatest +
                ", deleted=" + deleted +
                ", schemaMetadata=" + schemaMetadata +
                ", schemas=" + schemas +
                '}';
    }

    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder builder() {
        return builder(null);
    }

    public static Builder builder(SchemaProfile profile) {
        return new Builder(profile);
    }

    public static class Builder {

        private String subject;
        private int version;
        private int schemaRegistryId;
        private AvroCompatibilityLevel compatibilityLevel;
        private Mode mode;
        private boolean versionLatest;
        private boolean deleted;
        private FormattedMetadata schemaMetadata;
        private Set<SchemaEntity> schemas;

        public Builder(SchemaProfile profile) {
            if (profile == null) {
                return;
            }

            this.subject = profile.subject;
            this.version = profile.version;
            this.schemaRegistryId = profile.schemaRegistryId;
            this.compatibilityLevel = profile.compatibilityLevel;
            this.mode = profile.mode;
            this.versionLatest = profile.versionLatest;
            this.deleted = profile.deleted;
            this.schemaMetadata = profile.schemaMetadata;
            this.schemas = new HashSet<>(profile.schemas);
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder version(int version) {
            this.version = version;
            return this;
        }

        public Builder schemaRegistryId(int schemaRegistryId) {
            this.schemaRegistryId = schemaRegistryId;
            return this;
        }

        public Builder compatibilityLevel(AvroCompatibilityLevel compatibilityLevel) {
            this.compatibilityLevel = compatibilityLevel;
            return this;
        }

        public Builder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder versionLatest(boolean versionLatest) {
            this.versionLatest = versionLatest;
            return this;
        }

        public Builder schemaMetadata(FormattedMetadata schemaMetadata) {
            this.schemaMetadata = schemaMetadata;
            return this;
        }

        public Builder deleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Builder addSchema(SchemaEntity schema) {
            if (this.schemas == null) {
                this.schemas = new HashSet<>();
            }
            this.schemas.add(schema);
            return this;
        }

        public Builder schemas(Set<SchemaEntity> schemas) {
            if (schemas == null) {
                this.schemas = null;
                return this;
            }
            if (this.schemas == null) {
                this.schemas = new HashSet<>();
            } else {
                this.schemas.clear();
            }
            this.schemas.addAll(schemas);
            return this;
        }

        public SchemaProfile build() {
            return new SchemaProfile(subject, version, schemaRegistryId, compatibilityLevel, mode,
                    versionLatest, schemaMetadata, schemas, deleted);
        }
    }
}
