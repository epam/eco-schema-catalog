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
package com.epam.eco.schemacatalog.store.metadata;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.epam.eco.schemacatalog.domain.metadata.FieldMetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataType;
import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;
import com.epam.eco.schemacatalog.domain.metadata.SchemaMetadataKey;

/**
 * @author Raman_Babich
 */
public class InheritingMetadataContainerTest {

    @Test
    public void testIsEmpty1() {
        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        Assertions.assertTrue(container.isEmpty());
    }

    @Test
    public void testIsEmpty2() {
        MetadataKey key = new FieldMetadataKey("s", 1, "sfn", "f");
        MetadataValue value = MetadataValue.builder().
                doc("doc").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        container.put(key, value);
        Assertions.assertFalse(container.isEmpty());
    }

    @Test
    public void testGetByKey1() {
        MetadataKey key1 = new FieldMetadataKey("s", 1, "sfn", "f1");
        MetadataKey key4 = new FieldMetadataKey("s", 4, "sfn", "f2");

        MetadataValue value1 = MetadataValue.builder().
                doc("doc1").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value4 = MetadataValue.builder().
                doc("doc4").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        container.put(key1, value1);
        container.put(key4, value4);

        Assertions.assertEquals(container.get(key4), value4);
    }

    @Test
    public void testGetByKey2() {
        MetadataKey key1 = new FieldMetadataKey("s", 1, "sfn", "f1");
        MetadataKey key4 = new FieldMetadataKey("s", 4, "sfn", "f2");

        MetadataValue value1 = MetadataValue.builder().
                doc("doc1").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value4 = MetadataValue.builder().
                doc("doc4").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        container.put(key1, value1);
        container.put(key4, value4);

        MetadataKey key2 = new FieldMetadataKey("s", 2, "sfn", "f1");

        Assertions.assertNull(container.get(key2));
    }

    @Test
    public void testGetByKey3() {
        MetadataKey key1 = new FieldMetadataKey("s", 1, "sfn", "f1");
        MetadataKey key4 = new FieldMetadataKey("s", 4, "sfn", "f1");

        MetadataValue value1 = MetadataValue.builder().
                doc("doc1").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value4 = MetadataValue.builder().
                doc("doc4").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        container.put(key1, value1);
        container.put(key4, value4);

        MetadataKey key2 = new FieldMetadataKey("s", 2, "sfn", "f1");

        Assertions.assertNull(container.get(key2));
    }

    @Test
    public void testGetByVersion1() {
        MetadataKey key1 = new FieldMetadataKey("s", 1, "sfn", "f1");
        MetadataKey key4 = new FieldMetadataKey("s", 4, "sfn", "f2");

        MetadataValue value1 = MetadataValue.builder().
                doc("doc1").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value4 = MetadataValue.builder().
                doc("doc4").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        container.put(key1, value1);
        container.put(key4, value4);

        Map<MetadataKey, MetadataValue> byVersion = container.getCollection(2);

        Assertions.assertEquals(1, byVersion.size());
        Assertions.assertEquals(byVersion.get(key1), value1);
    }

    @Test
    public void testGetByVersion2() {
        MetadataKey key1 = new FieldMetadataKey("s", 1, "sfn", "f1");
        MetadataKey key4 = new FieldMetadataKey("s", 4, "sfn", "f2");

        MetadataValue value1 = MetadataValue.builder().
                doc("doc1").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value4 = MetadataValue.builder().
                doc("doc4").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        container.put(key1, value1);
        container.put(key4, value4);

        Map<MetadataKey, MetadataValue> byVersion = container.getCollection(5);

        Assertions.assertEquals(2, byVersion.size());
        Assertions.assertEquals(byVersion.get(key1), value1);
        Assertions.assertEquals(byVersion.get(key4), value4);
    }

