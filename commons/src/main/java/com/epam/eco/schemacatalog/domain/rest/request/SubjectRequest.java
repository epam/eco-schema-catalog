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

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

/**
 * @author Raman_Babich
 */
public final class SubjectRequest {

    private final AvroCompatibilityLevel compatibilityLevel;

    public SubjectRequest(
            @JsonProperty("compatibilityLevel") AvroCompatibilityLevel compatibilityLevel) {
        this.compatibilityLevel = compatibilityLevel;
    }

    public AvroCompatibilityLevel getCompatibilityLevel() {
        return compatibilityLevel;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SubjectRequest that = (SubjectRequest) obj;
        return
                compatibilityLevel == that.compatibilityLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(compatibilityLevel);
    }

    @Override
    public String toString() {
        return "SubjectRequest{" +
                "compatibilityLevel=" + compatibilityLevel +
                '}';
    }

}
