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
package com.epam.eco.schemacatalog.utils;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.epam.eco.schemacatalog.domain.metadata.format.TagType;

/**
 * @author Andrei_Tytsik
 */
public class MetadataDocAttributeExtractorTest {

    @Test
    public void testAttributesExtracted() throws Exception {
        Map<String, List<Object>> attributes = MetadataDocAttributeExtractor.extract(
                "{@link title1|link1} blablabla " +
                "{@sql_table database1|schema1|table1} blablabla " +
                "{@link title2|link2} blabla " +
                "{@schema_link title1|||}");

        Assert.assertNotNull(attributes);
        Assert.assertEquals(6, attributes.size());

        Assert.assertNotNull(attributes.get("LINK.title"));
        Assert.assertEquals(2, attributes.get("LINK.title").size());
        Assert.assertEquals("title1", attributes.get("LINK.title").get(0));
        Assert.assertEquals("title2", attributes.get("LINK.title").get(1));

        Assert.assertNotNull(attributes.get("LINK.link"));
        Assert.assertEquals(2, attributes.get("LINK.link").size());
        Assert.assertEquals("link1", attributes.get("LINK.link").get(0));
        Assert.assertEquals("link2", attributes.get("LINK.link").get(1));

        Assert.assertNotNull(attributes.get("SQL_TABLE.database"));
        Assert.assertEquals(1, attributes.get("SQL_TABLE.database").size());
        Assert.assertEquals("database1", attributes.get("SQL_TABLE.database").get(0));

        Assert.assertNotNull(attributes.get("SQL_TABLE.schema"));
        Assert.assertEquals(1, attributes.get("SQL_TABLE.schema").size());
        Assert.assertEquals("schema1", attributes.get("SQL_TABLE.schema").get(0));

        Assert.assertNotNull(attributes.get("SQL_TABLE.table"));
        Assert.assertEquals(1, attributes.get("SQL_TABLE.table").size());
        Assert.assertEquals("table1", attributes.get("SQL_TABLE.table").get(0));

        Assert.assertNotNull(attributes.get("SCHEMA_LINK.title"));
        Assert.assertEquals(1, attributes.get("SCHEMA_LINK.title").size());
        Assert.assertEquals("title1", attributes.get("SCHEMA_LINK.title").get(0));
    }

    @Test
    public void testAttributeKeyFormatted() throws Exception {
        Assert.assertEquals(
                "SQL_TABLE.database",
                MetadataDocAttributeExtractor.formatAttributeKey(TagType.SQL_TABLE, "database"));
        Assert.assertEquals(
                "FOREIGN_KEY.field",
                MetadataDocAttributeExtractor.formatAttributeKey(TagType.FOREIGN_KEY, "field"));
        Assert.assertEquals(
                "LINK.title",
                MetadataDocAttributeExtractor.formatAttributeKey(TagType.LINK, "title"));
    }

    @Test(expected=Exception.class)
    public void testAttributeKeyFormattingFailedOnInvalidArgument1() throws Exception {
        MetadataDocAttributeExtractor.formatAttributeKey(null, "database");
    }

    @Test(expected=Exception.class)
    public void testAttributeKeyFormattingFailedOnInvalidArgument2() throws Exception {
        MetadataDocAttributeExtractor.formatAttributeKey(TagType.SQL_TABLE, null);
    }

    @Test(expected=Exception.class)
    public void testAttributeKeyFormattingFailedOnInvalidArgument3() throws Exception {
        MetadataDocAttributeExtractor.formatAttributeKey(TagType.SQL_TABLE, "");
    }

    @Test(expected=Exception.class)
    public void testAttributeKeyFormattingFailedOnInvalidArgument4() throws Exception {
        MetadataDocAttributeExtractor.formatAttributeKey(TagType.SQL_TABLE, "database__");
    }

    @Test
    public void testAttributeKeyVerified() throws Exception {
        Assert.assertFalse(MetadataDocAttributeExtractor.isAttributeKey(""));
        Assert.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("abc"));
        Assert.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("abc.def"));
        Assert.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("."));

        Assert.assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE.database"));
        Assert.assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE.schema"));
        Assert.assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE.table"));

        Assert.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE.Database"));
        Assert.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE.schema1"));
        Assert.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SQL_TABLE._table"));

        Assert.assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.title"));
        Assert.assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.subject"));
        Assert.assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.version"));
        Assert.assertTrue(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.link"));

        Assert.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.title_"));
        Assert.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK."));
        Assert.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK"));
        Assert.assertFalse(MetadataDocAttributeExtractor.isAttributeKey("SCHEMA_LINK.1link"));
    }

}