    @Test
    public void testGetByVersion3() {
        MetadataKey key1 = new FieldMetadataKey("s", 1, "sfn", "f1");
        MetadataKey key4 = new FieldMetadataKey("s", 4, "sfn", "f1");

        MetadataValue value1 = MetadataValue.builder().
                doc("doc1").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value4 = MetadataValue.builder().
                doc("doc4").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        container.put(key1, value1);
        container.put(key4, value4);

        Map<MetadataKey, MetadataValue> byVersion = container.getCollection(5);

        Assertions.assertEquals(1, byVersion.size());
        Assertions.assertEquals(byVersion.get(key4), value4);
    }

    @Test
    public void testPut1() {
        MetadataKey key1 = new FieldMetadataKey("s", 1, "sfn", "f1");
        MetadataKey key4 = new FieldMetadataKey("s", 4, "sfn", "f1");

        MetadataValue value1 = MetadataValue.builder().
                doc("doc1").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value4 = MetadataValue.builder().
                doc("doc4").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        MetadataValue old1 = container.put(key1, value1);
        MetadataValue old4 = container.put(key4, value4);

        Assertions.assertNull(old1);
        Assertions.assertEquals(old4, value1);
    }

    @Test
    public void testPut2() {
        MetadataKey key1 = new FieldMetadataKey("s", 1, "sfn", "f1");
        MetadataKey key4 = new FieldMetadataKey("s", 1, "sfn", "f1");

        MetadataValue value1 = MetadataValue.builder().
                doc("doc1").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value4 = MetadataValue.builder().
                doc("doc4").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        MetadataValue old1 = container.put(key1, value1);
        MetadataValue old4 = container.put(key4, value4);

        Assertions.assertNull(old1);
        Assertions.assertEquals(old4, value1);
    }

    @Test
    public void testPut3() {
        MetadataKey key11 = new FieldMetadataKey("s", 1, "sfn", "f1");
        MetadataKey key12 = new FieldMetadataKey("s", 1, "sfn", "f2");
        MetadataKey key51 = new FieldMetadataKey("s", 5, "sfn", "f1");

        MetadataValue value11 = MetadataValue.builder().
                doc("doc11").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value121 = MetadataValue.builder().
                doc("doc121").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value122 = MetadataValue.builder().
                doc("doc122").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value51 = MetadataValue.builder().
                doc("doc51").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        container.put(key11, value11);
        container.put(key51, value51);
        container.put(key12, value121);
        container.put(key12, value122);

        List<MetadataValue> target = container.getCollection(6).entrySet().stream()
                .filter(e -> e.getKey().getType() == MetadataType.FIELD &&
                        ((FieldMetadataKey) e.getKey()).getField().equals("f2"))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        Assertions.assertEquals(target.size(), 1);
        Assertions.assertEquals(target.get(0), value122);
    }

    @Test
    public void testRemove1() {
        MetadataKey key1 = new FieldMetadataKey("s", 1, "sfn", "f1");
        MetadataKey key4 = new FieldMetadataKey("s", 4, "sfn", "f1");

        MetadataValue value1 = MetadataValue.builder().
                doc("doc1").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value4 = MetadataValue.builder().
                doc("doc4").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        container.put(key1, value1);
        container.put(key4, value4);

        MetadataValue old1 = container.remove(key1);

        Assertions.assertEquals(old1, value1);

        Map<MetadataKey, MetadataValue> byVersion5 = container.getCollection(5);

        Assertions.assertEquals(1, byVersion5.size());
        Assertions.assertEquals(byVersion5.get(key4), value4);

        Map<MetadataKey, MetadataValue> byVersion1 = container.getCollection(1);

        Assertions.assertNull(byVersion1);
    }

