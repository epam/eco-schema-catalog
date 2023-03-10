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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrei_Tytsik
 */
public class TagParserTest {

    @Test
    public void testTagIsParsed() {
        Tag tag = TagParser.parse("{@link}");
        Assertions.assertNotNull(tag);
        Assertions.assertEquals(TagType.LINK, tag.getType());
        Assertions.assertNotNull(tag.getParams());
        Assertions.assertEquals(2, tag.getParams().size());
        Assertions.assertNull(tag.getParams().get(0));
        Assertions.assertNull(tag.getParams().get(1));

        tag = TagParser.parse("{@link }");
        Assertions.assertNotNull(tag);
        Assertions.assertEquals(TagType.LINK, tag.getType());
        Assertions.assertNotNull(tag.getParams());
        Assertions.assertEquals(2, tag.getParams().size());
        Assertions.assertNull(tag.getParams().get(0));
        Assertions.assertNull(tag.getParams().get(1));

        tag = TagParser.parse("{@link      &vert;     }");
        Assertions.assertNotNull(tag);
        Assertions.assertEquals(TagType.LINK, tag.getType());
        Assertions.assertNotNull(tag.getParams());
        Assertions.assertEquals(2, tag.getParams().size());
        Assertions.assertEquals("     |     ", tag.getParams().get(0));
        Assertions.assertNull(tag.getParams().get(1));

        tag = TagParser.parse("{@link param_title}");
        Assertions.assertNotNull(tag);
        Assertions.assertEquals(TagType.LINK, tag.getType());
        Assertions.assertNotNull(tag.getParams());
        Assertions.assertEquals(2, tag.getParams().size());
        Assertions.assertEquals("param_title", tag.getParams().get(0));
        Assertions.assertNull(tag.getParams().get(1));

        tag = TagParser.parse("{@link param_title|param_link}");
        Assertions.assertNotNull(tag);
        Assertions.assertEquals(TagType.LINK, tag.getType());
        Assertions.assertNotNull(tag.getParams());
        Assertions.assertEquals(2, tag.getParams().size());
        Assertions.assertEquals("param_title", tag.getParams().get(0));
        Assertions.assertEquals("param_link", tag.getParams().get(1));

        tag = TagParser.parse("{@link |}");
        Assertions.assertNotNull(tag);
        Assertions.assertEquals(TagType.LINK, tag.getType());
        Assertions.assertNotNull(tag.getParams());
        Assertions.assertEquals(2, tag.getParams().size());
        Assertions.assertNull(tag.getParams().get(0));
        Assertions.assertNull(tag.getParams().get(1));

        tag = TagParser.parse("{@link |param_link}");
        Assertions.assertNotNull(tag);
        Assertions.assertEquals(TagType.LINK, tag.getType());
        Assertions.assertNotNull(tag.getParams());
        Assertions.assertEquals(2, tag.getParams().size());
        Assertions.assertNull(tag.getParams().get(0));
        Assertions.assertEquals("param_link", tag.getParams().get(1));

        tag = TagParser.parse("{@link param_title|}");
        Assertions.assertNotNull(tag);
        Assertions.assertEquals(TagType.LINK, tag.getType());
        Assertions.assertNotNull(tag.getParams());
        Assertions.assertEquals(2, tag.getParams().size());
        Assertions.assertEquals("param_title", tag.getParams().get(0));
        Assertions.assertNull(tag.getParams().get(1));

        tag = TagParser.parse("{@link &#123;&#124;&rbrace;|&#124;}");
        Assertions.assertNotNull(tag);
        Assertions.assertEquals(TagType.LINK, tag.getType());
        Assertions.assertNotNull(tag.getParams());
        Assertions.assertEquals(2, tag.getParams().size());
        Assertions.assertEquals("{|}", tag.getParams().get(0));
        Assertions.assertEquals("|", tag.getParams().get(1));
    }

    @Test
    public void testParserFailsOnIllegalArgument1() {
        assertThrows(
                Exception.class,
                () -> TagParser.parse(null)
        );
    }

    @Test
    public void testParserFailsOnIllegalArgument2() {
        assertThrows(
                Exception.class,
                () -> TagParser.parse("")
        );
    }

    @Test
    public void testParserFailsOnIllegalArgument3() {
        assertThrows(
                Exception.class,
                () -> TagParser.parse("{@lin}")
        );
    }

    @Test
    public void testParserFailsOnIllegalArgument4() {
        assertThrows(
                Exception.class,
                () -> TagParser.parse("{@link")
        );
    }

    @Test
    public void testParserFailsOnIllegalArgument5() {
        assertThrows(
                Exception.class,
                () -> TagParser.parse("@link}")
        );
    }

}
