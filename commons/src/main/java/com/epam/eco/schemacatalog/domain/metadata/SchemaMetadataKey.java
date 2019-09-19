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
package com.epam.eco.schemacatalog.domain.metadata;

import org.apache.commons.lang3.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Andrei_Tytsik
 */
public final class SchemaMetadataKey extends MetadataKey implements Comparable<SchemaMetadataKey> {

    @JsonCreator
    public SchemaMetadataKey(
            @JsonProperty("subject") String subject,
            @JsonProperty("version") int version) {
        super(MetadataType.SCHEMA, subject, version);
    }

    public static SchemaMetadataKey with(String subject, int version) {
        return new SchemaMetadataKey(subject, version);
    }

    @Override
    public int compareTo(SchemaMetadataKey other) {
        int result = ObjectUtils.compare(this.subject, other.subject);
        if (result == 0) {
            result = ObjectUtils.compare(this.version, other.version);
        }
        return result;
    }

}
