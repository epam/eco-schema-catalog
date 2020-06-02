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
package com.epam.eco.schemacatalog.fts.datagen;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.avro.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.epam.eco.commons.avro.CachedFieldExtractor;
import com.epam.eco.commons.avro.FieldInfo;
import com.epam.eco.schemacatalog.domain.metadata.FieldMetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;
import com.epam.eco.schemacatalog.domain.metadata.SchemaMetadataKey;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.Mode;
import com.epam.eco.schemacatalog.fts.constant.FtsTestConstants;
import com.epam.eco.schemacatalog.fts.entity.TestSchemaEntity;
import com.epam.eco.schemacatalog.fts.utils.FtsTestUtils;
import com.epam.eco.schemacatalog.utils.MetadataDocAttributeExtractor;

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.randomizers.net.UrlRandomizer;

/**
 * @author Yahor Urban
 */
@Component
public class FtsTestFactory {
    private static EnhancedRandom enhancedRandom;

    private static TestSchemaEntity entity;
    private static Random random = new Random();

    private static Set<Integer> uniqueNumbers;
    private static Iterator<Integer> iterator;

    @Autowired
    public void setEnhancedRandom(EnhancedRandom random) {
        FtsTestFactory.enhancedRandom = random;
    }

    public static SimpleEntry<String, String> getTestSchema() {
        populateTestSchemaTemplate();

        return new SimpleEntry<>(entity.getSubject(), fillSchemaTemplate(null));
    }

    public static Map<String, String> getTestSchemasWithSameNamespaces(int count) {
        populateTestSchemaTemplate();

        Map<String, String> result = new HashMap<>(count);
        Integer namespaceNumber = nextInt();

        for (int i = 0; i < count; i++) {
            result.put(entity.getSubject(), fillSchemaTemplate(namespaceNumber));
        }
        return result;
    }

    public static SimpleEntry<String, String> getRandomProperty() {
        entity = enhancedRandom.nextObject(TestSchemaEntity.class);
        return new SimpleEntry<>(entity.getPropKeys().get(nextInt()), entity.getPropValues().get(nextInt()));
    }

    public static String getRandomName() {
        return entity.getFieldNames().get(nextInt());
    }

    public static FullSchemaInfo getTestSchemaInfo() {
        populateTestSchemaTemplate();

        String schemaJson = fillSchemaTemplate(null);

        Schema schema = new Schema.Parser().parse(schemaJson);

        return FullSchemaInfo.builder()
                .subject(entity.getSubject())
                .compatibilityLevel(AvroCompatibilityLevel.BACKWARD)
                .mode(Mode.READONLY)
                .deleted(false)
                .versionLatest(true)
                .metadata(getTestMetadata(schema, entity.getSubject()))
                .schemaJson(schemaJson)
                .schemaRegistryId(1243534266)
                .version(1)
                .build();
    }

    private static Map<MetadataKey, MetadataValue> getTestMetadata(
            Schema schema, String subject) {
        Map<MetadataKey, MetadataValue> metadata = new HashMap<>();
        List<FieldInfo> fieldInfoList = CachedFieldExtractor.fromSchema(schema);

        List<FieldMetadataKey> fieldMetadataKeys = new ArrayList<>();
        for(FieldInfo fieldInfo : fieldInfoList) {
            fieldMetadataKeys.add(FieldMetadataKey.with(
                    subject,
                    1,
                    fieldInfo.getParent().getFullName(),
                    fieldInfo.getField().name()));
        }

        metadata.put(SchemaMetadataKey.with(subject, 1), getTestMetadataValue());

        fieldMetadataKeys.forEach(fieldMetadataKey -> metadata.put(fieldMetadataKey, getTestMetadataValue()));

        return metadata;
    }

