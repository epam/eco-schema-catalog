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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.Mode;
import com.epam.eco.schemacatalog.testdata.SchemaTestData;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrei_Tytsik
 */
class MetadataBrowserTest {

    @Test
    void testBrowserFunctional() {
        MetadataKey schemaKey = schemaKey();
        MetadataValue schemaValue = randomValue();
        Metadata schemaMetadata = Metadata.with(schemaKey, schemaValue);

        MetadataKey field1Key = fieldKey("TestA", "f1");
        MetadataValue field1Value = randomValue();
        Metadata field1Metadata = Metadata.with(field1Key, field1Value);

        MetadataKey field3Key = fieldKey("TestB", "f2");
        MetadataValue field3Value = randomValue();
        Metadata field3Metadata = Metadata.with(field3Key, field3Value);

        Map<MetadataKey, MetadataValue> metadata = new HashMap<>();
        metadata.put(schemaKey, schemaValue);
        metadata.put(field1Key, field1Value);
        metadata.put(field3Key, field3Value);

        FullSchemaInfo schemaInfo = testSchema(metadata);
        MetadataBrowser<FullSchemaInfo> browser = schemaInfo.getMetadataBrowser();

        assertNotNull(browser);

        assertFalse(browser.isEmpty());
        assertEquals(3, browser.size());

        assertTrue(browser.getSchemaMetadata().isPresent());
        assertEquals(schemaMetadata, browser.getSchemaMetadata().get());

        assertTrue(browser.getFieldMetadata("TestA", "f1").isPresent());
        assertEquals(field1Metadata, browser.getFieldMetadata("TestA", "f1").get());

        assertFalse(browser.getFieldMetadata("TestC", "f3").isPresent());

        assertTrue(browser.getFieldMetadata("TestB", "f2").isPresent());
        assertEquals(field3Metadata, browser.getFieldMetadata("TestB", "f2").get());

        assertFalse(browser.getFieldMetadata("TestD", "f4").isPresent());

        List<Metadata> fieldMetadataList = browser.getFieldMetadataAsList();
        assertNotNull(fieldMetadataList);
        assertEquals(2, fieldMetadataList.size());
        assertTrue(fieldMetadataList.contains(field1Metadata));
        assertTrue(fieldMetadataList.contains(field3Metadata));

        Map<MetadataKey, Metadata> fieldMetadataMap = browser.getFieldMetadataAsMap();
        assertNotNull(fieldMetadataMap);
        assertEquals(2, fieldMetadataMap.size());
        assertEquals(fieldMetadataMap.get(field1Key), field1Metadata);
        assertEquals(fieldMetadataMap.get(field3Key), field3Metadata);
    }

    @Test
    void testEmptyBrowserIsFunctional() {
        FullSchemaInfo schemaInfo = testSchema(null);
        MetadataBrowser<FullSchemaInfo> browser = schemaInfo.getMetadataBrowser();

        assertNotNull(browser);

        assertTrue(browser.isEmpty());
        assertEquals(0, browser.size());

        assertFalse(browser.getSchemaMetadata().isPresent());
        assertFalse(browser.getFieldMetadata("TestA", "f1").isPresent());
        assertFalse(browser.getFieldMetadata("TestB", "f2").isPresent());
        assertFalse(browser.getFieldMetadata("TestC", "f3").isPresent());
        assertFalse(browser.getFieldMetadata("TestD", "f4").isPresent());

        List<Metadata> fieldMetadataList = browser.getFieldMetadataAsList();
        assertNotNull(fieldMetadataList);
        assertTrue(fieldMetadataList.isEmpty());

        Map<MetadataKey, Metadata> fieldMetadataMap = browser.getFieldMetadataAsMap();
        assertNotNull(fieldMetadataMap);
        assertTrue(fieldMetadataMap.isEmpty());
    }

    @Test
    void testFailsOnIllegalArguments() {
        assertThrows(
                Exception.class,
                () -> new MetadataBrowser<>(null)
        );
    }

    @Test
    void testFailsOnUnknownField() {
        assertThrows(
                Exception.class,
                () -> {
                    FullSchemaInfo schemaInfo = testSchema(null);
                    MetadataBrowser<FullSchemaInfo> browser = schemaInfo.getMetadataBrowser();

                    assertNotNull(browser);

                    browser.getFieldMetadata("schemaFullName", "unknown_field_path");
                }
        );
    }

    private FullSchemaInfo testSchema(
            Map<MetadataKey, MetadataValue> metadata) {
        return FullSchemaInfo.builder().
                subject("subject").
                version(1).
                compatibilityLevel(CompatibilityLevel.FULL).
                mode(Mode.READWRITE).
                deleted(false).
                versionLatest(false).
                schemaRegistryId(1).
                schemaJson(SchemaTestData.SCHEMA1_JSON).
                metadata(metadata).
                build();
    }

    private MetadataKey schemaKey() {
        return SchemaMetadataKey.with(
                "subject",
                new Random().nextInt(1000));
    }

    private MetadataKey fieldKey(String schemaFullName, String field) {
        return FieldMetadataKey.with(
                "subject",
                new Random().nextInt(1000),
                schemaFullName,
                field);
    }

    private MetadataValue randomValue() {
        return MetadataValue.builder().
                doc(UUID.randomUUID().toString()).
                build();
    }

}
