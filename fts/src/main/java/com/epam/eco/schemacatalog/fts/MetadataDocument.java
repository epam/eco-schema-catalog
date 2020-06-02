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
package com.epam.eco.schemacatalog.fts;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Andrei_Tytsik
 */
public final class MetadataDocument {

    private Set<String> doc;
    private Set<KeyValue> attribute;
    private Set<String> updatedBy;

    public Set<String> getDoc() {
        return doc;
    }
    public void setDoc(Set<String> doc) {
        this.doc = doc;
    }
    public Set<KeyValue> getAttribute() {
        return attribute;
    }
    public void setAttribute(Set<KeyValue> attribute) {
        this.attribute = attribute;
    }
    public Set<String> getUpdatedBy() {
        return updatedBy;
    }
    public void setUpdatedBy(Set<String> updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void addDoc(String doc) {
        doc = StringUtils.stripToNull(doc);
        if (doc == null) {
            return;
        }

        if (this.doc == null) {
            this.doc = new HashSet<>();
        }
        this.doc.add(doc);
    }

    public void addAttributes(String key, Collection<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }

        values.forEach(value -> addAttribute(key, value));

    }

    public void addAttribute(String key, String value) {
        key = StringUtils.stripToNull(key);
        if (key == null || value == null) {
            return;
        }
        value = StringUtils.stripToNull(value);
        if (value == null) {
            return;
        }

        if (this.attribute == null) {
            this.attribute = new HashSet<>();
        }
        this.attribute.add(new KeyValue(key, value));
    }

    public void addUpdatedBy(String updatedBy) {
        updatedBy = StringUtils.stripToNull(updatedBy);
        if (updatedBy == null) {
            return;
        }

        if (this.updatedBy == null) {
            this.updatedBy = new HashSet<>();
        }
        this.updatedBy.add(updatedBy);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MetadataDocument that = (MetadataDocument) obj;
        return
                Objects.equals(this.doc, that.doc) &&
                Objects.equals(this.attribute, that.attribute) &&
                Objects.equals(this.updatedBy, that.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doc, attribute, updatedBy);
    }

    @Override
    public String toString() {
        return
                "{doc: " + doc +
                ", attribute: " + attribute +
                ", updatedBy: " + updatedBy +
                "}";
    }

}
