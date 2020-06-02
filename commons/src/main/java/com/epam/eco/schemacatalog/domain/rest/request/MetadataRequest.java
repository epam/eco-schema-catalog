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
package com.epam.eco.schemacatalog.domain.rest.request;

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Raman_Babich
 */
public final class MetadataRequest {

    private final String doc;
    private final Map<String, Object> attributes;

    public MetadataRequest(
            @JsonProperty("doc") String doc,
            @JsonProperty("attributes") Map<String, Object> attributes) {
        this.doc = doc;
        this.attributes = attributes;
    }

    public String getDoc() {
        return doc;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MetadataRequest that = (MetadataRequest) obj;
        return
                Objects.equals(doc, that.doc) &&
                Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doc, attributes);
    }

    @Override
    public String toString() {
        return "MetadataRequest{" +
                "doc='" + doc + '\'' +
                ", attributes=" + attributes +
                '}';
    }

}
