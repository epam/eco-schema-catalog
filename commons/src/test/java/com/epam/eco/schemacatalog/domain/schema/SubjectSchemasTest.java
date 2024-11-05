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
package com.epam.eco.schemacatalog.domain.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.testdata.SchemaTestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrei_Tytsik
 */
class SubjectSchemasTest {

    @SuppressWarnings("unchecked")
    @Test
    void testIdentitySerializedToJsonAndBack() {
        SubjectSchemas<IdentitySchemaInfo> origin = SubjectSchemas.<IdentitySchemaInfo>builder().
                appendSchema(SchemaTestData.randomIdentitySchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomIdentitySchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomIdentitySchemaInfo("subject1")).
                build();

        String json = JsonMapper.toJson(origin);
        assertNotNull(json);

        SubjectSchemas<IdentitySchemaInfo> deserialized = JsonMapper.jsonToObject(json, SubjectSchemas.class);
        assertNotNull(deserialized);
        assertEquals(origin, deserialized);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testBasicSerializedToJsonAndBack() {
        SubjectSchemas<BasicSchemaInfo> origin = SubjectSchemas.<BasicSchemaInfo>builder().
                appendSchema(SchemaTestData.randomBasicSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomBasicSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomBasicSchemaInfo("subject1")).
                build();

        String json = JsonMapper.toJson(origin);
        assertNotNull(json);

        SubjectSchemas<BasicSchemaInfo> deserialized = JsonMapper.jsonToObject(json, SubjectSchemas.class);
        assertNotNull(deserialized);
        assertEquals(origin, deserialized);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testLiteSerializedToJsonAndBack() {
        SubjectSchemas<LiteSchemaInfo> origin = SubjectSchemas.<LiteSchemaInfo>builder().
                appendSchema(SchemaTestData.randomLiteSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomLiteSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomLiteSchemaInfo("subject1")).
                build();

        String json = JsonMapper.toJson(origin);
        assertNotNull(json);

        SubjectSchemas<LiteSchemaInfo> deserialized = JsonMapper.jsonToObject(json, SubjectSchemas.class);
        assertNotNull(deserialized);
        assertEquals(origin, deserialized);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFullSerializedToJsonAndBack() {
        SubjectSchemas<FullSchemaInfo> origin = SubjectSchemas.<FullSchemaInfo>builder().
                appendSchema(SchemaTestData.randomFullSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomFullSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomFullSchemaInfo("subject1")).
                build();

        String json = JsonMapper.toJson(origin);
        assertNotNull(json);

        SubjectSchemas<FullSchemaInfo> deserialized = JsonMapper.jsonToObject(json, SubjectSchemas.class);
        assertNotNull(deserialized);
        assertEquals(origin, deserialized);
    }

    @Test
    void testSchemasAreDiscoverable() {
        String subject = "subject999";

        BasicSchemaInfo info1 = SchemaTestData.randomBasicSchemaInfo(subject, 1);
        BasicSchemaInfo info2 = SchemaTestData.randomBasicSchemaInfo(subject, 22);
        BasicSchemaInfo info3 = SchemaTestData.randomBasicSchemaInfo(subject, 333);

        BasicSchemaInfo[] schemas = new BasicSchemaInfo[]{info2, info3, info1};

        SubjectSchemas<BasicSchemaInfo> subjectSchemas = SubjectSchemas.with(Arrays.asList(schemas));

        assertNotNull(subjectSchemas);
        assertEquals(3, subjectSchemas.size());
        assertEquals(subject, subjectSchemas.getSubject());

        assertEquals(info1, subjectSchemas.getSchema(1));
        assertEquals(info2, subjectSchemas.getSchema(22));
        assertEquals(info3, subjectSchemas.getSchema(333));
        assertNull(subjectSchemas.getSchema(444));

        assertEquals(Integer.valueOf(1), subjectSchemas.getEarliestSchemaVersion());
        assertEquals(info1, subjectSchemas.getEarliestSchema());

        assertEquals(Integer.valueOf(333), subjectSchemas.getLatestSchemaVersion());
        assertEquals(info3, subjectSchemas.getLatestSchema());

        Map<Integer, BasicSchemaInfo> schemasMap = subjectSchemas.getSchemasAsMap();
        assertNotNull(schemasMap);
        assertEquals(3, schemasMap.size());

        List<BasicSchemaInfo> schemasList = new ArrayList<>();
        subjectSchemas.forEach(schemasList::add);
        assertEquals(3, schemasList.size());
        assertEquals(info1, schemasList.get(0));
        assertEquals(info2, schemasList.get(1));
        assertEquals(info3, schemasList.get(2));
    }

    @Test
    void testFailsOnIllegalArguments1() {
        assertThrows(
                Exception.class,
                () -> SubjectSchemas.with(null)
        );
    }

    @Test
    void testFailsOnIllegalArguments2() {
        assertThrows(
                Exception.class,
                () -> SubjectSchemas.with(Collections.singletonList(null))
        );
    }

    @Test
    void testFailsOnDifferentSubjects() {
        assertThrows(
                Exception.class,
                () -> {
                    BasicSchemaInfo[] schemas = new BasicSchemaInfo[]{
                            SchemaTestData.randomBasicSchemaInfo("subject1", 1),
                            SchemaTestData.randomBasicSchemaInfo("subject2", 1)};

                    SubjectSchemas.with(Arrays.asList(schemas));
                }
        );
    }
}