    private static MetadataValue getTestMetadataValue() {
        String docPattern = "%s. {@link %s|%s}";

        String doc = String.format(docPattern,
                FtsTestUtils.generateRandomText(10),
                enhancedRandom.nextObject(String.class),
                new UrlRandomizer().getRandomValue());
        Map<String, List<Object>> attributes = MetadataDocAttributeExtractor.extract(doc);
        Date updatedAt = enhancedRandom.nextObject(Date.class);
        String updatedBy = FtsTestUtils.generateRandomText(2);

        return MetadataValue.builder().
                doc(doc).
                attributes(attributes).
                updatedAt(updatedAt).
                updatedBy(updatedBy).
                build();
    }

    private static void populateTestSchemaTemplate() {
        entity = enhancedRandom.nextObject(TestSchemaEntity.class);
        entity.setNames(toLowerCase(entity.getNames()));
        entity.setNamespaces(toLowerCase(entity.getNamespaces()));
        generateUniqueNumbers(100);
    }

    private static String fillSchemaTemplate(Integer namespaceNumber) {
        int randomValue1 = nextInt();
        int randomValue2 = nextInt();
        int randomValue3 = nextInt();
        int randomValue4 = nextInt();
        return String.format(
                FtsTestConstants.SCHEMA_TEMPLATE,
                entity.getNames().get(randomValue1), namespaceNumber != null ? entity.getNamespaces().get(namespaceNumber) : entity.getNamespaces().get(randomValue1), entity.getDocs().get(nextInt()),
                entity.getFieldNames().get(nextInt()),
                entity.getFieldNames().get(nextInt()), entity.getPropKeys().get(nextInt()), entity.getPropValues().get(nextInt()),
                entity.getFieldNames().get(nextInt()), entity.getLogicalTypes().get(random.nextInt(3)),
                entity.getFieldNames().get(nextInt()),
                entity.getNames().get(randomValue2), entity.getNamespaces().get(randomValue2), entity.getDocs().get(nextInt()),
                entity.getFieldNames().get(nextInt()),
                entity.getFieldNames().get(nextInt()),
                entity.getFieldNames().get(nextInt()), entity.getLogicalTypes().get(random.nextInt(3)), entity.getPropKeys().get(nextInt()), entity.getPropValues().get(nextInt()),
                entity.getFieldNames().get(nextInt()),
                entity.getNames().get(randomValue3), entity.getNamespaces().get(randomValue3), entity.getDocs().get(nextInt()),
                entity.getFieldNames().get(nextInt()), entity.getPropKeys().get(nextInt()), entity.getPropValues().get(nextInt()),
                entity.getFieldNames().get(nextInt()),
                entity.getFieldNames().get(nextInt()), entity.getLogicalTypes().get(random.nextInt(3)),
                entity.getFieldNames().get(nextInt()),
                entity.getNames().get(randomValue4), entity.getNamespaces().get(randomValue4), entity.getDocs().get(nextInt()),
                entity.getFieldNames().get(nextInt()),
                entity.getFieldNames().get(nextInt()),
                entity.getFieldNames().get(nextInt()), entity.getLogicalTypes().get(random.nextInt(3)),
                entity.getFieldNames().get(nextInt()),
                entity.getFieldNames().get(nextInt()),
                entity.getFieldNames().get(nextInt()),
                entity.getFieldNames().get(nextInt()),
                entity.getFieldNames().get(nextInt()),
                entity.getFieldNames().get(nextInt()),
                entity.getFieldNames().get(nextInt()),
                entity.getFieldNames().get(nextInt()),
                entity.getFieldNames().get(nextInt()), entity.getPropKeys().get(nextInt()), entity.getPropValues().get(nextInt())
        );
    }

    private static List<String> toLowerCase(List<String> stringList) {
        ArrayList<String> result = new ArrayList<>(stringList.size());
        stringList.forEach(string -> result.add(string.toLowerCase()));
        return result;
    }

    private static void generateUniqueNumbers(Integer max) {
        uniqueNumbers = IntStream.range(0, max).boxed().collect(Collectors.toCollection(HashSet::new));
        iterator = uniqueNumbers.iterator();
    }

    private static int nextInt() {
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            generateUniqueNumbers(100);
            return nextInt();
        }
    }
}
