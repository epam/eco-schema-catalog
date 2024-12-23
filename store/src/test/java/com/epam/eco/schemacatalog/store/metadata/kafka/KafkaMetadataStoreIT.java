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
package com.epam.eco.schemacatalog.store.metadata.kafka;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;
import com.epam.eco.schemacatalog.store.metadata.MetadataStore;
import com.epam.eco.schemacatalog.store.utils.TestMetadata;
import com.epam.eco.schemacatalog.store.utils.TestMetadataStoreUpdateListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrei_Tytsik
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Config.class)
@Disabled("Manual, requires schema-registry running, see docker-compose in resources dir")
class KafkaMetadataStoreIT {

    private static final int EVENTUAL_CONSISTENCY_SECONDS = 1;

    @Autowired
    private MetadataStore metadataStore;

    @BeforeEach
    public void setUp() {
        resetEnv();
    }

    @AfterEach
    public void tearDown() {
        resetEnv();
    }

    @Test
    void createsRemovesUpdatesSchemaMetadata() throws InterruptedException {
        TestMetadataStoreUpdateListener updateListener = new TestMetadataStoreUpdateListener();
        metadataStore.registerListener(updateListener);

        List<String> updatedSubjects = updateListener.getUpdated();

        List<Map.Entry<MetadataKey, MetadataValue>> samples = TestMetadata.samples();
        MetadataKey key = samples.get(0).getKey();
        MetadataValue value = samples.get(0).getValue();

        updatedSubjects.clear();
        metadataStore.createOrReplace(key, value);
        TimeUnit.SECONDS.sleep(EVENTUAL_CONSISTENCY_SECONDS);

        assertFalse(updatedSubjects.isEmpty());

        String updatedSubject = updatedSubjects.get(updatedSubjects.size() - 1);
        assertEquals(updatedSubject, key.getSubject());

        MetadataValue returnedValue = metadataStore.get(key);
        assertEquals(returnedValue, value);

        Map<MetadataKey, MetadataValue> metadataCollection =
                metadataStore.getCollection(key.getSubject(), key.getVersion() + 1);
        assertEquals(metadataCollection.get(key), value);

        updatedSubjects.clear();
        metadataStore.delete(key);
        TimeUnit.SECONDS.sleep(EVENTUAL_CONSISTENCY_SECONDS);

        assertFalse(updatedSubjects.isEmpty());

        updatedSubject = updatedSubjects.get(updatedSubjects.size() - 1);
        assertEquals(updatedSubject, key.getSubject());

        TimeUnit.SECONDS.sleep(EVENTUAL_CONSISTENCY_SECONDS);

        assertNull(metadataStore.get(key));

        metadataCollection = metadataStore.getCollection(key.getSubject(), key.getVersion() + 1);
        assertNull(metadataCollection);

        Map<MetadataKey, MetadataValue> batch = TestMetadata.samples().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        updatedSubjects.clear();
        metadataStore.executeBatchUpdate(batch);
        TimeUnit.SECONDS.sleep(EVENTUAL_CONSISTENCY_SECONDS);

        assertFalse(updatedSubjects.isEmpty());

        updatedSubject = updatedSubjects.get(updatedSubjects.size() - 1);
        assertEquals(updatedSubject, key.getSubject());

        for (Map.Entry<MetadataKey, MetadataValue> entry : TestMetadata.samples()) {
            returnedValue = metadataStore.get(entry.getKey());
            assertEquals(returnedValue, entry.getValue());
        }

        Iterator<Integer> versionIterator = TestMetadata.versions().iterator();
        Integer firstVersion = versionIterator.next();
        Integer nextVersion = versionIterator.next();

        Integer blankVersionBetween = TestMetadata.greatestBlankVersionBetween(firstVersion, nextVersion);
        assertNotNull(blankVersionBetween);
        updatedSubjects.clear();
        metadataStore.deleteAll(TestMetadata.subject(), blankVersionBetween);
        TimeUnit.SECONDS.sleep(EVENTUAL_CONSISTENCY_SECONDS);

        assertTrue(updatedSubjects.isEmpty());

        metadataStore.deleteAll(TestMetadata.subject(), nextVersion);
        TimeUnit.SECONDS.sleep(EVENTUAL_CONSISTENCY_SECONDS);

        assertFalse(updatedSubjects.isEmpty());

        assertEquals(
                metadataStore.getCollection(TestMetadata.subject(), firstVersion),
                metadataStore.getCollection(TestMetadata.subject(), nextVersion));

        for (Map.Entry<MetadataKey, MetadataValue> entry : TestMetadata.samples()) {
            batch.put(entry.getKey(), null);
        }

        updatedSubjects.clear();
        metadataStore.executeBatchUpdate(batch);
        TimeUnit.SECONDS.sleep(EVENTUAL_CONSISTENCY_SECONDS);

        assertFalse(updatedSubjects.isEmpty());

        updatedSubject = updatedSubjects.get(updatedSubjects.size() - 1);
        assertEquals(updatedSubject, key.getSubject());

        TimeUnit.SECONDS.sleep(EVENTUAL_CONSISTENCY_SECONDS);

        for (Map.Entry<MetadataKey, MetadataValue> entry : TestMetadata.samples()) {
            returnedValue = metadataStore.get(entry.getKey());
            assertNull(returnedValue);
        }
    }

    private void resetEnv() {
        HashMap<MetadataKey, MetadataValue> batch = new HashMap<>();
        for (Map.Entry<MetadataKey, MetadataValue> entry : TestMetadata.samples()) {
            batch.put(entry.getKey(), null);
        }
        metadataStore.executeBatchUpdate(batch);
    }

}
