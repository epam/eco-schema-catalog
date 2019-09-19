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
package com.epam.eco.schemacatalog.domain.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.testdata.SchemaTestData;

/**
 * @author Andrei_Tytsik
 */
public class SubjectSchemasTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testIdentitySerializedToJsonAndBack() throws Exception {
        SubjectSchemas<IdentitySchemaInfo> origin = SubjectSchemas.<IdentitySchemaInfo>builder().
                appendSchema(SchemaTestData.randomIdentitySchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomIdentitySchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomIdentitySchemaInfo("subject1")).
                build();

        String json = JsonMapper.toJson(origin);
        Assert.assertNotNull(json);

        SubjectSchemas<IdentitySchemaInfo> deserialized = JsonMapper.jsonToObject(json, SubjectSchemas.class);
        Assert.assertNotNull(deserialized);
        Assert.assertEquals(origin, deserialized);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBasicSerializedToJsonAndBack() throws Exception {
        SubjectSchemas<BasicSchemaInfo> origin = SubjectSchemas.<BasicSchemaInfo>builder().
                appendSchema(SchemaTestData.randomBasicSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomBasicSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomBasicSchemaInfo("subject1")).
                build();

        String json = JsonMapper.toJson(origin);
        Assert.assertNotNull(json);

        SubjectSchemas<BasicSchemaInfo> deserialized = JsonMapper.jsonToObject(json, SubjectSchemas.class);
        Assert.assertNotNull(deserialized);
        Assert.assertEquals(origin, deserialized);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLiteSerializedToJsonAndBack() throws Exception {
        SubjectSchemas<LiteSchemaInfo> origin = SubjectSchemas.<LiteSchemaInfo>builder().
                appendSchema(SchemaTestData.randomLiteSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomLiteSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomLiteSchemaInfo("subject1")).
                build();

        String json = JsonMapper.toJson(origin);
        Assert.assertNotNull(json);

        SubjectSchemas<LiteSchemaInfo> deserialized = JsonMapper.jsonToObject(json, SubjectSchemas.class);
        Assert.assertNotNull(deserialized);
        Assert.assertEquals(origin, deserialized);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFullSerializedToJsonAndBack() throws Exception {
        SubjectSchemas<FullSchemaInfo> origin = SubjectSchemas.<FullSchemaInfo>builder().
                appendSchema(SchemaTestData.randomFullSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomFullSchemaInfo("subject1")).
                appendSchema(SchemaTestData.randomFullSchemaInfo("subject1")).
                build();

        String json = JsonMapper.toJson(origin);
        Assert.assertNotNull(json);

        SubjectSchemas<FullSchemaInfo> deserialized = JsonMapper.jsonToObject(json, SubjectSchemas.class);
        Assert.assertNotNull(deserialized);
        Assert.assertEquals(origin, deserialized);
    }

    @Test
    public void testSchemasAreDiscoverable() throws Exception {
        String subject = "subject999";

        BasicSchemaInfo info1 = SchemaTestData.randomBasicSchemaInfo(subject, 1);
        BasicSchemaInfo info2 = SchemaTestData.randomBasicSchemaInfo(subject, 22);
        BasicSchemaInfo info3 = SchemaTestData.randomBasicSchemaInfo(subject, 333);

        BasicSchemaInfo[] schemas = new BasicSchemaInfo[] {info2, info3, info1};

        SubjectSchemas<BasicSchemaInfo> subjectSchemas = SubjectSchemas.with(Arrays.asList(schemas));

        Assert.assertNotNull(subjectSchemas);
        Assert.assertEquals(3, subjectSchemas.size());
        Assert.assertEquals(subject, subjectSchemas.getSubject());

        Assert.assertEquals(info1, subjectSchemas.getSchema(1));
        Assert.assertEquals(info2, subjectSchemas.getSchema(22));
        Assert.assertEquals(info3, subjectSchemas.getSchema(333));
        Assert.assertEquals(null, subjectSchemas.getSchema(444));

        Assert.assertEquals(Integer.valueOf(1), subjectSchemas.getEarliestSchemaVersion());
        Assert.assertEquals(info1, subjectSchemas.getEarliestSchema());

        Assert.assertEquals(Integer.valueOf(333), subjectSchemas.getLatestSchemaVersion());
        Assert.assertEquals(info3, subjectSchemas.getLatestSchema());

        Map<Integer, BasicSchemaInfo> schemasMap = subjectSchemas.getSchemasAsMap();
        Assert.assertNotNull(schemasMap);
        Assert.assertEquals(3, schemasMap.size());

        List<BasicSchemaInfo> schemasList = new ArrayList<>();
        subjectSchemas.forEach(schemasList::add);
        Assert.assertEquals(3, schemasList.size());
        Assert.assertEquals(info1, schemasList.get(0));
        Assert.assertEquals(info2, schemasList.get(1));
        Assert.assertEquals(info3, schemasList.get(2));
    }

    @Test(expected=Exception.class)
    public void testFailsOnIllegalArguments1() throws Exception {
        SubjectSchemas.with(null);
    }

    @Test(expected=Exception.class)
    public void testFailsOnIllegalArguments2() throws Exception {
        SubjectSchemas.with(Collections.singletonList(null));
    }

    @Test(expected=Exception.class)
    public void testFailsOnDifferentSubjects() throws Exception {
        BasicSchemaInfo[] schemas = new BasicSchemaInfo[] {
                SchemaTestData.randomBasicSchemaInfo("subject1", 1),
                SchemaTestData.randomBasicSchemaInfo("subject2", 1)};

        SubjectSchemas.with(Arrays.asList(schemas));
    }

}
