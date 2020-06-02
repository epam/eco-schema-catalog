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

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.epam.eco.schemacatalog.domain.metadata.format.PartFormatter;

/**
 * @author Andrei_Tytsik
 */
public final class Metadata {

    private final MetadataKey key;
    private final MetadataValue value;

    public Metadata(
            @JsonProperty("key") MetadataKey key,
            @JsonProperty("value") MetadataValue value) {
        Validate.notNull(key, "Key is null");
        Validate.notNull(value, "Value is null");

        this.key = key;
        this.value = value;
    }

    public MetadataKey getKey() {
        return key;
    }
    public MetadataValue getValue() {
        return value;
    }

    public Metadata format() {
        return format(null);
    }

    public Metadata format(PartFormatter partFormatter) {
        return with(key, value.format(partFormatter));
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Metadata that = (Metadata)obj;
        return
                Objects.equals(this.key, that.key) &&
                Objects.equals(this.value, that.value);
    }

    @Override
    public String toString() {
        return
                "{key: " + key +
                ", value: " + value +
                "}";
    }

    public static Metadata with(MetadataKey key, MetadataValue value) {
        return new Metadata(key, value);
    }

}
