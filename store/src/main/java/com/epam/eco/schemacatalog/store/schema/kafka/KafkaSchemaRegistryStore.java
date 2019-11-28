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
package com.epam.eco.schemacatalog.store.schema.kafka;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.retry.support.RetryTemplate;

import com.epam.eco.commons.concurrent.ResourceSemaphores;
import com.epam.eco.commons.concurrent.ResourceSemaphores.ResourceSemaphore;
import com.epam.eco.commons.kafka.cache.CacheListener;
import com.epam.eco.commons.kafka.cache.KafkaCache;
import com.epam.eco.schemacatalog.client.ExtendedSchemaRegistryClient;
import com.epam.eco.schemacatalog.domain.exception.NotFoundException;
import com.epam.eco.schemacatalog.domain.schema.Mode;
import com.epam.eco.schemacatalog.domain.schema.SubjectAndVersion;
import com.epam.eco.schemacatalog.store.common.kafka.KafkaStoreProperties;
import com.epam.eco.schemacatalog.store.schema.SchemaEntity;
import com.epam.eco.schemacatalog.store.schema.SchemaRegistryStore;
import com.epam.eco.schemacatalog.store.schema.SchemaRegistryStoreUpdateListener;

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

/**
 * @author Andrei_Tytsik
 */
public class KafkaSchemaRegistryStore implements SchemaRegistryStore, CacheListener<Key, Value>, SmartLifecycle {

    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaSchemaRegistryStore.class);

    private static final String TOPIC = "_schemas";

    private static final NavigableMap<?, ?> EMPTY_MAP =
            Collections.unmodifiableNavigableMap(new TreeMap<>());

    @Autowired
    private KafkaStoreProperties properties;

    @Autowired
    private ExtendedSchemaRegistryClient schemaRegistryClient;

    @Autowired
    private RetryTemplate retryTemplate;

    private final List<SchemaRegistryStoreUpdateListener> listeners = new CopyOnWriteArrayList<>();

    private KafkaCache<Key, Value> schemaRegistryCache;

    private final Map<String, ConfigValue> configCache = new HashMap<>();
    private final Map<String, ModeValue> modeCache = new HashMap<>();
    private final Map<String, NavigableMap<Integer, SchemaValue>> schemaCache = new HashMap<>();
    private final Map<String, DeleteSubjectValue> deleteSubjectCache = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    private final ResourceSemaphores<String, SubjectOperation> subjectSemaphores =
            new ResourceSemaphores<>();

    private final ResourceSemaphores<SubjectAndVersion, SchemaOperation> schemaSemaphores =
            new ResourceSemaphores<>();

    private final AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void start() {
        if (started.get()) {
            return;
        }

        try {
            readGlobalConfig();
            readGlobalMode();
            initAndStartSchemaRegistryCache();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        LOGGER.info("Started");
    }

    @Override
    public void stop() {
        if (!started.get()) {
            return;
        }

        destroySchemaRegistryCache();

        LOGGER.info("Stopped");
    }

    @Override
    public boolean isRunning() {
        return started.get();
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    private void readGlobalConfig() throws Exception {
        // retry at start-up only
        AvroCompatibilityLevel globalCompatibilityLevel = retryTemplate.
                execute(context -> schemaRegistryClient.getGlobalCompatibilityLevel());

        if (globalCompatibilityLevel == null) {
            throw new RuntimeException("Global compatibility level is null");
        }

        configCache.put(
                null,
                new ConfigValue(globalCompatibilityLevel));
    }

    private void readGlobalMode() throws Exception {
        // retry at start-up only
        Mode globalMode = retryTemplate.
                execute(context -> schemaRegistryClient.getModeValue());

        if (globalMode == null) {
            throw new RuntimeException("Global mode is null");
        }

        modeCache.put(
                null,
                new ModeValue(globalMode));
    }

    private void initAndStartSchemaRegistryCache() throws Exception {
        schemaRegistryCache = KafkaCache.<Key, Value>builder().
                bootstrapServers(properties.getBootstrapServers()).
                topicName(TOPIC).
                bootstrapTimeoutInMs(properties.getBootstrapTimeoutInMs()).
                consumerConfig(properties.getClientConfig()).
                keyValueDecoder(new SchemaRegistryDecoder()).
                readOnly(true).
                storeData(false).
                listener(this).
                build();
        schemaRegistryCache.start();
    }

    private void destroySchemaRegistryCache() {
        schemaRegistryCache.close();
    }

    @Override
    public List<String> getAllSubjects() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(schemaCache.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<SchemaEntity> getAllSchemas() {
        lock.readLock().lock();
        try {
            return schemaCache.values().stream().
                    flatMap(schemas -> schemas.values().stream()).
                    map(this::toSchemaEntity).
                    collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<SchemaEntity> getSchemas(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        lock.readLock().lock();
        try {
            return getSubjectSchemasOrElseFail(subject).values().stream().
                    map(this::toSchemaEntity).
                    collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<SchemaEntity> getSchemas(Collection<String> subjects) {
        Validate.notNull(subjects, "Collection of subjects is null");

        lock.readLock().lock();
        try {
            return subjects.stream().
                    filter(schemaCache::containsKey).
                    flatMap(subject -> getSubjectSchemasOrElseFail(subject).values().stream()).
                    map(this::toSchemaEntity).
                    collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public SchemaEntity getLatestSchema(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        return getSchema(subject, null);
    }

    @Override
    public SchemaEntity getSchema(String subject, Integer version) {
        Validate.notBlank(subject, "Subject is blank");

        lock.readLock().lock();
        try {
            NavigableMap<Integer, SchemaValue> subjectSchemas = getSubjectSchemasOrElseFail(subject);
            version = version != null ? version : subjectSchemas.lastKey();
            SchemaValue schemaValue = subjectSchemas.get(version);
            if (schemaValue == null) {
                throw new NotFoundException(
                        String.format("Schema not found by subject=%s and version=%s", subject, version));
            }

            return toSchemaEntity(schemaValue);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public AvroCompatibilityLevel getSubjectCompatibility(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        lock.readLock().lock();
        try {
            return getEffectiveConfig(subject).getCompatibilityLevel();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean schemaExists(SubjectAndVersion subjectAndVersion) {
        Validate.notNull(subjectAndVersion, "SubjectAndVersion is null");

        return schemaExists(subjectAndVersion.getSubject(), subjectAndVersion.getVersion());
    }

    @Override
    public boolean schemaExists(String subject, int version) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.isTrue(version >= 0, "Version is invalid");

        lock.readLock().lock();
        try {
            NavigableMap<Integer, SchemaValue> subjectSchemas = getSubjectSchemasOrElseEmpty(subject);
            return subjectSchemas.containsKey(version);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean subjectExists(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        lock.readLock().lock();
        try {
            return schemaCache.containsKey(subject);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void updateSubjectCompatibility(String subject, AvroCompatibilityLevel compatibilityLevel) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(compatibilityLevel, "Compatibility Level is null");

        try (ResourceSemaphore<String, SubjectOperation> semaphore =
                subjectSemaphores.createSemaphore(subject, SubjectOperation.UPDATE)) {
            schemaRegistryClient.updateCompatibility(subject, compatibilityLevel);
            semaphore.awaitUnchecked();
        }
    }

    @Override
    public boolean testSchemaCompatible(String subject, Schema schema) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        return schemaRegistryClient.testCompatibilityUnchecked(subject, schema);
    }

    @Override
    public SchemaEntity registerSchema(String subject, Schema schema) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        int version;
        ResourceSemaphore<SubjectAndVersion, SchemaOperation> semaphore = null;

        lock.readLock().lock();
        try {
            schemaRegistryClient.registerUnchecked(subject, schema);
            version = schemaRegistryClient.getVersionUnchecked(subject, schema);
            if (!schemaExists(subject, version)) {
                semaphore = schemaSemaphores.createSemaphore(
                        new SubjectAndVersion(subject, version),
                        SchemaOperation.REGISTER);
            }
        } finally {
            lock.readLock().unlock();
        }

        if (semaphore != null) {
            try {
                semaphore.awaitUnchecked();
            } finally {
                semaphore.close();
            }
        }

        return getSchema(subject, version);
    }

    @Override
    public void deleteSubject(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        try (ResourceSemaphore<String, SubjectOperation> semaphore =
                subjectSemaphores.createSemaphore(subject, SubjectOperation.DELETE)) {
            schemaRegistryClient.deleteSubjectUnchecked(subject);
            semaphore.awaitUnchecked();
        }
    }

    @Override
    public void deleteSchema(SubjectAndVersion subjectAndVersion) {
        Validate.notNull(subjectAndVersion, "SubjectAndVersion is null");

        deleteSchema(subjectAndVersion.getSubject(), subjectAndVersion.getVersion());
    }

    @Override
    public void deleteSchema(String subject, int version) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.isTrue(version >= 0, "Version is invalid");

        SubjectAndVersion subjectAndVersion = new SubjectAndVersion(subject, version);
        try (ResourceSemaphore<SubjectAndVersion, SchemaOperation> semaphore =
                schemaSemaphores.createSemaphore(subjectAndVersion, SchemaOperation.DELETE)) {
            schemaRegistryClient.deleteSchema(subject, version);
            semaphore.awaitUnchecked();
        }
    }

    @Override
    public void registerListener(SchemaRegistryStoreUpdateListener listener) {
        Validate.notNull(listener, "Listener is null");

        listeners.add(listener);
    }

    @Override
    public void onCacheUpdated(Map<Key, Value> update) {
        if (update.isEmpty()) {
            return;
        }

        Set<SubjectAndVersion> affected = new HashSet<>();

        lock.writeLock().lock();
        try {
            update.forEach((key, value) -> {
                try {
                    if (key.getKeytype() == KeyType.CONFIG) {
                        affected.addAll(
                                applyConfigUpdateAndGetAffected(
                                        (ConfigKey) key, (ConfigValue) value));
                    } else if (key.getKeytype() == KeyType.SCHEMA) {
                        affected.addAll(
                                applySchemaUpdateAndGetAffected(
                                        (SchemaKey) key, (SchemaValue) value));
                    } else if (key.getKeytype() == KeyType.DELETE_SUBJECT) {
                        affected.addAll(
                                applySubjectDeleteAndGetAffected(
                                        (DeleteSubjectKey) key,
                                        (DeleteSubjectValue) value));
                    } else if (key.getKeytype() == KeyType.MODE) {
                        affected.addAll(
                                applyModeUpdateAndGetAffected(
                                        (ModeKey) key, (ModeValue) value));
                    } else {
                        LOGGER.warn(
                                "Ignoring unsupported 'schema registry update' record. Key = {}, value = {}",
                                key, value);
                    }
                } catch (Exception ex) {
                    LOGGER.error(
                            String.format(
                                    "Failed to handle 'schema registry update' record. Key = %s, value = %s",
                                    key, value),
                            ex);
                }
            });
        } finally {
            lock.writeLock().unlock();
        }

        fireListenersFor(affected);
    }

    private void fireListenersFor(Set<SubjectAndVersion> subjectAndVersions) {
        if (CollectionUtils.isEmpty(listeners) || CollectionUtils.isEmpty(subjectAndVersions)) {
            return;
        }

        List<SchemaEntity> updated = new ArrayList<>();
        List<SubjectAndVersion> deleted = new ArrayList<>();
        subjectAndVersions.forEach(sav -> {
            SchemaValue schemaValue = getSubjectSchemasOrElseFail(sav.getSubject()).get(sav.getVersion());
            if (schemaValue != null) {
                updated.add(toSchemaEntity(schemaValue));
            } else {
                deleted.add(sav);
            }
        });

        listeners.forEach(listener -> {
            try {
                listener.onSchemasDeleted(deleted);
            } catch (Exception ex) {
                LOGGER.error("Failed to handle 'schemas deleted' event", ex);
            }

            try {
                listener.onSchemasUpdated(updated);
            } catch (Exception ex) {
                LOGGER.error("Failed to handle 'schemas updated' event", ex);
            }
        });
    }

    private Set<SubjectAndVersion> applyConfigUpdateAndGetAffected(ConfigKey key, ConfigValue value) {
        ConfigValue oldValue;
        if (value != null) {
            oldValue = configCache.put(key.getSubject(), value);
        } else {
            oldValue = configCache.remove(key.getSubject());
        }

        if (Objects.equals(oldValue, value)) {
            return Collections.emptySet();
        }

        Set<SubjectAndVersion> affected = new HashSet<>();
        if (key.getSubject() == null) { // global
            for (String subject : schemaCache.keySet()) {
                if (!configCache.containsKey(subject)) {
                    NavigableMap<Integer, SchemaValue> subjectSchemas = getSubjectSchemasOrElseEmpty(subject);
                    affected.addAll(
                            subjectSchemas.keySet().stream().
                                map(version -> new SubjectAndVersion(subject, version)).
                                collect(Collectors.toSet()));
                }
            }
        } else {
            NavigableMap<Integer, SchemaValue> subjectSchemas =
                    getSubjectSchemasOrElseEmpty(key.getSubject());
            affected.addAll(
                    subjectSchemas.keySet().stream().
                        map(version -> new SubjectAndVersion(key.getSubject(), version)).
                        collect(Collectors.toSet()));
        }

        // subject UPDATE semaphore
        if (value != null && !Objects.equals(oldValue, value)) {
            subjectSemaphores.signalDoneFor(key.getSubject(), SubjectOperation.UPDATE);
        }

        return affected;
    }

    private Set<SubjectAndVersion> applyModeUpdateAndGetAffected(ModeKey key, ModeValue value) {
        ModeValue oldValue;
        if (value != null) {
            oldValue = modeCache.put(key.getSubject(), value);
        } else {
            oldValue = modeCache.remove(key.getSubject());
        }

        if (Objects.equals(oldValue, value)) {
            return Collections.emptySet();
        }

        Set<SubjectAndVersion> affected = new HashSet<>();
        if (key.getSubject() == null) { // global
            for (String subject : schemaCache.keySet()) {
                if (!modeCache.containsKey(subject)) {
                    NavigableMap<Integer, SchemaValue> subjectSchemas = getSubjectSchemasOrElseEmpty(subject);
                    affected.addAll(
                            subjectSchemas.keySet().stream().
                                map(version -> new SubjectAndVersion(subject, version)).
                                collect(Collectors.toSet()));
                }
            }
        } else {
            NavigableMap<Integer, SchemaValue> subjectSchemas =
                    getSubjectSchemasOrElseEmpty(key.getSubject());
            affected.addAll(
                    subjectSchemas.keySet().stream().
                        map(version -> new SubjectAndVersion(key.getSubject(), version)).
                        collect(Collectors.toSet()));
        }

        // subject UPDATE semaphore
        /* not needed unless we expose possibility to change mode
        if (value != null && !Objects.equals(oldValue, value)) {
            subjectSemaphores.signalDoneFor(key.getSubject(), SubjectOperation.UPDATE);
        }
        */

        return affected;
    }

    private Set<SubjectAndVersion> applySchemaUpdateAndGetAffected(SchemaKey key, SchemaValue value) {
        NavigableMap<Integer, SchemaValue> subjectSchemas = getSubjectSchemasOrElseCreate(key.getSubject());

        SchemaValue oldValue = null;
        if (value != null) {
            oldValue = subjectSchemas.put(key.getVersion(), value);
        } else {
            oldValue = subjectSchemas.remove(key.getVersion());
        }

        if (Objects.equals(oldValue, value)) {
            return Collections.emptySet();
        }

        populateSchemasDeleteIfNeededAndGetAffeted(key.getSubject());

        SubjectAndVersion subjectAndVersion =
                new SubjectAndVersion(key.getSubject(), key.getVersion());

        Set<SubjectAndVersion> affected = new HashSet<>();
        affected.add(subjectAndVersion);
        if (subjectSchemas.size() > 1 && subjectSchemas.lastKey().equals(key.getVersion())) {
            affected.add(
                    new SubjectAndVersion(
                            key.getSubject(),
                            subjectSchemas.lowerKey(key.getVersion())));
        }

        if (oldValue == null) { // schema REGISTER semaphore
            schemaSemaphores.signalDoneFor(subjectAndVersion, SchemaOperation.REGISTER);
        } else if (value != null && value.isDeleted() && !oldValue.isDeleted()) { // schema DELETE semaphore
            schemaSemaphores.signalDoneFor(subjectAndVersion, SchemaOperation.DELETE);
        }

        return affected;
    }

    private Set<SubjectAndVersion> applySubjectDeleteAndGetAffected(
            DeleteSubjectKey key,
            DeleteSubjectValue value) {
        DeleteSubjectValue oldValue = deleteSubjectCache.put(key.getSubject(), value);
        if (Objects.equals(oldValue, value)) {
            return Collections.emptySet();
        }

        Set<SubjectAndVersion> affected = populateSchemasDeleteIfNeededAndGetAffeted(key.getSubject());

        // subject DELETE semaphore
        if (!affected.isEmpty()) {
            subjectSemaphores.signalDoneFor(key.getSubject(), SubjectOperation.DELETE);
        }

        return affected;
    }

    private Set<SubjectAndVersion> populateSchemasDeleteIfNeededAndGetAffeted(String subject) {
        DeleteSubjectValue deleteSubject = deleteSubjectCache.get(subject);
        if (deleteSubject == null) {
            return Collections.emptySet();
        }

        Map<Integer, SchemaValue> subjectSchemas = getSubjectSchemasOrElseEmpty(subject);
        if (subjectSchemas.isEmpty()) {
            return Collections.emptySet();
        }

        Set<SubjectAndVersion> affected = new HashSet<>();
        for (SchemaValue schema : subjectSchemas.values()) {
            if (!schema.isDeleted() && schema.getVersion() <= deleteSubject.getVersion()) {
                schema.setDeleted(true);
                affected.add(new SubjectAndVersion(subject, schema.getVersion()));
            }
        }
        return affected;
    }

    private ConfigValue getEffectiveConfig(String subject) {
        ConfigValue config = configCache.get(subject);
        if (config == null && subject != null) {
            config = configCache.get(null); // global
        }

        if (config == null) {
            throw new RuntimeException(
                    String.format("Can't determine effective config for subject '%s'", subject));
        }

        return config;
    }

    private ModeValue getEffectiveMode(String subject) {
        ModeValue mode = modeCache.get(subject);
        if (mode == null && subject != null) {
            mode = modeCache.get(null); // global
        }

        if (mode == null) {
            throw new RuntimeException(
                    String.format("Can't determine effective mode for subject '%s'", subject));
        }

        return mode;
    }

    @SuppressWarnings("unchecked")
    private NavigableMap<Integer, SchemaValue> getSubjectSchemasOrElseEmpty(String subject) {
        return schemaCache.getOrDefault(subject, (NavigableMap<Integer, SchemaValue>)EMPTY_MAP);
    }

    private NavigableMap<Integer, SchemaValue> getSubjectSchemasOrElseCreate(String subject) {
        return schemaCache.computeIfAbsent(subject, k -> new TreeMap<>());
    }

    private NavigableMap<Integer, SchemaValue> getSubjectSchemasOrElseFail(String subject) {
        NavigableMap<Integer, SchemaValue> schemas = schemaCache.get(subject);
        if (schemas == null) {
            throw new NotFoundException(String.format("Subject '%s' does not exist", subject));
        }

        return schemas;
    }

    private SchemaEntity toSchemaEntity(SchemaValue schemaValue) {
        ConfigValue configValue = getEffectiveConfig(schemaValue.getSubject());
        ModeValue modeValue = getEffectiveMode(schemaValue.getSubject());
        NavigableMap<Integer, SchemaValue> subjectSchemas = getSubjectSchemasOrElseFail(
                schemaValue.getSubject());
        SchemaEntity schemaEntity = new SchemaEntity();
        schemaEntity.setId(schemaValue.getId());
        schemaEntity.setSubject(schemaValue.getSubject());
        schemaEntity.setVersion(schemaValue.getVersion());
        schemaEntity.setCompatibilityLevel(configValue.getCompatibilityLevel());
        schemaEntity.setMode(modeValue.getMode());
        schemaEntity.setSchema(schemaValue.getSchema());
        schemaEntity.setVersionLatest(subjectSchemas.lastKey().equals(schemaValue.getVersion()));
        schemaEntity.setDeleted(schemaValue.isDeleted());
        return schemaEntity;
    }

    private enum SubjectOperation {
        UPDATE, DELETE
    }

    private enum SchemaOperation {
        REGISTER, DELETE
    }

}
