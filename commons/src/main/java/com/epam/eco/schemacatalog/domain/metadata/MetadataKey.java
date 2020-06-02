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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Andrei_Tytsik
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SchemaMetadataKey.class, name = "SCHEMA"),
        @JsonSubTypes.Type(value = FieldMetadataKey.class, name = "FIELD")
})
public abstract class MetadataKey {

    protected final MetadataType type;
    protected final String subject;
    protected final int version;

    protected MetadataKey(MetadataType type, String subject, int version) {
        Validate.notNull(type, "Type is null");
        Validate.notBlank(subject, "Subject is blank");
        Validate.isTrue(version >= 0, "Version is invalid");

        this.type = type;
        this.subject = subject;
        this.version = version;
    }

    public MetadataType getType() {
        return type;
    }
    public String getSubject() {
        return subject;
    }
    public int getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, subject, version);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        MetadataKey that = (MetadataKey)obj;
        return
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.subject, that.subject) &&
                Objects.equals(this.version, that.version);
    }

    @Override
    public String toString() {
        return
                "{type: " + type +
                ", subject: " + subject +
                ", version: " + version +
                "}";
    }

}
