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
package com.epam.eco.schemacatalog.domain.metadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.epam.eco.schemacatalog.serde.jackson.MetadataKeyDeserializer;
import com.epam.eco.schemacatalog.serde.jackson.MetadataKeySerializer;

/**
 * @author Andrei_Tytsik
 */
public final class MetadataBatchUpdateParams {

    @JsonSerialize(keyUsing=MetadataKeySerializer.class)
    @JsonDeserialize(keyUsing=MetadataKeyDeserializer.class)
    private final Map<MetadataKey, MetadataUpdateParams> operations;

    public MetadataBatchUpdateParams(
            @JsonProperty("operations") Map<MetadataKey, MetadataUpdateParams> operations) {
        Validate.notEmpty(operations, "Collection of operations is null or empty");
        Validate.noNullElements(
                operations.keySet(),
                "Collection of operations contains null keys");

        this.operations = Collections.unmodifiableMap(new HashMap<>(operations));
    }

    public Map<MetadataKey, MetadataUpdateParams> getOperations() {
        return operations;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operations);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        MetadataBatchUpdateParams that = (MetadataBatchUpdateParams)obj;
        return
                Objects.equals(this.operations, that.operations);
    }

    @Override
    public String toString() {
        return
                "{operations: " + operations +
                "}";
    }

    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder builder() {
        return builder(null);
    }

    public static Builder builder(MetadataBatchUpdateParams origin) {
        return new Builder(origin);
    }

    public static final class Builder {

        private Map<MetadataKey, MetadataUpdateParams> operations = new HashMap<>();

        private Builder() {
            this(null);
        }

        private Builder(MetadataBatchUpdateParams origin) {
            if (origin == null) {
                return;
            }

            this.operations.putAll(origin.operations);
        }

        public Builder update(MetadataUpdateParams params) {
            Validate.notNull(params, "UpdateMetadataParams object is null");

            operations.put(params.getKey(), params);
            return this;
        }

        public Builder delete(MetadataKey key) {
            Validate.notNull(key, "Key is null");

            operations.put(key, null);
            return this;
        }

        public MetadataBatchUpdateParams build() {
            return new MetadataBatchUpdateParams(operations);
        }

    }

}
