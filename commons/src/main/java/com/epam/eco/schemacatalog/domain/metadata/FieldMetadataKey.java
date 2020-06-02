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

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Andrei_Tytsik
 */
public final class FieldMetadataKey extends MetadataKey implements Comparable<FieldMetadataKey> {

    private final String schemaFullName;
    private final String field;

    @JsonCreator
    public FieldMetadataKey(
            @JsonProperty("subject") String subject,
            @JsonProperty("version") int version,
            @JsonProperty("schemaFullName") String schemaFullName,
            @JsonProperty("field") String field) {
        super(MetadataType.FIELD, subject, version);

        Validate.notBlank(schemaFullName, "Schema full name is blank");
        Validate.notBlank(field, "Field is blank");

        this.schemaFullName = schemaFullName;
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public String getSchemaFullName() {
        return schemaFullName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        FieldMetadataKey that = (FieldMetadataKey) obj;
        return
                Objects.equals(schemaFullName, that.schemaFullName) &&
                Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), schemaFullName, field);
    }

    @Override
    public String toString() {
        return
                "{type: " + type +
                ", subject: " + subject +
                ", version: " + version +
                ", schemaFullName: " + schemaFullName +
                ", field: " + field +
                "}";
    }

    @Override
    public int compareTo(FieldMetadataKey other) {
        int result = ObjectUtils.compare(this.subject, other.subject);
        if (result == 0) {
            result = ObjectUtils.compare(this.version, other.version);
        }
        if (result == 0) {
            result = ObjectUtils.compare(this.schemaFullName, other.schemaFullName);
        }
        if (result == 0) {
            result = ObjectUtils.compare(this.field, other.field);
        }
        return result;
    }

    public static FieldMetadataKey with(
            String subject,
            int version,
            String schemaFullName,
            String field) {
        return new FieldMetadataKey(subject, version, schemaFullName, field);
    }

}
