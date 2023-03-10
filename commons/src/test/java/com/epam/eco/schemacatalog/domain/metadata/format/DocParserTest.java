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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrei_Tytsik
 */
public class DocParserTest {

    @Test
    public void testDocIsParsed() {
        List<Part> parts = DocParser.parse("");
        Assertions.assertNotNull(parts);
        Assertions.assertEquals(1, parts.size());
        Assertions.assertEquals(Text.class, parts.get(0).getClass());
        Assertions.assertEquals("", ((Text)parts.get(0)).getText());

        parts = DocParser.parse("text");
        Assertions.assertNotNull(parts);
        Assertions.assertEquals(1, parts.size());
        Assertions.assertEquals(Text.class, parts.get(0).getClass());
        Assertions.assertEquals("text", ((Text)parts.get(0)).getText());

        parts = DocParser.parse("   text   ");
        Assertions.assertNotNull(parts);
        Assertions.assertEquals(1, parts.size());
        Assertions.assertEquals(Text.class, parts.get(0).getClass());
        Assertions.assertEquals("   text   ", ((Text)parts.get(0)).getText());

        parts = DocParser.parse("{@link}");
        Assertions.assertNotNull(parts);
        Assertions.assertEquals(1, parts.size());
        Assertions.assertEquals(Tag.class, parts.get(0).getClass());
        Assertions.assertEquals(TagType.LINK, ((Tag)parts.get(0)).getType());

        parts = DocParser.parse("{@link p} ");
        Assertions.assertNotNull(parts);
        Assertions.assertEquals(2, parts.size());
        Assertions.assertEquals(Tag.class, parts.get(0).getClass());
        Assertions.assertEquals(TagType.LINK, ((Tag)parts.get(0)).getType());
        Assertions.assertEquals(Text.class, parts.get(1).getClass());
        Assertions.assertEquals(" ", ((Text)parts.get(1)).getText());

        parts = DocParser.parse(" {@link p|p}");
        Assertions.assertNotNull(parts);
        Assertions.assertEquals(2, parts.size());
        Assertions.assertEquals(Text.class, parts.get(0).getClass());
        Assertions.assertEquals(" ", ((Text)parts.get(0)).getText());
        Assertions.assertEquals(Tag.class, parts.get(1).getClass());
        Assertions.assertEquals(TagType.LINK, ((Tag)parts.get(1)).getType());

        parts = DocParser.parse("{@link pp}text");
        Assertions.assertNotNull(parts);
        Assertions.assertEquals(2, parts.size());
        Assertions.assertEquals(Tag.class, parts.get(0).getClass());
        Assertions.assertEquals(TagType.LINK, ((Tag)parts.get(0)).getType());
        Assertions.assertEquals(Text.class, parts.get(1).getClass());
        Assertions.assertEquals("text", ((Text)parts.get(1)).getText());

        parts = DocParser.parse("text{@link  |p}");
        Assertions.assertNotNull(parts);
        Assertions.assertEquals(2, parts.size());
        Assertions.assertEquals(Text.class, parts.get(0).getClass());
        Assertions.assertEquals("text", ((Text)parts.get(0)).getText());
        Assertions.assertEquals(Tag.class, parts.get(1).getClass());
        Assertions.assertEquals(TagType.LINK, ((Tag)parts.get(1)).getType());

        parts = DocParser.parse("text{@link  }text");
        Assertions.assertNotNull(parts);
        Assertions.assertEquals(3, parts.size());
        Assertions.assertEquals(Text.class, parts.get(0).getClass());
        Assertions.assertEquals("text", ((Text)parts.get(0)).getText());
        Assertions.assertEquals(Tag.class, parts.get(1).getClass());
        Assertions.assertEquals(TagType.LINK, ((Tag)parts.get(1)).getType());
        Assertions.assertEquals(Text.class, parts.get(2).getClass());
        Assertions.assertEquals("text", ((Text)parts.get(2)).getText());

        parts = DocParser.parse("{@link     }{@link pp|p}");
        Assertions.assertNotNull(parts);
        Assertions.assertEquals(2, parts.size());
        Assertions.assertEquals(Tag.class, parts.get(0).getClass());
        Assertions.assertEquals(TagType.LINK, ((Tag)parts.get(0)).getType());
        Assertions.assertEquals(Tag.class, parts.get(1).getClass());
        Assertions.assertEquals(TagType.LINK, ((Tag)parts.get(1)).getType());

        parts = DocParser.parse("{@link pppp}text{@link |}");
        Assertions.assertNotNull(parts);
        Assertions.assertEquals(3, parts.size());
        Assertions.assertEquals(Tag.class, parts.get(0).getClass());
        Assertions.assertEquals(TagType.LINK, ((Tag)parts.get(0)).getType());
        Assertions.assertEquals(Text.class, parts.get(1).getClass());
        Assertions.assertEquals("text", ((Text)parts.get(1)).getText());
        Assertions.assertEquals(Tag.class, parts.get(2).getClass());
        Assertions.assertEquals(TagType.LINK, ((Tag)parts.get(2)).getType());

        parts = DocParser.parse("{@field_link title|subject|1|field|link}");
        Assertions.assertNotNull(parts);
        Assertions.assertEquals(1, parts.size());
        Assertions.assertEquals(Tag.class, parts.get(0).getClass());
        Assertions.assertEquals(TagType.FIELD_LINK, ((Tag)parts.get(0)).getType());
    }

    @Test
    public void testInvalidTagNotationsParsedAsText() {
        List<Part> parts = DocParser.parse("{@wrong_tag_type }");
        Assertions.assertNotNull(parts);
        Assertions.assertEquals(1, parts.size());
        Assertions.assertEquals(Text.class, parts.get(0).getClass());
        Assertions.assertEquals("{@wrong_tag_type }", ((Text)parts.get(0)).getText());

        parts = DocParser.parse("{@link}{@wrong_tag_type ee}ppp{@link}");
        Assertions.assertNotNull(parts);
        Assertions.assertEquals(3, parts.size());
        Assertions.assertEquals(Tag.class, parts.get(0).getClass());
        Assertions.assertEquals(TagType.LINK, ((Tag)parts.get(0)).getType());
        Assertions.assertEquals(Text.class, parts.get(1).getClass());
        Assertions.assertEquals("{@wrong_tag_type ee}ppp", ((Text)parts.get(1)).getText());
        Assertions.assertEquals(Tag.class, parts.get(2).getClass());
        Assertions.assertEquals(TagType.LINK, ((Tag)parts.get(2)).getType());
    }

    @Test
    public void testParserFailsOnIllegalArgument() {
        assertThrows(
                Exception.class,
                () -> DocParser.parse(null)
        );
    }

}
