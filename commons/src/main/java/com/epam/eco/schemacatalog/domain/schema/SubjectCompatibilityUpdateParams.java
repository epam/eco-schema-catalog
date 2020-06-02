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

import com.fasterxml.jackson.annotation.JsonProperty;

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

/**
 * @author Andrei_Tytsik
 */
public final class SubjectCompatibilityUpdateParams {

    private final String subject;
    private final AvroCompatibilityLevel compatibilityLevel;

    public SubjectCompatibilityUpdateParams(
            @JsonProperty("subject") String subject,
            @JsonProperty("compatibilityLevel") AvroCompatibilityLevel compatibilityLevel) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(compatibilityLevel, "Compatibility level is null");

        this.subject = subject;
        this.compatibilityLevel = compatibilityLevel;
    }

    public String getSubject() {
        return subject;
    }
    public AvroCompatibilityLevel getCompatibilityLevel() {
        return compatibilityLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, compatibilityLevel);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        SubjectCompatibilityUpdateParams that = (SubjectCompatibilityUpdateParams)obj;
        return
                Objects.equals(this.subject, that.subject) &&
                Objects.equals(this.compatibilityLevel, that.compatibilityLevel);
    }

    @Override
    public String toString() {
        return
                "{subject: " + subject +
                ", compatibilityLevel: " + compatibilityLevel +
                "}";
    }

    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder builder() {
        return builder(null);
    }

    public static Builder builder(SubjectCompatibilityUpdateParams origin) {
        return new Builder(origin);
    }

    public static final class Builder {

        private String subject;
        private AvroCompatibilityLevel compatibilityLevel;

        private Builder() {
            this(null);
        }

        private Builder(SubjectCompatibilityUpdateParams origin) {
            if (origin == null) {
                return;
            }

            this.subject = origin.subject;
            this.compatibilityLevel = origin.compatibilityLevel;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder compatibilityLevel(String compatibilityLevel) {
            return compatibilityLevel(AvroCompatibilityLevel.valueOf(compatibilityLevel));
        }

        public Builder compatibilityLevel(AvroCompatibilityLevel compatibilityLevel) {
            this.compatibilityLevel = compatibilityLevel;
            return this;
        }

        public SubjectCompatibilityUpdateParams build() {
            return new SubjectCompatibilityUpdateParams(subject, compatibilityLevel);
        }

    }

}
