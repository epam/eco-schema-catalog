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

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.epam.eco.schemacatalog.domain.metadata.Metadata;
import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;
import com.epam.eco.schemacatalog.domain.metadata.format.PartFormatter;

/**
 * @author Raman_Babich
 */
public final class FormattedMetadata {

    private final MetadataKey key;
    private final FormattedMetadataValue value;

    public FormattedMetadata(
            @JsonProperty("key") MetadataKey key,
            @JsonProperty("value") FormattedMetadataValue value) {
        Validate.notNull(key, "Key can't be null");
        Validate.notNull(value, "Value can't be null");

        this.key = key;
        this.value = value;
    }

    public MetadataKey getKey() {
        return key;
    }

    public FormattedMetadataValue getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormattedMetadata that = (FormattedMetadata) o;
        return
                Objects.equals(key, that.key) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "FormattedMetadata{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }

    public static FormattedMetadata with(MetadataKey key, FormattedMetadataValue value) {
        return new FormattedMetadata(key, value);
    }

    public static FormattedMetadata with(MetadataKey key, MetadataValue value, PartFormatter formatter) {
        return with(key, FormattedMetadataValue.from(value, formatter));
    }

    public static FormattedMetadata with(Metadata metadata, PartFormatter formatter) {
        if (metadata == null) {
            return null;
        }

        return with(metadata.getKey(), metadata.getValue(), formatter);
    }

}
