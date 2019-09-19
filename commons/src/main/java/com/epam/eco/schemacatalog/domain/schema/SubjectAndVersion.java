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

/**
 * @author Andrei_Tytsik
 */
public final class SubjectAndVersion {

    private final String subject;
    private final int version;

    public SubjectAndVersion(
            @JsonProperty("subject") String subject,
            @JsonProperty("version") int version) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.isTrue(version >= 0, "Version is invalid");

        this.subject = subject;
        this.version = version;
    }

    public String getSubject() {
        return subject;
    }
    public int getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, version);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        SubjectAndVersion that = (SubjectAndVersion)obj;
        return
                Objects.equals(this.subject, that.subject) &&
                Objects.equals(this.version, that.version);
    }

    @Override
    public String toString() {
        return
                "{subject: " + subject +
                ", version: " + version +
                "}";
    }

    public static SubjectAndVersion with(String subject, int version) {
        return new SubjectAndVersion(subject, version);
    }

    public static SubjectAndVersion of(SchemaInfo schemaInfo) {
        Validate.notNull(schemaInfo, "Schema Info is null");

        return new SubjectAndVersion(schemaInfo.getSubject(), schemaInfo.getVersion());
    }

}
