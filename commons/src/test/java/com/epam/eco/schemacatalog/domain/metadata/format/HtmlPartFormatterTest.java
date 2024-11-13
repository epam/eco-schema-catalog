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
package com.epam.eco.schemacatalog.domain.metadata.format;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Dzmitry_Krivolap
 */
class HtmlPartFormatterTest {

    private final HtmlPartFormatter formatter = HtmlPartFormatter.INSTANCE;

    @Test
    void testFormatLink() {
        String result = DocFormatter.format(
                "{@link Google|http://google.com}",
                formatter);
        String expected =
                "<a href=\"http://google.com\" target=\"_blank\" rel=\"noopener noreferrer\">" +
                "Google" +
                "</a>";
        assertEquals(expected, result);
    }

    @Test
    void testFormatFieldLink() {
        String result = DocFormatter.format(
                "{@field_link Field Link|subject|1|schemaFullName|field|http://google.com}",
                formatter);
        String expected =
                "<a href=\"http://google.com#field\" target=\"_blank\" rel=\"noopener noreferrer\">" +
                "Field Link" +
                "</a>";
        assertEquals(expected, result);
    }

    @Test
    void testFormatSchemaLink() {
        String result = DocFormatter.format(
                "{@schema_link Schema Link|subject|1|http://google.com}",
                formatter);
        String expected =
                "<a href=\"http://google.com\" target=\"_blank\" rel=\"noopener noreferrer\">" +
                "Schema Link" +
                "</a>";
        assertEquals(expected, result);
    }

    @Test
    void testFormatForeignKey() {
        String result = DocFormatter.format(
                "{@foreign_key Foreign key|subject|1|schemaFullName|field|http://google.com}",
                formatter);
        String expected =
                "<a href=\"http://google.com#field\" target=\"_blank\" rel=\"noopener noreferrer\">" +
                "Foreign key" +
                "</a>";
        assertEquals(expected, result);
    }

    @Test
    void testFormatSqlTable() {
        String result = DocFormatter.format(
                "{@sql_table database|schema|table}",
                formatter);
        String expected =
                "{@sql_table database|schema|table}";
        assertEquals(expected, result);
    }

    @Test
    void testFormatLinkWithUnacceptableTags() {
        String result = DocFormatter.format(
                "{@link Google|<div></div>} <form></form>",
                formatter);
        String expected =
                "<a href=\"&lt;div&gt;&lt;/div&gt;\" target=\"_blank\" rel=\"noopener noreferrer\">" +
                "Google" +
                "</a> &lt;form&gt;&lt;/form&gt;";
        assertEquals(expected, result);
    }

    @Test
    void testFormatFieldLinkWithUnacceptableTags() {
        String result = DocFormatter.format(
                "{@field_link Field Link|subject|1|schemaFullName|<div></div>|link} <form></form>",
                formatter);
        String expected =
                "<a href=\"link#&lt;div&gt;&lt;/div&gt;\" target=\"_blank\" rel=\"noopener noreferrer\">" +
                "Field Link" +
                "</a> &lt;form&gt;&lt;/form&gt;";
        assertEquals(expected, result);
    }

    @Test
    void testFormatSchemaLinkWithUnacceptableTags() {
        String result = DocFormatter.format(
                "{@schema_link Schema Link|subject|1|<div></div>} <form></form>",
                formatter);
        String expected =
                "<a href=\"&lt;div&gt;&lt;/div&gt;\" target=\"_blank\" rel=\"noopener noreferrer\">" +
                "Schema Link" +
                "</a> &lt;form&gt;&lt;/form&gt;";
        assertEquals(expected, result);
    }

    @Test
    void testFormatForeignKeyWithUnacceptableTags() {
        String result = DocFormatter.format(
                "{@foreign_key Foreign key|subject|1|schemaFullName|<div></div>|link} <form></form>",
                formatter);
        String expected =
                "<a href=\"link#&lt;div&gt;&lt;/div&gt;\" target=\"_blank\" rel=\"noopener noreferrer\">" +
                "Foreign key" +
                "</a> &lt;form&gt;&lt;/form&gt;";
        assertEquals(expected, result);
    }

    @Test
    void testFormatSqlTableWithUnacceptableTags() {
        String result = DocFormatter.format(
                "{@sql_table database|<div></div>|table} <form></form>",
                formatter);
        String expected =
                "{@sql_table database|&lt;div&gt;&lt;/div&gt;|table} &lt;form&gt;&lt;/form&gt;";
        assertEquals(expected, result);
    }

    @Test
    void testFormatLinkWithJavaScript() {
        String result = DocFormatter.format(
                "{@link title|javascript:alert(1)}",
                formatter);
        String expected =
                "<a href=\"_no_javascript_alert(1)\" target=\"_blank\" rel=\"noopener noreferrer\">title</a>";
        assertEquals(expected, result);
    }

    @Test
    void testFormatFieldLinkWithJavaScript() {
        String result = DocFormatter.format(
                "{@field_link Field Link|subject|1|schemaFullName|field|javascript:alert(1)}",
                formatter);
        String expected =
                "<a href=\"_no_javascript_alert(1)#field\" target=\"_blank\" rel=\"noopener noreferrer\">" +
                "Field Link" +
                "</a>";
        assertEquals(expected, result);
    }

    @Test
    void testFormatForeignKeyWithJavaScript() {
        String result = DocFormatter.format(
                "{@foreign_key Foreign key|subject|1|schemaFullName|field|javascript:alert(1)}",
                formatter);
        String expected =
                "<a href=\"_no_javascript_alert(1)#field\" target=\"_blank\" rel=\"noopener noreferrer\">" +
                "Foreign key" +
                "</a>";
        assertEquals(expected, result);
    }

    @Test
    void testFormatSchemaLinkWithJavaScript() {
        String result = DocFormatter.format(
                "{@schema_link Schema Link|subject|1|javascript:alert(1)}",
                formatter);
        String expected =
                "<a href=\"_no_javascript_alert(1)\" target=\"_blank\" rel=\"noopener noreferrer\">" +
                "Schema Link" +
                "</a>";
        assertEquals(expected, result);
    }

}
