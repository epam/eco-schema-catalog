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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Andrei_Tytsik
 */
public class DocParserTest {

    @Test
    public void testDocIsParsed() throws Exception {
        List<Part> parts = DocParser.parse("");
        Assert.assertNotNull(parts);
        Assert.assertEquals(1, parts.size());
        Assert.assertEquals(Text.class, parts.get(0).getClass());
        Assert.assertEquals("", ((Text)parts.get(0)).getText());

        parts = DocParser.parse("text");
        Assert.assertNotNull(parts);
        Assert.assertEquals(1, parts.size());
        Assert.assertEquals(Text.class, parts.get(0).getClass());
        Assert.assertEquals("text", ((Text)parts.get(0)).getText());

        parts = DocParser.parse("   text   ");
        Assert.assertNotNull(parts);
        Assert.assertEquals(1, parts.size());
        Assert.assertEquals(Text.class, parts.get(0).getClass());
        Assert.assertEquals("   text   ", ((Text)parts.get(0)).getText());

        parts = DocParser.parse("{@link}");
        Assert.assertNotNull(parts);
        Assert.assertEquals(1, parts.size());
        Assert.assertEquals(Tag.class, parts.get(0).getClass());
        Assert.assertEquals(TagType.LINK, ((Tag)parts.get(0)).getType());

        parts = DocParser.parse("{@link p} ");
        Assert.assertNotNull(parts);
        Assert.assertEquals(2, parts.size());
        Assert.assertEquals(Tag.class, parts.get(0).getClass());
        Assert.assertEquals(TagType.LINK, ((Tag)parts.get(0)).getType());
        Assert.assertEquals(Text.class, parts.get(1).getClass());
        Assert.assertEquals(" ", ((Text)parts.get(1)).getText());

        parts = DocParser.parse(" {@link p|p}");
        Assert.assertNotNull(parts);
        Assert.assertEquals(2, parts.size());
        Assert.assertEquals(Text.class, parts.get(0).getClass());
        Assert.assertEquals(" ", ((Text)parts.get(0)).getText());
        Assert.assertEquals(Tag.class, parts.get(1).getClass());
        Assert.assertEquals(TagType.LINK, ((Tag)parts.get(1)).getType());

        parts = DocParser.parse("{@link pp}text");
        Assert.assertNotNull(parts);
        Assert.assertEquals(2, parts.size());
        Assert.assertEquals(Tag.class, parts.get(0).getClass());
        Assert.assertEquals(TagType.LINK, ((Tag)parts.get(0)).getType());
        Assert.assertEquals(Text.class, parts.get(1).getClass());
        Assert.assertEquals("text", ((Text)parts.get(1)).getText());

        parts = DocParser.parse("text{@link  |p}");
        Assert.assertNotNull(parts);
        Assert.assertEquals(2, parts.size());
        Assert.assertEquals(Text.class, parts.get(0).getClass());
        Assert.assertEquals("text", ((Text)parts.get(0)).getText());
        Assert.assertEquals(Tag.class, parts.get(1).getClass());
        Assert.assertEquals(TagType.LINK, ((Tag)parts.get(1)).getType());

        parts = DocParser.parse("text{@link  }text");
        Assert.assertNotNull(parts);
        Assert.assertEquals(3, parts.size());
        Assert.assertEquals(Text.class, parts.get(0).getClass());
        Assert.assertEquals("text", ((Text)parts.get(0)).getText());
        Assert.assertEquals(Tag.class, parts.get(1).getClass());
        Assert.assertEquals(TagType.LINK, ((Tag)parts.get(1)).getType());
        Assert.assertEquals(Text.class, parts.get(2).getClass());
        Assert.assertEquals("text", ((Text)parts.get(2)).getText());

        parts = DocParser.parse("{@link     }{@link pp|p}");
        Assert.assertNotNull(parts);
        Assert.assertEquals(2, parts.size());
        Assert.assertEquals(Tag.class, parts.get(0).getClass());
        Assert.assertEquals(TagType.LINK, ((Tag)parts.get(0)).getType());
        Assert.assertEquals(Tag.class, parts.get(1).getClass());
        Assert.assertEquals(TagType.LINK, ((Tag)parts.get(1)).getType());

        parts = DocParser.parse("{@link pppp}text{@link |}");
        Assert.assertNotNull(parts);
        Assert.assertEquals(3, parts.size());
        Assert.assertEquals(Tag.class, parts.get(0).getClass());
        Assert.assertEquals(TagType.LINK, ((Tag)parts.get(0)).getType());
        Assert.assertEquals(Text.class, parts.get(1).getClass());
        Assert.assertEquals("text", ((Text)parts.get(1)).getText());
        Assert.assertEquals(Tag.class, parts.get(2).getClass());
        Assert.assertEquals(TagType.LINK, ((Tag)parts.get(2)).getType());

        parts = DocParser.parse("{@field_link title|subject|1|field|link}");
        Assert.assertNotNull(parts);
        Assert.assertEquals(1, parts.size());
        Assert.assertEquals(Tag.class, parts.get(0).getClass());
        Assert.assertEquals(TagType.FIELD_LINK, ((Tag)parts.get(0)).getType());
    }

    @Test
    public void testInvalidTagNotationsParsedAsText() throws Exception {
        List<Part> parts = DocParser.parse("{@wrong_tag_type }");
        Assert.assertNotNull(parts);
        Assert.assertEquals(1, parts.size());
        Assert.assertEquals(Text.class, parts.get(0).getClass());
        Assert.assertEquals("{@wrong_tag_type }", ((Text)parts.get(0)).getText());

        parts = DocParser.parse("{@link}{@wrong_tag_type ee}ppp{@link}");
        Assert.assertNotNull(parts);
        Assert.assertEquals(3, parts.size());
        Assert.assertEquals(Tag.class, parts.get(0).getClass());
        Assert.assertEquals(TagType.LINK, ((Tag)parts.get(0)).getType());
        Assert.assertEquals(Text.class, parts.get(1).getClass());
        Assert.assertEquals("{@wrong_tag_type ee}ppp", ((Text)parts.get(1)).getText());
        Assert.assertEquals(Tag.class, parts.get(2).getClass());
        Assert.assertEquals(TagType.LINK, ((Tag)parts.get(2)).getType());
    }

    @Test(expected=Exception.class)
    public void testParserFailsOnIllegalArgument() throws Exception {
        DocParser.parse(null);
    }

}