    @Test
    public void testRemove2() {
        MetadataKey key1 = new FieldMetadataKey("s", 1, "sfn", "f1");
        MetadataKey key4 = new FieldMetadataKey("s", 4, "sfn", "f2");
        MetadataKey key5 = new FieldMetadataKey("s", 5, "sfn", "f1");

        MetadataValue value1 = MetadataValue.builder().
                doc("doc1").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value4 = MetadataValue.builder().
                doc("doc4").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value5 = MetadataValue.builder().
                doc("doc5").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        container.put(key1, value1);
        container.put(key4, value4);
        container.put(key5, value5);

        Map<MetadataKey, MetadataValue> byVersion5 = container.getCollection(5);
        Assertions.assertEquals(2, byVersion5.size());
        Assertions.assertEquals(byVersion5.get(key4), value4);
        Assertions.assertEquals(byVersion5.get(key5), value5);

        Map<MetadataKey, MetadataValue> byVersion1 = container.getCollection(1);
        Assertions.assertEquals(1, byVersion1.size());
        Assertions.assertEquals(byVersion1.get(key1), value1);

        Map<MetadataKey, MetadataValue> byVersion4 = container.getCollection(4);
        Assertions.assertEquals(2, byVersion4.size());
        Assertions.assertEquals(byVersion4.get(key1), value1);
        Assertions.assertEquals(byVersion4.get(key4), value4);

        MetadataValue old1 = container.remove(key1);

        Assertions.assertEquals(old1, value1);

        byVersion5 = container.getCollection(5);
        Assertions.assertEquals(2, byVersion5.size());
        Assertions.assertEquals(byVersion5.get(key4), value4);
        Assertions.assertEquals(byVersion5.get(key5), value5);

        byVersion1 = container.getCollection(1);
        Assertions.assertNull(byVersion1);

        byVersion4 = container.getCollection(4);
        Assertions.assertEquals(1, byVersion4.size());
        Assertions.assertEquals(byVersion4.get(key4), value4);
    }

    @Test
    public void testRemove3() {
        MetadataKey key1 = new FieldMetadataKey("s", 1, "sfn", "f1");
        MetadataKey key4 = new FieldMetadataKey("s", 4, "sfn", "f1");

        MetadataValue value1 = MetadataValue.builder().
                doc("doc1").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        @SuppressWarnings("unused")
        MetadataValue value4 = MetadataValue.builder().
                doc("doc4").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        container.put(key1, value1);

        MetadataValue res = container.remove(key4);

        Assertions.assertNull(res);

        Map<MetadataKey, MetadataValue> byVersion5 = container.getCollection(5);

        Assertions.assertEquals(1, byVersion5.size());
        Assertions.assertEquals(byVersion5.get(key1), value1);
    }

    @Test
    public void testCorrectRemoveOfVersionWithNoOriginKey() {
        MetadataKey aKey = new FieldMetadataKey("s", 1, "sfn", "1");
        MetadataKey bKey = new FieldMetadataKey("s", 2, "sfn", "2");
        MetadataKey cKey = new FieldMetadataKey("s", 3, "sfn", "3");
        MetadataKey dKey = new FieldMetadataKey("s", 5, "sfn", "1");

        MetadataValue aValue = MetadataValue.builder().
                doc("a").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue bValue = MetadataValue.builder().
                doc("b").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue cValue = MetadataValue.builder().
                doc("c").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue dValue = MetadataValue.builder().
                doc("d").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        container.put(aKey, aValue);
        container.put(bKey, bValue);
        container.put(cKey, cValue);
        container.put(dKey, dValue);

        Map<MetadataKey, MetadataValue> byVersion = container.getCollection(6);
        Assertions.assertEquals(3, byVersion.size());
        Assertions.assertEquals(bValue, byVersion.get(bKey));
        Assertions.assertEquals(cValue, byVersion.get(cKey));
        Assertions.assertEquals(dValue, byVersion.get(dKey));

        container.remove(dKey);

        byVersion = container.getCollection(6);
        Assertions.assertEquals(3, byVersion.size());
        Assertions.assertEquals(aValue, byVersion.get(aKey));
        Assertions.assertEquals(bValue, byVersion.get(bKey));
        Assertions.assertEquals(cValue, byVersion.get(cKey));
    }

