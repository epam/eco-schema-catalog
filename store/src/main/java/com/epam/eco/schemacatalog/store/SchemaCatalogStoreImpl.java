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
package com.epam.eco.schemacatalog.store;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.avro.Schema;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.epam.eco.commons.avro.AvroUtils;
import com.epam.eco.commons.avro.validation.DetailedSchemaValidationException;
import com.epam.eco.schemacatalog.domain.metadata.MetadataBatchUpdateParams;
import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataUpdateParams;
import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.SchemaCompatibilityCheckResult;
import com.epam.eco.schemacatalog.domain.schema.SchemaCompatibilityError;
import com.epam.eco.schemacatalog.domain.schema.SchemaRegisterParams;
import com.epam.eco.schemacatalog.domain.schema.SubjectAndVersion;
import com.epam.eco.schemacatalog.domain.schema.SubjectCompatibilityUpdateParams;
import com.epam.eco.schemacatalog.domain.schema.SubjectSchemas;
import com.epam.eco.schemacatalog.store.metadata.MetadataStore;
import com.epam.eco.schemacatalog.store.metadata.MetadataStoreUpdateListener;
import com.epam.eco.schemacatalog.store.schema.SchemaEntity;
import com.epam.eco.schemacatalog.store.schema.SchemaRegistryStore;
import com.epam.eco.schemacatalog.store.schema.SchemaRegistryStoreUpdateListener;
import com.epam.eco.schemacatalog.store.utils.SecurityUtils;
import com.epam.eco.schemacatalog.utils.DetailedAvroCompatibilityChecker;
import com.epam.eco.schemacatalog.utils.MetadataDocAttributeExtractor;

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

/**
 * @author Andrei_Tytsik
 */
