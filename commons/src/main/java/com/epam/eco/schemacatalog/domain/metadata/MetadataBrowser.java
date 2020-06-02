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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.Validate;

import com.epam.eco.commons.avro.FieldInfo;
import com.epam.eco.schemacatalog.domain.schema.SchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.Schemafull;

/**
 * @author Andrei_Tytsik
 */
public final class MetadataBrowser<T extends SchemaInfo & Schemafull & MetadataAware<T>> {

    private final T schema;

    private Metadata schemaMetadata;
    private Map<FieldDescriptor, Metadata> fieldMetadata;
    private MultiValuedMap<String, String> entityFields;

    private boolean schemaMetadataComputed = false;

    public MetadataBrowser(T schema) {
        Validate.notNull(schema, "Schema is null");

        this.schema = schema;
    }

    public boolean isEmpty() {
        return schema.getMetadata().isEmpty();
    }

    public int size() {
        return schema.getMetadata().size();
    }

    public List<Metadata> getSchemaMetadataAsList() {
        return getAsList(MetadataType.SCHEMA);
    }

    public List<Metadata> getFieldMetadataAsList() {
        return getAsList(MetadataType.FIELD);
    }

    public List<Metadata> getAsList(MetadataType type) {
        Validate.notNull(type, "Type is null");

        return schema.getMetadata().entrySet().stream().
                filter(e -> type == e.getKey().getType()).
                filter(e -> e.getKey().getSubject().equals(schema.getSubject())).
                map(e -> Metadata.with(e.getKey(), e.getValue())).
                collect(Collectors.toList());
    }

    public List<Metadata> toList() {
        return schema.getMetadata().entrySet().stream().
                filter(e -> e.getKey().getSubject().equals(schema.getSubject())).
                map(e -> Metadata.with(e.getKey(), e.getValue())).
                collect(Collectors.toList());
    }

    public Map<MetadataKey, Metadata> getFieldMetadataAsMap() {
        return getAsMap(MetadataType.FIELD);
    }

    public Map<MetadataKey, Metadata> getAsMap(MetadataType type) {
        Validate.notNull(type, "Type is null");

        return schema.getMetadata().entrySet().stream().
                filter(e -> type == e.getKey().getType()).
                filter(e -> e.getKey().getSubject().equals(schema.getSubject())).
                collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Metadata.with(e.getKey(), e.getValue())));
    }

    public Optional<Metadata> getSchemaMetadata() {
        if (!schemaMetadataComputed) {
            Optional<Metadata> metadata = schema.getMetadata().entrySet().stream().
                    filter(e -> MetadataType.SCHEMA == e.getKey().getType()).
                    filter(e -> e.getKey().getSubject().equals(schema.getSubject())).
                    map(e -> Metadata.with(e.getKey(), e.getValue())).
                    findAny();
            schemaMetadata = metadata.orElse(null);
            return metadata;
        }
        return Optional.ofNullable(schemaMetadata);
    }

    public Optional<Metadata> getFieldMetadata(String schemaFullName, String field) {
        Validate.notBlank(schemaFullName, "Schema full name is blank");
        Validate.notBlank(field, "Field is blank");

        MultiValuedMap<String, String> entityFields = getEntityFields();
        Collection<String> fields = entityFields.get(schemaFullName);
        if (fields == null || !fields.contains(field)) {
            throw new IllegalArgumentException(
                    String.format("Schema has no field '%s' in '%s'", field, schemaFullName));
        }

        FieldDescriptor key = FieldDescriptor.with(schemaFullName, field);
        return Optional.ofNullable(getFieldMetadata().get(key));
    }

    private Map<FieldDescriptor, Metadata> getFieldMetadata() {
        if (fieldMetadata == null) {
            fieldMetadata = schema.getMetadata().entrySet().stream().
                    filter(e -> MetadataType.FIELD == e.getKey().getType()).
                    filter(e -> e.getKey().getSubject().equals(schema.getSubject())).
                    collect(Collectors.toMap(
                            e -> FieldDescriptor.with((FieldMetadataKey)e.getKey()),
                            e -> Metadata.with(e.getKey(), e.getValue())));
        }
        return fieldMetadata;
    }

    private MultiValuedMap<String, String> getEntityFields() {
        if (entityFields == null) {
            List<FieldInfo> extract = schema.getSchemaFieldInfosAsList();
            entityFields = new ArrayListValuedHashMap<>();
            extract.forEach(fi -> entityFields.put(
                    fi.getParent().getFullName(),
                    fi.getField().name()));
        }
        return entityFields;
    }

    private static class FieldDescriptor {

        private final String schemaFullName;
        private final String field;

        public FieldDescriptor(String schemaFullName, String field) {
            Validate.notBlank(field, "Schema full name can't be blank");

            this.schemaFullName = schemaFullName;
            this.field = field;
        }

        @SuppressWarnings("unused")
        public String getSchemaFullName() {
            return schemaFullName;
        }

        @SuppressWarnings("unused")
        public String getField() {
            return field;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            FieldDescriptor that = (FieldDescriptor) obj;
            return
                    Objects.equals(schemaFullName, that.schemaFullName) &&
                    Objects.equals(field, that.field);
        }

        @Override
        public int hashCode() {
            return Objects.hash(schemaFullName, field);
        }

        public static FieldDescriptor with(String schemaFullName, String field) {
            return new FieldDescriptor(schemaFullName, field);
        }

        public static FieldDescriptor with(FieldMetadataKey key) {
            Validate.notNull(key, "Key can't be null");

            return with(key.getSchemaFullName(), key.getField());
        }
    }

}
