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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Andrei_Tytsik
 */
public final class SubjectSchemas<T extends SchemaInfo> implements Iterable<T> {

    private final SortedMap<Integer, T> schemaMap;
    private final String subject;

    public SubjectSchemas(
            @JsonProperty("schemas") Collection<T> schemas) {
        Validate.notEmpty(schemas, "Collection of schemas is null or empty");
        Validate.noNullElements(
                schemas,
                "Collection of schemas contains null elements");

        schemaMap = schemas.stream().
                collect(Collectors.toMap(
                        SchemaInfo::getVersion,
                        Function.identity(),
                        (s1,s2) -> s1,
                        TreeMap::new));

        subject = determineSubject(schemas);
    }

    @Override
    public Iterator<T> iterator() {
        return schemaMap.values().iterator();
    }

    public int size() {
        return schemaMap.size();
    }

    public Collection<T> getSchemas() {
        return schemaMap.values();
    }

    @JsonIgnore
    public Map<Integer, T> getSchemasAsMap() {
        return schemaMap;
    }

    @JsonIgnore
    public String getSubject() {
        return subject;
    }

    public T getSchema(int version) {
        return schemaMap.get(version);
    }

    @JsonIgnore
    public T getEarliestSchema() {
        return schemaMap.get(getEarliestSchemaVersion());
    }

    @JsonIgnore
    public Integer getEarliestSchemaVersion() {
        return schemaMap.firstKey();
    }

    @JsonIgnore
    public T getLatestSchema() {
        return schemaMap.get(getLatestSchemaVersion());
    }

    @JsonIgnore
    public Integer getLatestSchemaVersion() {
        return schemaMap.lastKey();
    }

    @Override
    public int hashCode() {
        return Objects.hash(schemaMap);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        SubjectSchemas<?> that = (SubjectSchemas<?>)obj;
        return
                Objects.equals(this.schemaMap, that.schemaMap);
    }

    @Override
    public String toString() {
        return schemaMap.toString();
    }

    private static <T extends SchemaInfo> String determineSubject(Collection<T> schemas) {
        String subject = schemas.iterator().next().getSubject();

        if (schemas.size() == 1) {
            return subject;
        }

        boolean allHaveSameSubject = schemas.stream().
                allMatch(s -> s.getSubject().equals(subject));
        if (!allHaveSameSubject) {
            throw new IllegalArgumentException("Schemas have different subjects");
        }

        return subject;
    }

    public Builder<T> toBuilder() {
        return builder(this);
    }

    public static <T extends SchemaInfo> Builder<T> builder() {
        return builder(null);
    }

    public static <T extends SchemaInfo> Builder<T> builder(SubjectSchemas<T> origin) {
        return new Builder<>(origin);
    }

    public static <T extends SchemaInfo> SubjectSchemas<T> with(Collection<T> schemas) {
        return new SubjectSchemas<>(schemas);
    }

    public <R extends SchemaInfo> SubjectSchemas<R> transform(Function<T, R> function) {
        return SubjectSchemas.<R>builder()
                .schemas(getSchemas()
                        .stream().map(function)
                        .collect(Collectors.toList()))
                .build();
    }

    public static class Builder<T extends SchemaInfo> {

        private Set<T> schemas = new HashSet<>();

        protected Builder() {
            this(null);
        }

        protected Builder(SubjectSchemas<T> origin) {
            if (origin == null) {
                return;
            }

            this.schemas.addAll(origin.getSchemas());
        }

        public Builder<T> schemas(Collection<T> schemas) {
            this.schemas.clear();
            if (schemas != null) {
                this.schemas.addAll(schemas);
            }
            return this;
        }

        public Builder<T> appendSchema(T schema) {
            this.schemas.add(schema);
            return this;
        }

        public SubjectSchemas<T> build() {
            return new SubjectSchemas<>(schemas);
        }

    }

}
