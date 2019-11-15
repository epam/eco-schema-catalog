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
package com.epam.eco.schemacatalog.domain.metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.Mode;
import com.epam.eco.schemacatalog.testdata.SchemaTestData;

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

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

        Assert.assertNotNull(browser);

        Assert.assertFalse(browser.isEmpty());
        Assert.assertEquals(3, browser.size());

        Assert.assertTrue(browser.getSchemaMetadata().isPresent());
        Assert.assertEquals(schemaMetadata, browser.getSchemaMetadata().get());

        Assert.assertTrue(browser.getFieldMetadata("TestA", "f1").isPresent());
        Assert.assertEquals(field1Metadata, browser.getFieldMetadata("TestA", "f1").get());

        Assert.assertFalse(browser.getFieldMetadata("TestC", "f3").isPresent());

        Assert.assertTrue(browser.getFieldMetadata("TestB", "f2").isPresent());
        Assert.assertEquals(field3Metadata, browser.getFieldMetadata("TestB", "f2").get());

        Assert.assertFalse(browser.getFieldMetadata("TestD", "f4").isPresent());

        List<Metadata> fieldMetadataList = browser.getFieldMetadataAsList();
        Assert.assertNotNull(fieldMetadataList);
        Assert.assertEquals(2, fieldMetadataList.size());
        Assert.assertTrue(fieldMetadataList.contains(field1Metadata));
        Assert.assertTrue(fieldMetadataList.contains(field3Metadata));

        Map<MetadataKey, Metadata> fieldMetadataMap = browser.getFieldMetadataAsMap();
        Assert.assertNotNull(fieldMetadataMap);
        Assert.assertEquals(2, fieldMetadataMap.size());
        Assert.assertEquals(fieldMetadataMap.get(field1Key), field1Metadata);
        Assert.assertEquals(fieldMetadataMap.get(field3Key), field3Metadata);
    }

    @Test
    public void testEmptyBrowserIsFunctional() {
        FullSchemaInfo schemaInfo = testSchema(null);
        MetadataBrowser<FullSchemaInfo> browser = schemaInfo.getMetadataBrowser();

        Assert.assertNotNull(browser);

        Assert.assertTrue(browser.isEmpty());
        Assert.assertEquals(0, browser.size());

        Assert.assertFalse(browser.getSchemaMetadata().isPresent());
        Assert.assertFalse(browser.getFieldMetadata("TestA", "f1").isPresent());
        Assert.assertFalse(browser.getFieldMetadata("TestB", "f2").isPresent());
        Assert.assertFalse(browser.getFieldMetadata("TestC", "f3").isPresent());
        Assert.assertFalse(browser.getFieldMetadata("TestD", "f4").isPresent());

        List<Metadata> fieldMetadataList = browser.getFieldMetadataAsList();
        Assert.assertNotNull(fieldMetadataList);
        Assert.assertTrue(fieldMetadataList.isEmpty());

        Map<MetadataKey, Metadata> fieldMetadataMap = browser.getFieldMetadataAsMap();
        Assert.assertNotNull(fieldMetadataMap);
        Assert.assertTrue(fieldMetadataMap.isEmpty());
    }

    @Test(expected=Exception.class)
    public void testFailsOnIllegalArguments() {
        new MetadataBrowser<>(null);
    }

    @Test(expected=Exception.class)
    public void testFailsOnUnknownField() {
        FullSchemaInfo schemaInfo = testSchema(null);
        MetadataBrowser<FullSchemaInfo> browser = schemaInfo.getMetadataBrowser();

        Assert.assertNotNull(browser);

        browser.getFieldMetadata("schemaFullName", "unknown_field_path");
    }

    private FullSchemaInfo testSchema(
            Map<MetadataKey, MetadataValue> metadata) {
        return FullSchemaInfo.builder().
                subject("subject").
                version(1).
                compatibilityLevel(AvroCompatibilityLevel.FULL).
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
