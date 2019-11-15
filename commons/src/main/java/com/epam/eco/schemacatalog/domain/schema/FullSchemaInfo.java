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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.epam.eco.schemacatalog.domain.metadata.Metadata;
import com.epam.eco.schemacatalog.domain.metadata.MetadataAware;
import com.epam.eco.schemacatalog.domain.metadata.MetadataBrowser;
import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;
import com.epam.eco.schemacatalog.domain.metadata.format.PartFormatter;
import com.epam.eco.schemacatalog.serde.jackson.MetadataKeyDeserializer;
import com.epam.eco.schemacatalog.serde.jackson.MetadataKeySerializer;

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

/**
 * @author Andrei_Tytsik
 */
public class FullSchemaInfo extends BasicSchemaInfo implements MetadataAware<FullSchemaInfo> {

    private final AvroCompatibilityLevel compatibilityLevel;
    private final Mode mode;
    private final boolean deleted;
    private final boolean versionLatest;
    @JsonSerialize(keyUsing=MetadataKeySerializer.class)
    @JsonDeserialize(keyUsing=MetadataKeyDeserializer.class)
    private final Map<MetadataKey, MetadataValue> metadata;

    private final MetadataBrowser<FullSchemaInfo> metadataBrowser;

    public FullSchemaInfo(
            @JsonProperty("subject") String subject,
            @JsonProperty("version") int version,
            @JsonProperty("schemaRegistryId") int schemaRegistryId,
            @JsonProperty("schemaJson") String schemaJson,
            @JsonProperty("compatibilityLevel") AvroCompatibilityLevel compatibilityLevel,
            @JsonProperty("mode") Mode mode,
            @JsonProperty("deleted") boolean deleted,
            @JsonProperty("versionLatest") boolean versionLatest,
            @JsonProperty("metadata") Map<MetadataKey, MetadataValue> metadata) {
        super(subject, version, schemaRegistryId, schemaJson);

        Validate.notNull(compatibilityLevel, "Compatibility level is null");
        Validate.notNull(mode, "Mode is null");

        this.compatibilityLevel = compatibilityLevel;
        this.mode = mode;
        this.deleted = deleted;
        this.versionLatest = versionLatest;
        this.metadata =
                metadata != null ?
                Collections.unmodifiableMap(new HashMap<>(metadata)) :
                Collections.emptyMap();

        metadataBrowser = new MetadataBrowser<>(this);
    }

    public AvroCompatibilityLevel getCompatibilityLevel() {
        return compatibilityLevel;
    }
    public Mode getMode() {
        return mode;
    }
    public boolean isDeleted() {
        return deleted;
    }
    public boolean isVersionLatest() {
        return versionLatest;
    }
    @Override
    public Map<MetadataKey, MetadataValue> getMetadata() {
        return metadata;
    }

    @JsonIgnore
    @Override
    public MetadataBrowser<FullSchemaInfo> getMetadataBrowser() {
        return metadataBrowser;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                compatibilityLevel,
                mode,
                deleted,
                versionLatest,
                metadata);
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        FullSchemaInfo that = (FullSchemaInfo)obj;
        return
                Objects.equals(this.compatibilityLevel, that.compatibilityLevel) &&
                Objects.equals(this.mode, that.mode) &&
                Objects.equals(this.deleted, that.deleted) &&
                Objects.equals(this.versionLatest, that.versionLatest) &&
                Objects.equals(this.metadata, that.metadata);
    }

    @Override
    public String toString() {
        return
                "{subject: " + subject +
                ", version: " + version +
                ", schemaRegistryId: " + schemaRegistryId +
                ", schemaJson: " + schemaJson +
                ", compatibilityLevel: " + compatibilityLevel +
                ", mode: " + mode +
                ", deleted: " + deleted +
                ", versionLatest: " + versionLatest +
                ", metadata: " + metadata +
                "}";
    }

    public BasicSchemaInfo toBasic() {
        return BasicSchemaInfo.builder(this).build();
    }

    public IdentitySchemaInfo toIdentity() {
        return IdentitySchemaInfo.builder(this).build();
    }

    @Override
    public FullSchemaInfo toSchemaWithFormattedMetadata(PartFormatter partFormatter) {
        return FullSchemaInfo.builder(this)
                .metadata(formatMetadata(partFormatter))
                .build();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder<? extends Builder<?>> builder() {
        return builder(null);
    }

    public static Builder<? extends Builder<?>> builder(FullSchemaInfo origin) {
        return new Builder<>(origin);
    }

    public static <T extends FullSchemaInfo> FullSchemaInfo cast(T schemaInfo) {
        return builder(schemaInfo).build();
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Builder<T>> extends BasicSchemaInfo.Builder<T> {

        protected AvroCompatibilityLevel compatibilityLevel;
        protected Mode mode;
        protected boolean deleted = false;
        protected boolean versionLatest = false;
        protected Map<MetadataKey, MetadataValue> metadata = new HashMap<>();

        protected Builder() {
        }

        protected Builder(FullSchemaInfo origin) {
            super(origin);

            if (origin == null) {
                return;
            }

            this.compatibilityLevel = origin.compatibilityLevel;
            this.mode = origin.mode;
            this.deleted = origin.deleted;
            this.versionLatest = origin.versionLatest;
            this.metadata.putAll(origin.metadata);
        }

        public T compatibilityLevel(String compatibilityLevel) {
            return compatibilityLevel(AvroCompatibilityLevel.valueOf(compatibilityLevel));
        }

        public T compatibilityLevel(AvroCompatibilityLevel compatibilityLevel) {
            this.compatibilityLevel = compatibilityLevel;
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

        public T versionLatest(boolean versionLatest) {
            this.versionLatest = versionLatest;
            return (T)this;
        }

        public T metadata(Map<MetadataKey, MetadataValue> metadata) {
            this.metadata.clear();
            if (metadata != null) {
                this.metadata.putAll(metadata);
            }
            return (T)this;
        }

        public T appendMetadata(MetadataKey key, MetadataValue value) {
            metadata.put(key, value);
            return (T)this;
        }

        public T appendMetadata(Metadata metadata) {
            if (metadata != null) {
                this.metadata.put(metadata.getKey(), metadata.getValue());
            }
            return (T)this;
        }

        @Override
        public FullSchemaInfo build() {
            return new FullSchemaInfo(
                    subject,
                    version,
                    schemaRegistryId,
                    schemaJson,
                    compatibilityLevel,
                    mode,
                    deleted,
                    versionLatest,
                    metadata);
        }

    }

}
