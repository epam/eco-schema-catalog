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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.testdata.SchemaTestData;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrei_Tytsik
 */
public class SubjectSchemasTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testIdentitySerializedToJsonAndBack() {
        SubjectSchemas<IdentitySchemaInfo> origin = SubjectSchemas.<IdentitySchemaInfo>builder().
                appendSchema(SchemaTestData.randomIdentitySchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomIdentitySchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomIdentitySchemaInfo("subject1")).
                build();

        String json = JsonMapper.toJson(origin);
        Assertions.assertNotNull(json);

        SubjectSchemas<IdentitySchemaInfo> deserialized = JsonMapper.jsonToObject(json, SubjectSchemas.class);
        Assertions.assertNotNull(deserialized);
        Assertions.assertEquals(origin, deserialized);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBasicSerializedToJsonAndBack() {
        SubjectSchemas<BasicSchemaInfo> origin = SubjectSchemas.<BasicSchemaInfo>builder().
                appendSchema(SchemaTestData.randomBasicSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomBasicSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomBasicSchemaInfo("subject1")).
                build();

        String json = JsonMapper.toJson(origin);
        Assertions.assertNotNull(json);

        SubjectSchemas<BasicSchemaInfo> deserialized = JsonMapper.jsonToObject(json, SubjectSchemas.class);
        Assertions.assertNotNull(deserialized);
        Assertions.assertEquals(origin, deserialized);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLiteSerializedToJsonAndBack() {
        SubjectSchemas<LiteSchemaInfo> origin = SubjectSchemas.<LiteSchemaInfo>builder().
                appendSchema(SchemaTestData.randomLiteSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomLiteSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomLiteSchemaInfo("subject1")).
                build();

        String json = JsonMapper.toJson(origin);
        Assertions.assertNotNull(json);

        SubjectSchemas<LiteSchemaInfo> deserialized = JsonMapper.jsonToObject(json, SubjectSchemas.class);
        Assertions.assertNotNull(deserialized);
        Assertions.assertEquals(origin, deserialized);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFullSerializedToJsonAndBack() {
        SubjectSchemas<FullSchemaInfo> origin = SubjectSchemas.<FullSchemaInfo>builder().
                appendSchema(SchemaTestData.randomFullSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomFullSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomFullSchemaInfo("subject1")).
                build();

        String json = JsonMapper.toJson(origin);
        Assertions.assertNotNull(json);

        SubjectSchemas<FullSchemaInfo> deserialized = JsonMapper.jsonToObject(json, SubjectSchemas.class);
        Assertions.assertNotNull(deserialized);
        Assertions.assertEquals(origin, deserialized);
    }

    @Test
    public void testSchemasAreDiscoverable() {
        String subject = "subject999";

        BasicSchemaInfo info1 = SchemaTestData.randomBasicSchemaInfo(subject, 1);
        BasicSchemaInfo info2 = SchemaTestData.randomBasicSchemaInfo(subject, 22);
        BasicSchemaInfo info3 = SchemaTestData.randomBasicSchemaInfo(subject, 333);

        BasicSchemaInfo[] schemas = new BasicSchemaInfo[] {info2, info3, info1};

        SubjectSchemas<BasicSchemaInfo> subjectSchemas = SubjectSchemas.with(Arrays.asList(schemas));

        Assertions.assertNotNull(subjectSchemas);
        Assertions.assertEquals(3, subjectSchemas.size());
        Assertions.assertEquals(subject, subjectSchemas.getSubject());

        Assertions.assertEquals(info1, subjectSchemas.getSchema(1));
        Assertions.assertEquals(info2, subjectSchemas.getSchema(22));
        Assertions.assertEquals(info3, subjectSchemas.getSchema(333));
        Assertions.assertNull(subjectSchemas.getSchema(444));

        Assertions.assertEquals(Integer.valueOf(1), subjectSchemas.getEarliestSchemaVersion());
        Assertions.assertEquals(info1, subjectSchemas.getEarliestSchema());

        Assertions.assertEquals(Integer.valueOf(333), subjectSchemas.getLatestSchemaVersion());
        Assertions.assertEquals(info3, subjectSchemas.getLatestSchema());

        Map<Integer, BasicSchemaInfo> schemasMap = subjectSchemas.getSchemasAsMap();
        Assertions.assertNotNull(schemasMap);
        Assertions.assertEquals(3, schemasMap.size());

        List<BasicSchemaInfo> schemasList = new ArrayList<>();
        subjectSchemas.forEach(schemasList::add);
        Assertions.assertEquals(3, schemasList.size());
        Assertions.assertEquals(info1, schemasList.get(0));
        Assertions.assertEquals(info2, schemasList.get(1));
        Assertions.assertEquals(info3, schemasList.get(2));
    }

    @Test
    public void testFailsOnIllegalArguments1() {
        assertThrows(
                Exception.class,
                () -> SubjectSchemas.with(null)
        );
    }

    @Test
    public void testFailsOnIllegalArguments2() {
        assertThrows(
                Exception.class,
                () -> SubjectSchemas.with(Collections.singletonList(null))
        );
    }

    @Test
    public void testFailsOnDifferentSubjects() {
        assertThrows(
                Exception.class,
                () -> {
                    BasicSchemaInfo[] schemas = new BasicSchemaInfo[] {
                            SchemaTestData.randomBasicSchemaInfo("subject1", 1),
                            SchemaTestData.randomBasicSchemaInfo("subject2", 1)};

                    SubjectSchemas.with(Arrays.asList(schemas));
                }
        );
    }
}
