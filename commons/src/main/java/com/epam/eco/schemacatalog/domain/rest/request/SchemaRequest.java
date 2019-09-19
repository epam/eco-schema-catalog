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
package com.epam.eco.schemacatalog.domain.rest.request;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Raman_Babich
 */
public final class SchemaRequest {

    private final String subject;
    private final String schemaJson;

    public SchemaRequest(
            @JsonProperty("subject") String subject,
            @JsonProperty("schemaJson") String schemaJson) {
        this.subject = subject;
        this.schemaJson = schemaJson;
    }

    public String getSubject() {
        return subject;
    }

    public String getSchemaJson() {
        return schemaJson;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SchemaRequest that = (SchemaRequest) obj;
        return
                Objects.equals(subject, that.subject) &&
                Objects.equals(schemaJson, that.schemaJson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, schemaJson);
    }

    @Override
    public String toString() {
        return "SchemaRequest{" +
                "subject='" + subject + '\'' +
                ", schemaJson='" + schemaJson + '\'' +
                '}';
    }

}
