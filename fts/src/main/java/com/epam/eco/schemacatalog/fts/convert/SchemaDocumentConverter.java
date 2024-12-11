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
package com.epam.eco.schemacatalog.fts.convert;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;

import com.epam.eco.commons.avro.traversal.SchemaTraverseListener;
import com.epam.eco.commons.avro.traversal.SchemaTraverser;
import com.epam.eco.schemacatalog.domain.metadata.Metadata;
import com.epam.eco.schemacatalog.domain.metadata.MetadataAware;
import com.epam.eco.schemacatalog.domain.metadata.MetadataBrowser;
import com.epam.eco.schemacatalog.domain.metadata.format.DocFormatter;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.SubjectAndVersion;
import com.epam.eco.schemacatalog.fts.MetadataDocument;
import com.epam.eco.schemacatalog.fts.SchemaDocument;
import com.epam.eco.schemacatalog.utils.EcoIdUtils;
import com.epam.eco.schemacatalog.utils.MetadataDocAttributeExtractor;

/**
 * @author Andrei_Tytsik
 */
public abstract class SchemaDocumentConverter {

    private SchemaDocumentConverter() {
    }

    public static SchemaDocument convert(FullSchemaInfo schemaInfo) {
        SchemaDocument document = new SchemaDocument();
        document.setSubject(schemaInfo.getSubject());
        document.setVersion(schemaInfo.getVersion());
        document.setSchemaRegistryId(schemaInfo.getSchemaRegistryId());
        document.setEcoId(schemaInfo.getEcoId());
        document.setVersionLatest(schemaInfo.isVersionLatest());
        document.setCompatibility(schemaInfo.getCompatibilityLevel().name());
        document.setGlobalCompatibility(schemaInfo.isGlobalCompatibilityLevel());
        document.setMode(schemaInfo.getMode().name());
        document.setDeleted(schemaInfo.isDeleted());

        if (schemaInfo.getSchemaAvro().getType() == Type.RECORD) {
            document.setRootName(schemaInfo.getSchemaAvro().getName());
            document.setRootNamespace(schemaInfo.getSchemaAvro().getNamespace());
            document.setRootFullname(schemaInfo.getSchemaAvro().getFullName());
        }
        document.setMetadata(toMetadataDocument(schemaInfo));

        new SchemaTraverser(new SchemaTraverseListener() {
            @Override
            public void onSchemaField(String path, Schema parentSchema, Field field) {
                document.addPath(path);
                document.addName(field.name());
                document.addDoc(field.doc());

                Map<String, Object> props = field.getObjectProps();
                if (props != null && !props.isEmpty()) {
                    props.forEach(
                            (key, value) -> document.addProperty(key, Objects.toString(value, null)));
                }

                Set<String> aliases = field.aliases();
                if (aliases != null && !aliases.isEmpty()) {
                    aliases.forEach(document::addAlias);
                }
            }

            @Override
            public void onSchema(String path, Schema parentSchema, Schema schema) {
                document.addDoc(schema.getDoc());
                if (
                        Type.RECORD == schema.getType() ||
                        Type.ENUM == schema.getType() ||
                        Type.FIXED == schema.getType()) {
                    document.addName(schema.getName());
                    document.addNamespace(schema.getNamespace());
                    document.addFullname(schema.getFullName());
                }
                if (schema.getLogicalType() != null) {
                    document.addLogicalType(schema.getLogicalType().getName());
                }
                Map<String, Object> props = schema.getObjectProps();
                if (props != null && !props.isEmpty()) {
                    props.forEach(
                            (key, value) -> document.addProperty(key, Objects.toString(value, null)));
                }
            }
        }).walk(schemaInfo.getSchemaAvro());

        return document;
    }

    public static SchemaDocument convert(SubjectAndVersion subjectAndVersion) {
        SchemaDocument document = new SchemaDocument();
        document.setSubject(subjectAndVersion.getSubject());
        document.setVersion(subjectAndVersion.getVersion());
        document.setEcoId(EcoIdUtils.formatId(subjectAndVersion));
        return document;
    }

    private static MetadataDocument toMetadataDocument(MetadataAware<FullSchemaInfo> metadataAware) {
        MetadataBrowser<FullSchemaInfo> browser = metadataAware.getMetadataBrowser();
        MetadataDocument document = new MetadataDocument();
        browser.toList().forEach(
                metadata -> populateMetadataToDocument(metadata, document));
        return document;
    }

    private static void populateMetadataToDocument(Metadata metadata, MetadataDocument document) {
        if (metadata == null) {
            return;
        }

        document.addDoc(DocFormatter.format(metadata.getValue().getDoc()));
        document.addUpdatedBy(metadata.getValue().getUpdatedBy());
        if (metadata.getValue().getDoc() != null) {
            MetadataDocAttributeExtractor.
                    extract(metadata.getValue().getDoc()).
                    forEach(
                            (key, value) -> document.addAttributes(key, toString(value)));
        }
        metadata.getValue().getAttributes().forEach(
                (key, value) -> document.addAttribute(key, Objects.toString(value, null)));
    }

    private static List<String> toString(Collection<?> list) {
        if (list == null) {
            return null;
        }

        return list.stream().
                map(elem -> Objects.toString(elem, null))
                .toList();
    }

}
