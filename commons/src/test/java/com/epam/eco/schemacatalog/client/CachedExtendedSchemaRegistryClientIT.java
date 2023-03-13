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
package com.epam.eco.schemacatalog.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.avro.Schema;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import com.epam.eco.commons.avro.modification.RemoveSchemaFieldByPath;
import com.epam.eco.commons.avro.modification.RenameSchema;
import com.epam.eco.commons.avro.modification.SetSchemaProperties;
import com.epam.eco.commons.avro.modification.SortSchemaFields;
import com.epam.eco.schemacatalog.domain.schema.BasicSchemaInfo;

import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CachedExtendedSchemaRegistryClientIT {

    private static final String TEST_SCHEMA_JSON1 = "{\"type\": \"record\", \"name\": \"Name\", \"fields\": []}";
    private static final String TEST_SCHEMA_JSON2 = "{\"type\": \"record\", \"name\": \"Name\", \"fields\": [{\"name\": \"a\", \"type\": \"string\", \"default\": \"\"}]}";

    static class Clients implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                    Arguments.of(buildEcoClient()),
                    Arguments.of(new MockExtendedSchemaRegistryClient())
            );
        }
    }

    private static ExtendedSchemaRegistryClient buildEcoClient() throws Exception {
        Properties properties = new Properties();
        properties.load(CachedExtendedSchemaRegistryClientIT.class.getResourceAsStream(
                "/extended-schemaregistry-client-it.properties"));
        String schemaRegistryUrl = properties.getProperty("schema.registry.url");
        return new CachedExtendedSchemaRegistryClient(schemaRegistryUrl, 1_000);
    }

    @ParameterizedTest
    @ArgumentsSource(Clients.class)
    public void testSchemaIsResolvedBySubjectAndVersion(
            ExtendedSchemaRegistryClient client
    ) throws Exception {
        String subject = "getBySubjectAndVersionTest-subj";

        ParsedSchema schemaExpected = registerSchema(client, subject, TEST_SCHEMA_JSON1);
        ParsedSchema schemaResult = client.getSchemaBySubjectAndVersion(subject, 1);

        assertNotNull(schemaResult);
        assertEquals(schemaExpected, schemaResult);
    }

    @ParameterizedTest
    @ArgumentsSource(Clients.class)
    public void getBySubjectAndNullVersionNegative(ExtendedSchemaRegistryClient client) {
        assertThrows(
                RuntimeException.class,
                () -> client.getSchemaBySubjectAndVersion("getBySubjectAndNullVersionNegative-subj", 0)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(Clients.class)
    public void getByBlankSubjectAndVersionNegative(ExtendedSchemaRegistryClient client) {
        assertThrows(
                IllegalArgumentException.class,
                () -> client.getSchemaBySubjectAndVersion(" ", 1)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(Clients.class)
    public void testSchemaInfoIsResolvedBySubjectAndVersion(ExtendedSchemaRegistryClient client) throws Exception {
        String subject = "getSchemaInfoTest-subj";

        ParsedSchema schema = registerSchema(client, subject, TEST_SCHEMA_JSON1);
        BasicSchemaInfo info = client.getSchemaInfo(subject, 1);

        assertNotNull(info);
        assertNotNull(info.getSubject());
        assertNotNull(info.getSchemaJson());
        assertNotNull(info.getSchemaAvro());

        assertTrue(info.getSchemaRegistryId() > 0);

        assertEquals(1, info.getVersion());
        assertEquals(schema, info.getParsedSchema());
    }

    @ParameterizedTest
    @ArgumentsSource(Clients.class)
    public void getSchemaInfoByBlankSubjectNegative(ExtendedSchemaRegistryClient client) {
        assertThrows(
                IllegalArgumentException.class,
                () -> client.getSchemaInfo(" ", 1)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(Clients.class)
    public void getSchemaInfoByNullVersionNegative(ExtendedSchemaRegistryClient client) {
        assertThrows(
                RuntimeException.class,
                () -> client.getSchemaInfo("getSchemaInfoByNullVersionNegative-subj", 0)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(Clients.class)
    public void testLatestSchemaInfoIsResolvedBySubject(ExtendedSchemaRegistryClient client) throws Exception {
        String subject = "getSchemaInfoLatestTest-subj";

        registerSchema(client, subject, TEST_SCHEMA_JSON1);
        ParsedSchema schemaExpected = registerSchema(client, subject, TEST_SCHEMA_JSON2);
        BasicSchemaInfo info = client.getLatestSchemaInfo(subject);

        assertNotNull(info);
        assertNotNull(info.getSubject());
        assertNotNull(info.getSchemaJson());
        assertNotNull(info.getSchemaAvro());

        assertTrue(info.getSchemaRegistryId() > 0);

        assertEquals(2, info.getVersion());
        assertEquals(schemaExpected, info.getParsedSchema());
    }

    @ParameterizedTest
    @ArgumentsSource(Clients.class)
    public void getSchemaInfoLatestByBlankSubjectNegative(ExtendedSchemaRegistryClient client) {
        assertThrows(
                IllegalArgumentException.class,
                () -> client.getLatestSchemaInfo(" ")
        );
    }

    @ParameterizedTest
    @ArgumentsSource(Clients.class)
    public void testSchemaIsModifiedAndRegistered(ExtendedSchemaRegistryClient client) {
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

//        client.register("nomatter", new AvroSchema(schema));

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

    @ParameterizedTest
    @ArgumentsSource(Clients.class)
    public void testSchemasWithDifferentFieldOrderAreSortedModifiedAndRegisteredAsSingleSchema(ExtendedSchemaRegistryClient client) {
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

    @ParameterizedTest
    @ArgumentsSource(Clients.class)
    public void modifyAndRegisterNotExistentSchema(ExtendedSchemaRegistryClient client) {
        assertThrows(
                RuntimeException.class,
                () -> client.modifyAndRegisterSchema(
                        "unexistentSourceSubject",
                        1,
                        "destinationSubject",
                        new RemoveSchemaFieldByPath("a")
                )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(Clients.class)
    public void modifyAndRegisterWithNullSourceSchema(ExtendedSchemaRegistryClient client) {
        assertThrows(
                NullPointerException.class,
                () -> client.modifyAndRegisterSchema(
                        "sourceSubject",
                        null,
                        "destinationSubject",
                        new RemoveSchemaFieldByPath("field_to_exclude1")
                )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(Clients.class)
    public void modifyAndRegisterWithNullTargetSubject(ExtendedSchemaRegistryClient client) {
        assertThrows(
                IllegalArgumentException.class,
                () -> client.modifyAndRegisterSchema(
                        "sourceSubject",
                        new Schema.Parser().parse(TEST_SCHEMA_JSON2),
                        " ",
                        new RemoveSchemaFieldByPath("a")
                )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(Clients.class)
    public void modifyAndRegisterWithBlankSourceSubject(ExtendedSchemaRegistryClient client) {
        assertThrows(
                IllegalArgumentException.class,
                () -> client.modifyAndRegisterSchema(
                        " ",
                        1,
                        "destinationSubject",
                        new RemoveSchemaFieldByPath("a")
                )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(Clients.class)
    public void modifyAndRegisterWithNullSourceVersion(ExtendedSchemaRegistryClient client) {
        assertThrows(
                RuntimeException.class,
                () -> client.modifyAndRegisterSchema(
                        "sourceSubject",
                        0,
                        "destinationSubject",
                        new RemoveSchemaFieldByPath("a")
                )
        );
    }

    private ParsedSchema registerSchema(
            ExtendedSchemaRegistryClient client,
            String subject,
            String schemaJson
    ) throws Exception {
        ParsedSchema schema = new AvroSchema(schemaJson);
        client.register(subject, schema);
        return schema;
    }

}