    @Test
    public void testCorrectRestoreOfVersionOnRemoveOriginKey() {
        MetadataKey aKey = new FieldMetadataKey("s", 1, "sfn", "1");
        MetadataKey bKey = new FieldMetadataKey("s", 2, "sfn", "2");
        MetadataKey cKey = new FieldMetadataKey("s", 3, "sfn", "3");
        MetadataKey dKey = new FieldMetadataKey("s", 5, "sfn", "1");
        MetadataKey eKey = new FieldMetadataKey("s", 5, "sfn", "4");

        MetadataValue aValue = MetadataValue.builder().
                doc("a").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue bValue = MetadataValue.builder().
                doc("b").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue cValue = MetadataValue.builder().
                doc("c").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue dValue = MetadataValue.builder().
                doc("d").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue eValue = MetadataValue.builder().
                doc("e").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        container.put(aKey, aValue);
        container.put(bKey, bValue);
        container.put(cKey, cValue);
        container.put(dKey, dValue);
        container.put(eKey, eValue);

        Map<MetadataKey, MetadataValue> byVersion = container.getCollection(6);
        Assertions.assertEquals(4, byVersion.size());
        Assertions.assertEquals(bValue, byVersion.get(bKey));
        Assertions.assertEquals(cValue, byVersion.get(cKey));
        Assertions.assertEquals(dValue, byVersion.get(dKey));
        Assertions.assertEquals(eValue, byVersion.get(eKey));

        container.remove(dKey);

        byVersion = container.getCollection(6);
        Assertions.assertEquals(4, byVersion.size());
        Assertions.assertEquals(aValue, byVersion.get(aKey));
        Assertions.assertEquals(bValue, byVersion.get(bKey));
        Assertions.assertEquals(cValue, byVersion.get(cKey));
        Assertions.assertEquals(eValue, byVersion.get(eKey));
    }

    @Test
    public void testPutRemove1() {
        MetadataKey key1 = new FieldMetadataKey("s", 1, "sfn", "f");
        MetadataKey key7 = new SchemaMetadataKey("s", 7);
        MetadataKey key4 = new SchemaMetadataKey("s", 4);

        MetadataValue value1 = MetadataValue.builder().
                doc("doc1").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value4 = MetadataValue.builder().
                doc("doc4").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();
        MetadataValue value7 = MetadataValue.builder().
                doc("doc7").
                attributes(null).
                updatedAt(new Date()).
                updatedBy("me").
                build();

        InheritingMetadataContainer container = new InheritingMetadataContainer("s");
        container.put(key1, value1);
        container.put(key4, value4);
        container.put(key7, value7);

        Map<MetadataKey, MetadataValue> byVersion1 = container.getCollection(1);
        Assertions.assertEquals(1, byVersion1.size());
        Assertions.assertEquals(byVersion1.get(key1), value1);

        Map<MetadataKey, MetadataValue> byVersion4 = container.getCollection(4);
        Assertions.assertEquals(2, byVersion4.size());
        Assertions.assertEquals(byVersion4.get(key1), value1);
        Assertions.assertEquals(byVersion4.get(key4), value4);

        Map<MetadataKey, MetadataValue> byVersion7 = container.getCollection(7);
        Assertions.assertEquals(2, byVersion7.size());
        Assertions.assertEquals(byVersion7.get(key1), value1);
        Assertions.assertEquals(byVersion7.get(key7), value7);

        container.remove(key4);

        byVersion1 = container.getCollection(1);
        Assertions.assertEquals(1, byVersion1.size());
        Assertions.assertEquals(byVersion1.get(key1), value1);

        byVersion4 = container.getCollection(4);
        Assertions.assertEquals(1, byVersion4.size());
        Assertions.assertEquals(byVersion4.get(key1), value1);

        byVersion7 = container.getCollection(7);
        Assertions.assertEquals(2, byVersion7.size());
        Assertions.assertEquals(byVersion7.get(key1), value1);
        Assertions.assertEquals(byVersion7.get(key7), value7);
    }

}
