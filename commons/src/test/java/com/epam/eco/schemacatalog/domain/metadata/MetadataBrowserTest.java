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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.Mode;
import com.epam.eco.schemacatalog.testdata.SchemaTestData;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrei_Tytsik
 */
public class MetadataBrowserTest {

    @Test
    public void testBrowserFunctional() {
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

        Assertions.assertNotNull(browser);

        Assertions.assertFalse(browser.isEmpty());
        Assertions.assertEquals(3, browser.size());

        Assertions.assertTrue(browser.getSchemaMetadata().isPresent());
        Assertions.assertEquals(schemaMetadata, browser.getSchemaMetadata().get());

        Assertions.assertTrue(browser.getFieldMetadata("TestA", "f1").isPresent());
        Assertions.assertEquals(field1Metadata, browser.getFieldMetadata("TestA", "f1").get());

        Assertions.assertFalse(browser.getFieldMetadata("TestC", "f3").isPresent());

        Assertions.assertTrue(browser.getFieldMetadata("TestB", "f2").isPresent());
        Assertions.assertEquals(field3Metadata, browser.getFieldMetadata("TestB", "f2").get());

        Assertions.assertFalse(browser.getFieldMetadata("TestD", "f4").isPresent());

        List<Metadata> fieldMetadataList = browser.getFieldMetadataAsList();
        Assertions.assertNotNull(fieldMetadataList);
        Assertions.assertEquals(2, fieldMetadataList.size());
        Assertions.assertTrue(fieldMetadataList.contains(field1Metadata));
        Assertions.assertTrue(fieldMetadataList.contains(field3Metadata));

        Map<MetadataKey, Metadata> fieldMetadataMap = browser.getFieldMetadataAsMap();
        Assertions.assertNotNull(fieldMetadataMap);
        Assertions.assertEquals(2, fieldMetadataMap.size());
        Assertions.assertEquals(fieldMetadataMap.get(field1Key), field1Metadata);
        Assertions.assertEquals(fieldMetadataMap.get(field3Key), field3Metadata);
    }

    @Test
    public void testEmptyBrowserIsFunctional() {
        FullSchemaInfo schemaInfo = testSchema(null);
        MetadataBrowser<FullSchemaInfo> browser = schemaInfo.getMetadataBrowser();

        Assertions.assertNotNull(browser);

        Assertions.assertTrue(browser.isEmpty());
        Assertions.assertEquals(0, browser.size());

        Assertions.assertFalse(browser.getSchemaMetadata().isPresent());
        Assertions.assertFalse(browser.getFieldMetadata("TestA", "f1").isPresent());
        Assertions.assertFalse(browser.getFieldMetadata("TestB", "f2").isPresent());
        Assertions.assertFalse(browser.getFieldMetadata("TestC", "f3").isPresent());
        Assertions.assertFalse(browser.getFieldMetadata("TestD", "f4").isPresent());

        List<Metadata> fieldMetadataList = browser.getFieldMetadataAsList();
        Assertions.assertNotNull(fieldMetadataList);
        Assertions.assertTrue(fieldMetadataList.isEmpty());

        Map<MetadataKey, Metadata> fieldMetadataMap = browser.getFieldMetadataAsMap();
        Assertions.assertNotNull(fieldMetadataMap);
        Assertions.assertTrue(fieldMetadataMap.isEmpty());
    }

    @Test
    public void testFailsOnIllegalArguments() {
        assertThrows(
                Exception.class,
                () -> new MetadataBrowser<>(null)
        );
    }

    @Test
    public void testFailsOnUnknownField() {
        assertThrows(
                Exception.class,
                () -> {
                    FullSchemaInfo schemaInfo = testSchema(null);
                    MetadataBrowser<FullSchemaInfo> browser = schemaInfo.getMetadataBrowser();

                    Assertions.assertNotNull(browser);

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
