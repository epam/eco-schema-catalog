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
package com.epam.eco.schemacatalog.store.metadata.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;

import com.epam.eco.commons.kafka.cache.CacheListener;
import com.epam.eco.commons.kafka.cache.KafkaCache;
import com.epam.eco.commons.kafka.config.ConsumerConfigBuilder;
import com.epam.eco.commons.kafka.config.ProducerConfigBuilder;
import com.epam.eco.commons.kafka.serde.JsonDeserializer;
import com.epam.eco.commons.kafka.serde.JsonSerializer;
import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;
import com.epam.eco.schemacatalog.store.common.kafka.KafkaStoreProperties;
import com.epam.eco.schemacatalog.store.metadata.MetadataContainer;
import com.epam.eco.schemacatalog.store.metadata.MetadataContainerFactory;
import com.epam.eco.schemacatalog.store.metadata.MetadataStore;
import com.epam.eco.schemacatalog.store.metadata.MetadataStoreUpdateListener;

/**
 * @author Raman_Babich
 */
public class KafkaMetadataStore implements MetadataStore, CacheListener<MetadataKey, MetadataValue>, SmartLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMetadataStore.class);

    private static final String TOPIC_NAME = "__schemas_metadata";

    @Autowired
    private KafkaStoreProperties properties;

    private final List<MetadataStoreUpdateListener> listeners = new CopyOnWriteArrayList<>();

    @Autowired
    private MetadataContainerFactory containerFactory;

    private KafkaCache<MetadataKey, MetadataValue> metadataCache;

    private final Map<String, MetadataContainer> storeCache = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    private final AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void start() {
        if (started.get()) {
            return;
        }

        try {
            initAndStartMetadataCache();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        started.set(true);

        LOGGER.info("Started");
    }

    @Override
    public void stop() {
        if (!started.get()) {
            return;
        }

        destroyMetadataCache();

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

    @Override
    public MetadataValue get(MetadataKey key) {
        Validate.notNull(key, "Metadata key is null");

        lock.readLock().lock();
        try {
            MetadataContainer container = storeCache.get(key.getSubject());
            if (container != null) {
                return container.get(key);
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Map<MetadataKey, MetadataValue> getCollection(String subject, int version) {
        Validate.notNull(subject, "Subject is null");
        Validate.isTrue(version >= 0, "Version is negative");

        lock.readLock().lock();
        try {
            MetadataContainer container = storeCache.get(subject);
            if (container != null) {
                return container.getCollection(version);
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void createOrReplace(MetadataKey key, MetadataValue value) {
        Validate.notNull(key, "Metadata key is null");
        Validate.notNull(key, "Metadata value is null");

        lock.writeLock().lock();
        try {
            doCreateOrReplace(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(MetadataKey key) {
        Validate.notNull(key, "Metadata key is null");

        lock.writeLock().lock();
        try {
            doDelete(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void executeBatchUpdate(Map<MetadataKey, MetadataValue> batch) {
        Validate.notNull(batch, "Batch is null");

        if (batch.isEmpty()) {
            return;
        }

        lock.writeLock().lock();
        try {
            doExecuteBatchUpdate(batch);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void registerListener(MetadataStoreUpdateListener listener) {
        Validate.notNull(listener, "Listener is null");

        listeners.add(listener);
    }

    @Override
    public void onCacheUpdated(Map<MetadataKey, MetadataValue> update) {
        Validate.notNull(update, "Batch is null");

        if (update.isEmpty()) {
            return;
        }

        lock.writeLock().lock();
        try {
            doExecuteBatchUpdate(update, false);
        } finally {
            lock.writeLock().unlock();
        }

        fireUpdateListeners(update.keySet().stream()
                .map(MetadataKey::getSubject)
                .collect(Collectors.toSet()));
    }

    private void fireUpdateListeners(Set<String> subjects) {
        if (listeners.isEmpty()) {
            return;
        }

        listeners.forEach(listener -> {
            try {
                listener.onMetadataSubjectsUpdated(subjects);
            } catch (Exception ex) {
                LOGGER.error(
                        String.format(
                                "Failed to handle 'metadata update' event. Subjects = %s",
                                subjects),
                        ex);
            }
        });
    }

    private void doExecuteBatchUpdate(Map<MetadataKey, MetadataValue> batch) {
        doExecuteBatchUpdate(batch, true);
    }

    private void doExecuteBatchUpdate(
            Map<MetadataKey, MetadataValue> batch,
            boolean saveToKafka) {
        if (saveToKafka) {
            saveToKafka(batch);
        }

        for (Map.Entry<MetadataKey, MetadataValue> entry : batch.entrySet()) {
            MetadataKey key = entry.getKey();
            MetadataValue value = entry.getValue();
            if (value != null) {
                doCreateOrReplace(key, value, false);
            } else {
                doDelete(key, false);
            }
        }
    }

    private void doCreateOrReplace(MetadataKey key, MetadataValue value) {
        doCreateOrReplace(key, value, true);
    }

    private void doCreateOrReplace(
            MetadataKey key,
            MetadataValue value,
            boolean saveToKafka) {
        if (saveToKafka) {
            saveToKafka(key, value);
        }

        MetadataContainer container = storeCache.get(key.getSubject());
        if (container != null) {
            container.put(key, value);
        } else {
            container = containerFactory.create(key.getSubject());
            container.put(key, value);
            storeCache.put(key.getSubject(), container);
        }
    }

    private void doDelete(MetadataKey key) {
        doDelete(key, true);
    }

    private void doDelete(MetadataKey key, boolean saveToKafka) {
        if (saveToKafka) {
            saveToKafka(key, null);
        }

        MetadataContainer container = storeCache.get(key.getSubject());
        if (container != null) {
            MetadataValue oldValue = container.get(key);
            if (oldValue != null) {
                container.remove(key);
                if (container.isEmpty()) {
                    storeCache.remove(key.getSubject());
                }
            }
        }
    }

    private void saveToKafka(MetadataKey key, MetadataValue value) {
        metadataCache.put(key, value);
    }

    private void saveToKafka(Map<MetadataKey, MetadataValue> batch) {
        if (batch.isEmpty()) {
            return;
        }

        metadataCache.putAll(batch);
    }

    private void initAndStartMetadataCache() throws Exception  {
        metadataCache = KafkaCache.<MetadataKey, MetadataValue>builder()
                .bootstrapServers(properties.getBootstrapServers())
                .topicName(TOPIC_NAME)
                .bootstrapTimeoutInMs(properties.getBootstrapTimeoutInMs())
                .consumerConfigBuilder(
                        ConsumerConfigBuilder.with(properties.getClientConfig()).
                            keyDeserializer(JsonDeserializer.class).
                            property(JsonDeserializer.KEY_TYPE, MetadataKey.class).
                            valueDeserializer(JsonDeserializer.class).
                            property(JsonDeserializer.VALUE_TYPE, MetadataValue.class))
                .consumerParallelism(1)
                .readOnly(false)
                .listener(this)
                .storeData(false)
                .producerConfigBuilder(
                        ProducerConfigBuilder.with(properties.getClientConfig())
                            .keySerializer(JsonSerializer.class)
                            .valueSerializer(JsonSerializer.class))
                .build();
        metadataCache.start();
    }

    private void destroyMetadataCache() {
        metadataCache.close();
    }

}