public class SchemaCatalogStoreImpl implements SchemaCatalogStore, SchemaRegistryStoreUpdateListener, MetadataStoreUpdateListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(SchemaCatalogStoreImpl.class);

    @Autowired
    private SchemaRegistryStore schemaRegistryStore;

    @Autowired
    private MetadataStore metadataStore;

    @Autowired(required=false)
    private List<SchemaCatalogStoreUpdateListener> updateListeners;

    @PostConstruct
    private void init() {
        subscribeOnUpdatesFromStores();

        LOGGER.info("Initialized");
    }

    private void subscribeOnUpdatesFromStores() {
        schemaRegistryStore.registerListener(this);
        metadataStore.registerListener(this);
    }

    @Override
    public List<String> getAllSubjects() {
        return schemaRegistryStore.getAllSubjects();
    }

    @Override
    public List<FullSchemaInfo> getAllSchemas() {
        return schemaRegistryStore.getAllSchemas().stream().
                map(this::toFullSchemaInfo).
                collect(Collectors.toList());
    }

    @Override
    public FullSchemaInfo getSchema(String subject, int version) {
        return toFullSchemaInfo(
                schemaRegistryStore.getSchema(subject, version));
    }

    @Override
    public FullSchemaInfo getLatestSchema(String subject) {
        return toFullSchemaInfo(
                schemaRegistryStore.getLatestSchema(subject));
    }

    @Override
    public SubjectSchemas<FullSchemaInfo> getSubjectSchemas(String subject) {
        List<FullSchemaInfo> infos = schemaRegistryStore.getSchemas(subject).stream().
                map(this::toFullSchemaInfo).
                collect(Collectors.toList());
        return SubjectSchemas.with(infos);
    }

    @Override
    public boolean testSchemaCompatible(SchemaRegisterParams params) {
        Validate.notNull(params, "Schema Register params object is null");

        return schemaRegistryStore.testSchemaCompatible(
                params.getSubject(), params.getSchemaAvro());
    }

    @Override
    public SchemaCompatibilityCheckResult testSchemaCompatibleDetailed(SchemaRegisterParams params) {
        Validate.notNull(params, "Schema Register params object is null");

        AvroCompatibilityLevel compatibilityLevel =
                schemaRegistryStore.getSubjectCompatibility(params.getSubject());

        List<Schema> schemas = schemaRegistryStore.getSchemas(params.getSubject()).stream().
                filter(e -> !e.isDeleted()).
                map(e -> AvroUtils.schemaFromJson(e.getSchema())).
                collect(Collectors.toList());

        try {
            DetailedAvroCompatibilityChecker.forLevel(compatibilityLevel).testCompatibility(
                    params.getSchemaAvro(),
                    schemas);
            return new SchemaCompatibilityCheckResult(params.getSubject());
        } catch (DetailedSchemaValidationException sve) {
            return new SchemaCompatibilityCheckResult(
                    params.getSubject(),
                    sve.getErrors().stream().map(SchemaCompatibilityError::from).
                    collect(Collectors.toList()));
        }
    }

    @Override
    public FullSchemaInfo registerSchema(SchemaRegisterParams params) {
        Validate.notNull(params, "Schema Register params object is null");

        return toFullSchemaInfo(
                schemaRegistryStore.registerSchema(params.getSubject(), params.getSchemaAvro()));
    }

    @Override
    public void updateSubject(SubjectCompatibilityUpdateParams params) {
        Validate.notNull(params, "Subject Update Compatibility params object is null");

        schemaRegistryStore.updateSubjectCompatibility(params.getSubject(), params.getCompatibilityLevel());
    }

    @Override
    public void deleteSubject(String subject) {
        schemaRegistryStore.deleteSubject(subject);
    }

    @Override
    public void deleteSchema(String subject, int version) {
        schemaRegistryStore.deleteSchema(subject, version);
    }

    @Override
    public void updateMetadata(MetadataUpdateParams params) {
        Validate.notNull(params, "UpdateMetadataParams object is null");

        metadataStore.createOrReplace(
                params.getKey(),
                toMetadataValue(params));
    }

    @Override
    public void updateMetadata(MetadataBatchUpdateParams params) {
        Validate.notNull(params, "BatchUpdateMetadataParams object is null");

        Map<MetadataKey, MetadataValue> batch = new HashMap<>();
        params.getOperations().forEach((key, value) -> batch.put(key, toMetadataValue(value)));

        metadataStore.executeBatchUpdate(batch);
    }

    @Override
    public void deleteMetadata(MetadataKey key) {
        metadataStore.delete(key);
    }

    @Override
    public void onSchemasUpdated(Collection<SchemaEntity> schemas) {
        fireSchemasUpdated(
                schemas.stream().map(this::toFullSchemaInfo).collect(Collectors.toList()));
    }

    @Override
    public void onSchemasDeleted(Collection<SubjectAndVersion> subjectAndVersions) {
        subjectAndVersions.forEach(sav -> metadataStore.deleteAll(sav.getSubject(), sav.getVersion()));
        fireSchemasDeleted(subjectAndVersions);
    }

    @Override
    public void onMetadataSubjectsUpdated(Collection<String> subjects) {
        fireSchemasUpdated(schemaRegistryStore.getSchemas(subjects).stream().
                map(this::toFullSchemaInfo).
                collect(Collectors.toList()));
    }

    private void fireSchemasUpdated(Collection<FullSchemaInfo> schemas) {
        if (CollectionUtils.isEmpty(updateListeners) || CollectionUtils.isEmpty(schemas)) {
            return;
        }

        updateListeners.forEach(listener -> {
            try {
                listener.onSchemasUpdated(schemas);
            } catch (Exception ex) {
                LOGGER.error("Failed to handle 'schemas updated' event.", ex);
            }
        });
    }

    private void fireSchemasDeleted(Collection<SubjectAndVersion> subjectAndVersions) {
        if (CollectionUtils.isEmpty(updateListeners) || CollectionUtils.isEmpty(subjectAndVersions)) {
            return;
        }

        updateListeners.forEach(listener -> {
            try {
                listener.onSchemasDeleted(subjectAndVersions);
            } catch (Exception ex) {
                LOGGER.error("Failed to handle 'schemas deleted' event.", ex);
            }
        });
    }

    private FullSchemaInfo toFullSchemaInfo(SchemaEntity schemaEntity) {
        return FullSchemaInfo.builder().
                subject(schemaEntity.getSubject()).
                version(schemaEntity.getVersion()).
                schemaRegistryId(schemaEntity.getId()).
                schemaJson(schemaEntity.getSchema()).
                compatibilityLevel(schemaEntity.getCompatibilityLevel()).
                mode(schemaEntity.getMode()).
                deleted(schemaEntity.isDeleted()).
                versionLatest(schemaEntity.isVersionLatest()).
                metadata(
                        metadataStore.getCollection(
                                schemaEntity.getSubject(),
                                schemaEntity.getVersion())).
                build();
    }

    private MetadataValue toMetadataValue(MetadataUpdateParams params) {
        if (params == null) {
            return null;
        }

        MetadataValue.Builder builder = MetadataValue.builder();

        builder.doc(params.getDoc());

        params.getAttributes().entrySet().stream().
            filter(e -> !MetadataDocAttributeExtractor.isAttributeKey(e.getKey())).
            forEach(e -> builder.appendAttribute(e.getKey(), e.getValue()));

        builder.
            updatedAtNow().
            updatedBy(SecurityUtils.getPrincipal());

        return builder.build();
    }

}
