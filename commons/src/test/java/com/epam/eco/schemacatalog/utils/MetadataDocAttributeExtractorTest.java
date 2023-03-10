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
package com.epam.eco.schemacatalog.utils;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.epam.eco.schemacatalog.domain.metadata.format.TagType;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrei_Tytsik
 */
public class MetadataDocAttributeExtractorTest {

    @Test
    public void testAttributesExtracted() {
        Map<String, List<Object>> attributes = MetadataDocAttributeExtractor.extract(
                "{@link title1|link1} blablabla " +
                "{@sql_table database1|schema1|table1} blablabla " +
                "{@link title2|link2} blabla " +
                "{@schema_link title1|||}");

        Assertions.assertNotNull(attributes);
        Assertions.assertEquals(6, attributes.size());

        Assertions.assertNotNull(attributes.get("LINK.title"));
        Assertions.assertEquals(2, attributes.get("LINK.title").size());
        Assertions.assertEquals("title1", attributes.get("LINK.title").get(0));
        Assertions.assertEquals("title2", attributes.get("LINK.title").get(1));

        Assertions.assertNotNull(attributes.get("LINK.link"));
        Assertions.assertEquals(2, attributes.get("LINK.link").size());
        Assertions.assertEquals("link1", attributes.get("LINK.link").get(0));
        Assertions.assertEquals("link2", attributes.get("LINK.link").get(1));

        Assertions.assertNotNull(attributes.get("SQL_TABLE.database"));
        Assertions.assertEquals(1, attributes.get("SQL_TABLE.database").size());
        Assertions.assertEquals("database1", attributes.get("SQL_TABLE.database").get(0));

        Assertions.assertNotNull(attributes.get("SQL_TABLE.schema"));
        Assertions.assertEquals(1, attributes.get("SQL_TABLE.schema").size());
        Assertions.assertEquals("schema1", attributes.get("SQL_TABLE.schema").get(0));

        Assertions.assertNotNull(attributes.get("SQL_TABLE.table"));
        Assertions.assertEquals(1, attributes.get("SQL_TABLE.table").size());
        Assertions.assertEquals("table1", attributes.get("SQL_TABLE.table").get(0));

        Assertions.assertNotNull(attributes.get("SCHEMA_LINK.title"));
        Assertions.assertEquals(1, attributes.get("SCHEMA_LINK.title").size());
        Assertions.assertEquals("title1", attributes.get("SCHEMA_LINK.title").get(0));
    }

    @Test
    public void testAttributeKeyFormatted() {
        Assertions.assertEquals(
                "SQL_TABLE.database",
                MetadataDocAttributeExtractor.formatAttributeKey(TagType.SQL_TABLE, "database"));
        Assertions.assertEquals(
                "FOREIGN_KEY.field",
                MetadataDocAttributeExtractor.formatAttributeKey(TagType.FOREIGN_KEY, "field"));
        Assertions.assertEquals(
                "LINK.title",
                MetadataDocAttributeExtractor.formatAttributeKey(TagType.LINK, "title"));
    }

    @Test
    public void testAttributeKeyFormattingFailedOnInvalidArgument1() {
        assertThrows(
                Exception.class,
                () -> MetadataDocAttributeExtractor.formatAttributeKey(null, "database")
        );
    }

    @Test
    public void testAttributeKeyFormattingFailedOnInvalidArgument2() {
        assertThrows(
                Exception.class,
                () -> MetadataDocAttributeExtractor.formatAttributeKey(TagType.SQL_TABLE, null)
        );
    }

    @Test
    public void testAttributeKeyFormattingFailedOnInvalidArgument3() {
        assertThrows(
                Exception.class,
                () -> MetadataDocAttributeExtractor.formatAttributeKey(TagType.SQL_TABLE, "")
        );
    }

    @Test
    public void testAttributeKeyFormattingFailedOnInvalidArgument4() {
        assertThrows(
                Exception.class,
                () -> MetadataDocAttributeExtractor.formatAttributeKey(TagType.SQL_TABLE, "database__")
        );
    }

    @Test
    public void testAttributeKeyVerified() {
        Assertions.assertFalse(MetadataDocAttributeExtractor.isAttributeKey(""));
        Assertions.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("abc"));
        Assertions.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("abc.def"));
        Assertions.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("."));

        Assertions.assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE.database"));
        Assertions.assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE.schema"));
        Assertions.assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE.table"));

        Assertions.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE.Database"));
        Assertions.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE.schema1"));
        Assertions.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE._table"));

        Assertions.assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.title"));
        Assertions.assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.subject"));
        Assertions.assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.version"));
        Assertions.assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.link"));

        Assertions.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.title_"));
        Assertions.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK."));
        Assertions.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK"));
        Assertions.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.1link"));
    }

}
