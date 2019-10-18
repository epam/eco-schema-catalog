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
package com.epam.eco.schemacatalog.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.avro.Schema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.epam.eco.commons.avro.modification.RemoveSchemaFieldByPath;
import com.epam.eco.commons.avro.modification.RenameSchema;
import com.epam.eco.commons.avro.modification.SetSchemaProperties;
import com.epam.eco.commons.avro.modification.SortSchemaFields;
import com.epam.eco.schemacatalog.domain.schema.BasicSchemaInfo;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(value = Parameterized.class)
public class CachedExtendedSchemaRegistryClientIT {

    private static final String TEST_SCHEMA_JSON1 = "{\"type\": \"record\", \"name\": \"Name\", \"fields\": []}";
    private static final String TEST_SCHEMA_JSON2 = "{\"type\": \"record\", \"name\": \"Name\", \"fields\": [{\"name\": \"a\", \"type\": \"string\", \"default\": \"\"}]}";

    @Parameterized.Parameters
    public static Collection<ExtendedSchemaRegistryClient> c() throws Exception {
        return asList(
                buildEcoClient(),
                new MockExtendedSchemaRegistryClient()
        );
    }

    private static ExtendedSchemaRegistryClient buildEcoClient() throws Exception {
        Properties properties = new Properties();
        properties.load(CachedExtendedSchemaRegistryClientIT.class.getResourceAsStream(
                "/extended-schemaregistry-client-it.properties"));
        String schemaRegistryUrl = properties.getProperty("schema.registry.url");
        return new CachedExtendedSchemaRegistryClient(schemaRegistryUrl, 1_000);
    }

    private ExtendedSchemaRegistryClient client;

    public CachedExtendedSchemaRegistryClientIT(ExtendedSchemaRegistryClient client) {
        this.client = client;
    }

    @Test
    public void testSchemaIsResolvedBySubjectAndVersion() throws Exception {
        String subject = "getBySubjectAndVersionTest-subj";

        Schema schemaExpected = registerSchema(subject, TEST_SCHEMA_JSON1);
        Schema schemaResult = client.getBySubjectAndVersion(subject, 1);

        assertNotNull(schemaResult);
        assertEquals(schemaExpected, schemaResult);
    }

