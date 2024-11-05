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

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrei_Tytsik
 */
class DocParserTest {

    @Test
    void testDocIsParsed() {
        List<Part> parts = DocParser.parse("");
        assertNotNull(parts);
        assertEquals(1, parts.size());
        assertEquals(Text.class, parts.get(0).getClass());
        assertEquals("", ((Text) parts.get(0)).getText());

        parts = DocParser.parse("text");
        assertNotNull(parts);
        assertEquals(1, parts.size());
        assertEquals(Text.class, parts.get(0).getClass());
        assertEquals("text", ((Text) parts.get(0)).getText());

        parts = DocParser.parse("   text   ");
        assertNotNull(parts);
        assertEquals(1, parts.size());
        assertEquals(Text.class, parts.get(0).getClass());
        assertEquals("   text   ", ((Text) parts.get(0)).getText());

        parts = DocParser.parse("{@link}");
        assertNotNull(parts);
        assertEquals(1, parts.size());
        assertEquals(Tag.class, parts.get(0).getClass());
        assertEquals(TagType.LINK, ((Tag) parts.get(0)).getType());

        parts = DocParser.parse("{@link p} ");
        assertNotNull(parts);
        assertEquals(2, parts.size());
        assertEquals(Tag.class, parts.get(0).getClass());
        assertEquals(TagType.LINK, ((Tag) parts.get(0)).getType());
        assertEquals(Text.class, parts.get(1).getClass());
        assertEquals(" ", ((Text) parts.get(1)).getText());

        parts = DocParser.parse(" {@link p|p}");
        assertNotNull(parts);
        assertEquals(2, parts.size());
        assertEquals(Text.class, parts.get(0).getClass());
        assertEquals(" ", ((Text) parts.get(0)).getText());
        assertEquals(Tag.class, parts.get(1).getClass());
        assertEquals(TagType.LINK, ((Tag) parts.get(1)).getType());

        parts = DocParser.parse("{@link pp}text");
        assertNotNull(parts);
        assertEquals(2, parts.size());
        assertEquals(Tag.class, parts.get(0).getClass());
        assertEquals(TagType.LINK, ((Tag) parts.get(0)).getType());
        assertEquals(Text.class, parts.get(1).getClass());
        assertEquals("text", ((Text) parts.get(1)).getText());

        parts = DocParser.parse("text{@link  |p}");
        assertNotNull(parts);
        assertEquals(2, parts.size());
        assertEquals(Text.class, parts.get(0).getClass());
        assertEquals("text", ((Text) parts.get(0)).getText());
        assertEquals(Tag.class, parts.get(1).getClass());
        assertEquals(TagType.LINK, ((Tag) parts.get(1)).getType());

        parts = DocParser.parse("text{@link  }text");
        assertNotNull(parts);
        assertEquals(3, parts.size());
        assertEquals(Text.class, parts.get(0).getClass());
        assertEquals("text", ((Text) parts.get(0)).getText());
        assertEquals(Tag.class, parts.get(1).getClass());
        assertEquals(TagType.LINK, ((Tag) parts.get(1)).getType());
        assertEquals(Text.class, parts.get(2).getClass());
        assertEquals("text", ((Text) parts.get(2)).getText());

        parts = DocParser.parse("{@link     }{@link pp|p}");
        assertNotNull(parts);
        assertEquals(2, parts.size());
        assertEquals(Tag.class, parts.get(0).getClass());
        assertEquals(TagType.LINK, ((Tag) parts.get(0)).getType());
        assertEquals(Tag.class, parts.get(1).getClass());
        assertEquals(TagType.LINK, ((Tag) parts.get(1)).getType());

        parts = DocParser.parse("{@link pppp}text{@link |}");
        assertNotNull(parts);
        assertEquals(3, parts.size());
        assertEquals(Tag.class, parts.get(0).getClass());
        assertEquals(TagType.LINK, ((Tag) parts.get(0)).getType());
        assertEquals(Text.class, parts.get(1).getClass());
        assertEquals("text", ((Text) parts.get(1)).getText());
        assertEquals(Tag.class, parts.get(2).getClass());
        assertEquals(TagType.LINK, ((Tag) parts.get(2)).getType());

        parts = DocParser.parse("{@field_link title|subject|1|field|link}");
        assertNotNull(parts);
        assertEquals(1, parts.size());
        assertEquals(Tag.class, parts.get(0).getClass());
        assertEquals(TagType.FIELD_LINK, ((Tag) parts.get(0)).getType());
    }

    @Test
    void testInvalidTagNotationsParsedAsText() {
        List<Part> parts = DocParser.parse("{@wrong_tag_type }");
        assertNotNull(parts);
        assertEquals(1, parts.size());
        assertEquals(Text.class, parts.get(0).getClass());
        assertEquals("{@wrong_tag_type }", ((Text) parts.get(0)).getText());

        parts = DocParser.parse("{@link}{@wrong_tag_type ee}ppp{@link}");
        assertNotNull(parts);
        assertEquals(3, parts.size());
        assertEquals(Tag.class, parts.get(0).getClass());
        assertEquals(TagType.LINK, ((Tag) parts.get(0)).getType());
        assertEquals(Text.class, parts.get(1).getClass());
        assertEquals("{@wrong_tag_type ee}ppp", ((Text) parts.get(1)).getText());
        assertEquals(Tag.class, parts.get(2).getClass());
        assertEquals(TagType.LINK, ((Tag) parts.get(2)).getType());
    }

    @Test
    void testParserFailsOnIllegalArgument() {
        assertThrows(
                Exception.class,
                () -> DocParser.parse(null)
        );
    }

}
