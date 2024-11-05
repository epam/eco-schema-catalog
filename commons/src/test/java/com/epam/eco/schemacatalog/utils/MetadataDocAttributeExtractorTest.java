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

import org.junit.jupiter.api.Test;

import com.epam.eco.schemacatalog.domain.metadata.format.TagType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrei_Tytsik
 */
class MetadataDocAttributeExtractorTest {

    @Test
    void testAttributesExtracted() {
        Map<String, List<Object>> attributes = MetadataDocAttributeExtractor.extract(
                "{@link title1|link1} blablabla " +
                "{@sql_table database1|schema1|table1} blablabla " +
                "{@link title2|link2} blabla " +
                "{@schema_link title1|||}");

        assertNotNull(attributes);
        assertEquals(6, attributes.size());

        assertNotNull(attributes.get("LINK.title"));
        assertEquals(2, attributes.get("LINK.title").size());
        assertEquals("title1", attributes.get("LINK.title").get(0));
        assertEquals("title2", attributes.get("LINK.title").get(1));

        assertNotNull(attributes.get("LINK.link"));
        assertEquals(2, attributes.get("LINK.link").size());
        assertEquals("link1", attributes.get("LINK.link").get(0));
        assertEquals("link2", attributes.get("LINK.link").get(1));

        assertNotNull(attributes.get("SQL_TABLE.database"));
        assertEquals(1, attributes.get("SQL_TABLE.database").size());
        assertEquals("database1", attributes.get("SQL_TABLE.database").get(0));

        assertNotNull(attributes.get("SQL_TABLE.schema"));
        assertEquals(1, attributes.get("SQL_TABLE.schema").size());
        assertEquals("schema1", attributes.get("SQL_TABLE.schema").get(0));

        assertNotNull(attributes.get("SQL_TABLE.table"));
        assertEquals(1, attributes.get("SQL_TABLE.table").size());
        assertEquals("table1", attributes.get("SQL_TABLE.table").get(0));

        assertNotNull(attributes.get("SCHEMA_LINK.title"));
        assertEquals(1, attributes.get("SCHEMA_LINK.title").size());
        assertEquals("title1", attributes.get("SCHEMA_LINK.title").get(0));
    }

    @Test
    void testAttributeKeyFormatted() {
        assertEquals(
                "SQL_TABLE.database",
                MetadataDocAttributeExtractor.formatAttributeKey(TagType.SQL_TABLE, "database"));
        assertEquals(
                "FOREIGN_KEY.field",
                MetadataDocAttributeExtractor.formatAttributeKey(TagType.FOREIGN_KEY, "field"));
        assertEquals(
                "LINK.title",
                MetadataDocAttributeExtractor.formatAttributeKey(TagType.LINK, "title"));
    }

    @Test
    void testAttributeKeyFormattingFailedOnInvalidArgument1() {
        assertThrows(
                Exception.class,
                () -> MetadataDocAttributeExtractor.formatAttributeKey(null, "database")
        );
    }

    @Test
    void testAttributeKeyFormattingFailedOnInvalidArgument2() {
        assertThrows(
                Exception.class,
                () -> MetadataDocAttributeExtractor.formatAttributeKey(TagType.SQL_TABLE, null)
        );
    }

    @Test
    void testAttributeKeyFormattingFailedOnInvalidArgument3() {
        assertThrows(
                Exception.class,
                () -> MetadataDocAttributeExtractor.formatAttributeKey(TagType.SQL_TABLE, "")
        );
    }

    @Test
    void testAttributeKeyFormattingFailedOnInvalidArgument4() {
        assertThrows(
                Exception.class,
                () -> MetadataDocAttributeExtractor.formatAttributeKey(TagType.SQL_TABLE, "database__")
        );
    }

    @Test
    void testAttributeKeyVerified() {
        assertFalse(MetadataDocAttributeExtractor.isAttributeKey(""));
        assertFalse(MetadataDocAttributeExtractor.isAttributeKey("abc"));
        assertFalse(MetadataDocAttributeExtractor.isAttributeKey("abc.def"));
        assertFalse(MetadataDocAttributeExtractor.isAttributeKey("."));

        assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE.database"));
        assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE.schema"));
        assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE.table"));

        assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE.Database"));
        assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE.schema1"));
        assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE._table"));

        assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.title"));
        assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.subject"));
        assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.version"));
        assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.link"));

        assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.title_"));
        assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK."));
        assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK"));
        assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.1link"));
    }

}
