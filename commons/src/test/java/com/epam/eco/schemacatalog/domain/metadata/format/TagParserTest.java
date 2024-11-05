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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrei_Tytsik
 */
class TagParserTest {

    @Test
    void testTagIsParsed() {
        Tag tag = TagParser.parse("{@link}");
        assertNotNull(tag);
        assertEquals(TagType.LINK, tag.getType());
        assertNotNull(tag.getParams());
        assertEquals(2, tag.getParams().size());
        assertNull(tag.getParams().get(0));
        assertNull(tag.getParams().get(1));

        tag = TagParser.parse("{@link }");
        assertNotNull(tag);
        assertEquals(TagType.LINK, tag.getType());
        assertNotNull(tag.getParams());
        assertEquals(2, tag.getParams().size());
        assertNull(tag.getParams().get(0));
        assertNull(tag.getParams().get(1));

        tag = TagParser.parse("{@link      &vert;     }");
        assertNotNull(tag);
        assertEquals(TagType.LINK, tag.getType());
        assertNotNull(tag.getParams());
        assertEquals(2, tag.getParams().size());
        assertEquals("     |     ", tag.getParams().get(0));
        assertNull(tag.getParams().get(1));

        tag = TagParser.parse("{@link param_title}");
        assertNotNull(tag);
        assertEquals(TagType.LINK, tag.getType());
        assertNotNull(tag.getParams());
        assertEquals(2, tag.getParams().size());
        assertEquals("param_title", tag.getParams().get(0));
        assertNull(tag.getParams().get(1));

        tag = TagParser.parse("{@link param_title|param_link}");
        assertNotNull(tag);
        assertEquals(TagType.LINK, tag.getType());
        assertNotNull(tag.getParams());
        assertEquals(2, tag.getParams().size());
        assertEquals("param_title", tag.getParams().get(0));
        assertEquals("param_link", tag.getParams().get(1));

        tag = TagParser.parse("{@link |}");
        assertNotNull(tag);
        assertEquals(TagType.LINK, tag.getType());
        assertNotNull(tag.getParams());
        assertEquals(2, tag.getParams().size());
        assertNull(tag.getParams().get(0));
        assertNull(tag.getParams().get(1));

        tag = TagParser.parse("{@link |param_link}");
        assertNotNull(tag);
        assertEquals(TagType.LINK, tag.getType());
        assertNotNull(tag.getParams());
        assertEquals(2, tag.getParams().size());
        assertNull(tag.getParams().get(0));
        assertEquals("param_link", tag.getParams().get(1));

        tag = TagParser.parse("{@link param_title|}");
        assertNotNull(tag);
        assertEquals(TagType.LINK, tag.getType());
        assertNotNull(tag.getParams());
        assertEquals(2, tag.getParams().size());
        assertEquals("param_title", tag.getParams().get(0));
        assertNull(tag.getParams().get(1));

        tag = TagParser.parse("{@link &#123;&#124;&rbrace;|&#124;}");
        assertNotNull(tag);
        assertEquals(TagType.LINK, tag.getType());
        assertNotNull(tag.getParams());
        assertEquals(2, tag.getParams().size());
        assertEquals("{|}", tag.getParams().get(0));
        assertEquals("|", tag.getParams().get(1));
    }

    @Test
    void testParserFailsOnIllegalArgument1() {
        assertThrows(
                Exception.class,
                () -> TagParser.parse(null)
        );
    }

    @Test
    void testParserFailsOnIllegalArgument2() {
        assertThrows(
                Exception.class,
                () -> TagParser.parse("")
        );
    }

    @Test
    void testParserFailsOnIllegalArgument3() {
        assertThrows(
                Exception.class,
                () -> TagParser.parse("{@lin}")
        );
    }

    @Test
    void testParserFailsOnIllegalArgument4() {
        assertThrows(
                Exception.class,
                () -> TagParser.parse("{@link")
        );
    }

    @Test
    void testParserFailsOnIllegalArgument5() {
        assertThrows(
                Exception.class,
                () -> TagParser.parse("@link}")
        );
    }

}