    @Test(expected = RuntimeException.class)
    public void getBySubjectAndNullVersionNegative() {
        client.getBySubjectAndVersion("getBySubjectAndNullVersionNegative-subj", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getByBlankSubjectAndVersionNegative() {
        client.getBySubjectAndVersion(" ", 1);
    }

    @Test
    public void testSchemaInfoIsResolvedBySubjectAndVersion() throws Exception {
        String subject = "getSchemaInfoTest-subj";

        Schema schema = registerSchema(subject, TEST_SCHEMA_JSON1);
        BasicSchemaInfo info = client.getSchemaInfo(subject, 1);

        assertNotNull(info);
        assertNotNull(info.getSubject());
        assertNotNull(info.getSchemaJson());
        assertNotNull(info.getSchemaAvro());

        assertTrue(info.getSchemaRegistryId() > 0);

        assertEquals(1, info.getVersion());
        assertEquals(schema, info.getSchemaAvro());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSchemaInfoByBlankSubjectNegative() {
        client.getSchemaInfo(" ", 1);
    }

    @Test(expected = RuntimeException.class)
    public void getSchemaInfoByNullVersionNegative() {
        client.getSchemaInfo("getSchemaInfoByNullVersionNegative-subj", 0);
    }

    @Test
    public void testLatestSchemaInfoIsResolvedBySubject() throws Exception {
        String subject = "getSchemaInfoLatestTest-subj";

        registerSchema(subject, TEST_SCHEMA_JSON1);
        Schema schemaExpected = registerSchema(subject, TEST_SCHEMA_JSON2);
        BasicSchemaInfo info = client.getLatestSchemaInfo(subject);

        assertNotNull(info);
        assertNotNull(info.getSubject());
        assertNotNull(info.getSchemaJson());
        assertNotNull(info.getSchemaAvro());

        assertTrue(info.getSchemaRegistryId() > 0);

        assertEquals(2, info.getVersion());
        assertEquals(schemaExpected, info.getSchemaAvro());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSchemaInfoLatestByBlankSubjectNegative() {
        client.getLatestSchemaInfo(" ");
    }

    @Test
    public void testSchemaIsModifiedAndRegistered() {
        String subject = "modifyAndRegisterSchemaTest-subj";

        String schemaStr =
                "{\"type\": \"record\", \"name\": \"orig_name\", \"namespace\": \"orig_namespace\", \"fields\": [" +
                        "{\"name\": \"c\", \"type\": \"string\"}," +
                        "{\"name\": \"a\", \"type\": \"string\"}," +
                        "{\"name\": \"b\", \"type\": \"string\"}," +
                        "{\"name\": \"field_to_exclude1\", \"type\": \"string\"}," +
                        "{\"name\": \"field_to_exclude2\", \"type\": \"string\"}," +
                        "{\"name\": \"field_to_exclude3\", \"type\": \"string\"}" +
                        "]" +
                        "}";

        Schema schema = new Schema.Parser().parse(schemaStr);

        String name = "test_schemaregistry_name";
        String namespace = "test_schemaregistry_namespace";
        String propKey = "test_prop_key";
        String propValue = "test_prop_value";

        Map<String, Object> properties = new HashMap<>();
        properties.put(propKey, propValue);

        BasicSchemaInfo info = client.modifyAndRegisterSchema(
                "nomatter",
                schema,
                subject,
                new RenameSchema(name, namespace),
                new SetSchemaProperties(properties),
                new RemoveSchemaFieldByPath("field_to_exclude1"),
                new RemoveSchemaFieldByPath("field_to_exclude2"),
                new RemoveSchemaFieldByPath("field_to_exclude3"),
                new SortSchemaFields()
        );

        assertNotNull(info);
        assertNotNull(info.getSubject());
        assertNotNull(info.getSchemaJson());
        assertNotNull(info.getSchemaAvro());

        assertTrue(info.getSchemaRegistryId() > 0);

        assertEquals(subject, info.getSubject());
        assertEquals(name, info.getSchemaAvro().getName());
        assertEquals(namespace, info.getSchemaAvro().getNamespace());
        assertEquals(propValue, info.getSchemaAvro().getObjectProp(propKey));
        assertNull(info.getSchemaAvro().getField("field_to_exclude1"));
        assertNull(info.getSchemaAvro().getField("field_to_exclude2"));
        assertNull(info.getSchemaAvro().getField("field_to_exclude3"));
        assertEquals("a", info.getSchemaAvro().getFields().get(0).name());
        assertEquals("b", info.getSchemaAvro().getFields().get(1).name());
        assertEquals("c", info.getSchemaAvro().getFields().get(2).name());
    }

    @Test
    public void testSchemasWithDifferentFieldOrderAreSortedModifiedAndRegisteredAsSingleSchema() {
        String subject = "modifySortAndRegisterSchemaTest-subj";

        String schemaStr1 =
                "{\"type\": \"record\", \"name\": \"testName\", \"namespace\": \"testNamespace\", \"fields\": [" +
                        "{\"name\": \"c\", \"type\": \"string\"}," +
                        "{\"name\": \"a\", \"type\": \"string\"}," +
                        "{\"name\": \"b\", \"type\": \"string\"}" +
                        "]}";
        Schema schema1 = new Schema.Parser().parse(schemaStr1);

        String schemaStr2 =
                "{\"type\": \"record\", \"name\": \"testName\", \"namespace\": \"testNamespace\", \"fields\": [" +
                        "{\"name\": \"b\", \"type\": \"string\"}," +
                        "{\"name\": \"c\", \"type\": \"string\"}," +
                        "{\"name\": \"a\", \"type\": \"string\"}" +
                        "]}";
        Schema schema2 = new Schema.Parser().parse(schemaStr2);

        BasicSchemaInfo info1 = client.modifyAndRegisterSchema(
                "nomatter",
                schema1,
                subject,
                new SortSchemaFields()
        );

        BasicSchemaInfo info2 = client.modifyAndRegisterSchema(
                "nomatter",
                schema2,
                subject,
                new SortSchemaFields()
        );

        assertNotNull(info1);
        assertNotNull(info2);
        assertEquals(info1.getVersion(), info2.getVersion());
        assertEquals(info1.getSchemaJson(), info2.getSchemaJson());
        assertEquals(info1.getSchemaAvro(), info2.getSchemaAvro());
    }

    @Test(expected = RuntimeException.class)
    public void modifyAndRegisterNotExistentSchema() {
        client.modifyAndRegisterSchema(
                "unexistentSourceSubject",
                1,
                "destinationSubject",
                new RemoveSchemaFieldByPath("a")
        );
    }

    @Test(expected = NullPointerException.class)
    public void modifyAndRegisterWithNullSourceSchema() {
        client.modifyAndRegisterSchema(
                "sourceSubject",
                null,
                "destinationSubject",
                new RemoveSchemaFieldByPath("field_to_exclude1")
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void modifyAndRegisterWithNullTargetSubject() {
        client.modifyAndRegisterSchema(
                "sourceSubject",
                new Schema.Parser().parse(TEST_SCHEMA_JSON2),
                " ",
                new RemoveSchemaFieldByPath("a")
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void modifyAndRegisterWithBlankSourceSubject() {
        client.modifyAndRegisterSchema(
                " ",
                1,
                "destinationSubject",
                new RemoveSchemaFieldByPath("a")
        );
    }

    @Test(expected = RuntimeException.class)
    public void modifyAndRegisterWithNullSourceVersion() {
        client.modifyAndRegisterSchema(
                "sourceSubject",
                0,
                "destinationSubject",
                new RemoveSchemaFieldByPath("a")
        );
    }

    private Schema registerSchema(String subject, String schemaJson) throws Exception {
        Schema schema = new Schema.Parser().parse(schemaJson);
        client.register(subject, schema);
        return schema;
    }

}
