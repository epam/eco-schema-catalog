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
package com.epam.eco.schemacatalog.rest.view;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Raman_Babich
 */
public final class SchemaEntity {

    private final String name;
    private final String namespace;
    private final Set<SchemaField> fields;
    private final boolean root;

    public SchemaEntity(
            @JsonProperty("name") String name,
            @JsonProperty("namespace") String namespace,
            @JsonProperty("fields") Set<SchemaField> fields,
            @JsonProperty("root") boolean root) {
        Validate.notBlank(name, "Name can't be blank");
        Validate.notNull(fields, "Fields can't be null");
        Validate.noNullElements(fields, "Fields can't contain null element");

        this.name = name;
        this.namespace = namespace;
        this.fields = Collections.unmodifiableSet(new HashSet<>(fields));
        this.root = root;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public Set<SchemaField> getFields() {
        return fields;
    }

    public boolean isRoot() {
        return root;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchemaEntity that = (SchemaEntity) o;
        return root == that.root &&
                Objects.equals(name, that.name) &&
                Objects.equals(namespace, that.namespace) &&
                Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, namespace, fields, root);
    }

    @Override
    public String toString() {
        return "SchemaEntity{" +
                "name='" + name + '\'' +
                ", namespace='" + namespace + '\'' +
                ", fields=" + fields +
                ", root=" + root +
                '}';
    }

    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder builder() {
        return builder(null);
    }

    public static Builder builder(SchemaEntity schema) {
        return new Builder(schema);
    }

    public static class Builder {

        private String name;
        private String namespace;
        private Set<SchemaField> fields;
        private boolean root;

        public Builder() {
            this(null);
        }

        public Builder(SchemaEntity schema) {
            if (schema == null) {
                return;
            }

            this.name = schema.name;
            this.namespace = schema.namespace;
            this.fields = new HashSet<>(schema.fields);
            this.root = schema.root;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder namespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public Builder addField(SchemaField field) {
            if (this.fields == null) {
                this.fields = new HashSet<>();
            }
            this.fields.add(field);
            return this;
        }

        public Builder fields(Set<SchemaField> fields) {
            if (fields == null) {
                this.fields = null;
                return this;
            }
            if (this.fields == null) {
                this.fields = new HashSet<>();
            } else {
                this.fields.clear();
            }
            this.fields.addAll(fields);
            return this;
        }

        public Builder root(boolean root) {
            this.root = root;
            return this;
        }

        public SchemaEntity build() {
            return new SchemaEntity(name, namespace, fields, root);
        }
    }

}
