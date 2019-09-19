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
package com.epam.eco.schemacatalog.store.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.epam.eco.schemacatalog.domain.metadata.FieldMetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;
import com.epam.eco.schemacatalog.domain.metadata.SchemaMetadataKey;
import com.epam.eco.schemacatalog.utils.MetadataDocAttributeExtractor;

/**
 * @author Raman_Babich
 */
public final class TestMetadata {

    private static List<Map.Entry<MetadataKey, MetadataValue>> batch = new ArrayList<>();

    static {
        String subject = "__test-subject";
        int version = 0;
        String entityName = "Test";

        MetadataKey key1 = new SchemaMetadataKey(subject, version);
        String doc1 = "doc 1";
        Map<String, List<Object>> attributes1 = MetadataDocAttributeExtractor.extract(doc1);
        MetadataValue value1 = MetadataValue.builder().
                doc(doc1).
                attributes(attributes1).
                updatedAt(new Date()).
                updatedBy("__test").
                build();

        String field2 = "field2";
        MetadataKey key2 = FieldMetadataKey.with(subject, version, entityName, field2);
        String doc2 = "doc 2";
        Map<String, List<Object>> attributes2 = MetadataDocAttributeExtractor.extract(doc2);
        MetadataValue value2 = MetadataValue.builder().
                doc(doc2).
                attributes(attributes2).
                updatedAt(new Date()).
                updatedBy("__test").
                build();

        String field3 = "field3";
        MetadataKey key3 = FieldMetadataKey.with(subject, version, entityName, field3);
        String doc3 = "doc 3";
        Map<String, List<Object>> attributes3 = MetadataDocAttributeExtractor.extract(doc3);
        MetadataValue value3 = MetadataValue.builder().
                doc(doc3).
                attributes(attributes3).
                updatedAt(new Date()).
                updatedBy("__test").
                build();

        String field4 = "field4";
        MetadataKey key4 = FieldMetadataKey.with(subject, version, entityName, field4);
        String doc4 = "doc 4";
        Map<String, List<Object>> attributes4 = MetadataDocAttributeExtractor.extract(doc4);
        MetadataValue value4 = MetadataValue.builder().
                doc(doc4).
                attributes(attributes4).
                updatedAt(new Date()).
                updatedBy("__test").
                build();

        batch.add(new AbstractMap.SimpleEntry<>(key1, value1));
        batch.add(new AbstractMap.SimpleEntry<>(key2, value2));
        batch.add(new AbstractMap.SimpleEntry<>(key3, value3));
        batch.add(new AbstractMap.SimpleEntry<>(key4, value4));
    }

    public static List<Map.Entry<MetadataKey, MetadataValue>> samples() {
        return Collections.unmodifiableList(batch);
    }

    private TestMetadata() {
    }

}
