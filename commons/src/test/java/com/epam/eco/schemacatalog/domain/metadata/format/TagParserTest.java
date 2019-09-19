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
package com.epam.eco.schemacatalog.domain.metadata.format;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Andrei_Tytsik
 */
public class TagParserTest {

    @Test
    public void testTagIsParsed() throws Exception {
        Tag tag = TagParser.parse("{@link}");
        Assert.assertNotNull(tag);
        Assert.assertEquals(TagType.LINK, tag.getType());
        Assert.assertNotNull(tag.getParams());
        Assert.assertEquals(2, tag.getParams().size());
        Assert.assertEquals(null, tag.getParams().get(0));
        Assert.assertEquals(null, tag.getParams().get(1));

        tag = TagParser.parse("{@link }");
        Assert.assertNotNull(tag);
        Assert.assertEquals(TagType.LINK, tag.getType());
        Assert.assertNotNull(tag.getParams());
        Assert.assertEquals(2, tag.getParams().size());
        Assert.assertEquals(null, tag.getParams().get(0));
        Assert.assertEquals(null, tag.getParams().get(1));

        tag = TagParser.parse("{@link      &vert;     }");
        Assert.assertNotNull(tag);
        Assert.assertEquals(TagType.LINK, tag.getType());
        Assert.assertNotNull(tag.getParams());
        Assert.assertEquals(2, tag.getParams().size());
        Assert.assertEquals("     |     ", tag.getParams().get(0));
        Assert.assertEquals(null, tag.getParams().get(1));

        tag = TagParser.parse("{@link param_title}");
        Assert.assertNotNull(tag);
        Assert.assertEquals(TagType.LINK, tag.getType());
        Assert.assertNotNull(tag.getParams());
        Assert.assertEquals(2, tag.getParams().size());
        Assert.assertEquals("param_title", tag.getParams().get(0));
        Assert.assertEquals(null, tag.getParams().get(1));

        tag = TagParser.parse("{@link param_title|param_link}");
        Assert.assertNotNull(tag);
        Assert.assertEquals(TagType.LINK, tag.getType());
        Assert.assertNotNull(tag.getParams());
        Assert.assertEquals(2, tag.getParams().size());
        Assert.assertEquals("param_title", tag.getParams().get(0));
        Assert.assertEquals("param_link", tag.getParams().get(1));

        tag = TagParser.parse("{@link |}");
        Assert.assertNotNull(tag);
        Assert.assertEquals(TagType.LINK, tag.getType());
        Assert.assertNotNull(tag.getParams());
        Assert.assertEquals(2, tag.getParams().size());
        Assert.assertEquals(null, tag.getParams().get(0));
        Assert.assertEquals(null, tag.getParams().get(1));

        tag = TagParser.parse("{@link |param_link}");
        Assert.assertNotNull(tag);
        Assert.assertEquals(TagType.LINK, tag.getType());
        Assert.assertNotNull(tag.getParams());
        Assert.assertEquals(2, tag.getParams().size());
        Assert.assertEquals(null, tag.getParams().get(0));
        Assert.assertEquals("param_link", tag.getParams().get(1));

        tag = TagParser.parse("{@link param_title|}");
        Assert.assertNotNull(tag);
        Assert.assertEquals(TagType.LINK, tag.getType());
        Assert.assertNotNull(tag.getParams());
        Assert.assertEquals(2, tag.getParams().size());
        Assert.assertEquals("param_title", tag.getParams().get(0));
        Assert.assertEquals(null, tag.getParams().get(1));

        tag = TagParser.parse("{@link &#123;&#124;&rbrace;|&#124;}");
        Assert.assertNotNull(tag);
        Assert.assertEquals(TagType.LINK, tag.getType());
        Assert.assertNotNull(tag.getParams());
        Assert.assertEquals(2, tag.getParams().size());
        Assert.assertEquals("{|}", tag.getParams().get(0));
        Assert.assertEquals("|", tag.getParams().get(1));
    }

    @Test(expected=Exception.class)
    public void testParserFailsOnIllegalArgument1() throws Exception {
        TagParser.parse(null);
    }

    @Test(expected=Exception.class)
    public void testParserFailsOnIllegalArgument2() throws Exception {
        TagParser.parse("");
    }

    @Test(expected=Exception.class)
    public void testParserFailsOnIllegalArgument3() throws Exception {
        TagParser.parse("{@lin}");
    }

    @Test(expected=Exception.class)
    public void testParserFailsOnIllegalArgument4() throws Exception {
        TagParser.parse("{@link");
    }

    @Test(expected=Exception.class)
    public void testParserFailsOnIllegalArgument5() throws Exception {
        TagParser.parse("@link}");
    }

}
