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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Andrei_Tytsik
 */
public class SchemaVersionRangeTest {

    @Test
    public void testRangeIsParsedFromVersionString() throws Exception {
        SchemaVersionRange range = SchemaVersionRange.parse("    1          ");
        Assert.assertNotNull(range);
        Assert.assertEquals(1, range.earliest);
        Assert.assertEquals(1, range.latest);
    }

    @Test(expected=Exception.class)
    public void testRangeIsNotParsedFromInvalidVersionString1() throws Exception {
        SchemaVersionRange.parse("              ");
    }

    @Test(expected=Exception.class)
    public void testRangeIsNotParsedFromInvalidVersionString2() throws Exception {
        SchemaVersionRange.parse("");
    }

    @Test(expected=Exception.class)
    public void testRangeIsNotParsedFromInvalidVersionString3() throws Exception {
        SchemaVersionRange.parse("a");
    }

    @Test
    public void testRangeIsParsedFromVersionRangeString1() throws Exception {
        SchemaVersionRange range = SchemaVersionRange.parse("  [,]   ");
        Assert.assertNotNull(range);
        Assert.assertEquals(0, range.earliest);
        Assert.assertEquals(Integer.MAX_VALUE, range.latest);
    }

    @Test
    public void testRangeIsParsedFromVersionRangeString2() throws Exception {
        SchemaVersionRange range = SchemaVersionRange.parse("   [     ,     ]   ");
        Assert.assertNotNull(range);
        Assert.assertEquals(0, range.earliest);
        Assert.assertEquals(Integer.MAX_VALUE, range.latest);
    }

    @Test
    public void testRangeIsParsedFromVersionRangeString3() throws Exception {
        SchemaVersionRange range = SchemaVersionRange.parse("   [  1   ,     ]   ");
        Assert.assertNotNull(range);
        Assert.assertEquals(1, range.earliest);
        Assert.assertEquals(Integer.MAX_VALUE, range.latest);
    }

    @Test
    public void testRangeIsParsedFromVersionRangeString4() throws Exception {
        SchemaVersionRange range = SchemaVersionRange.parse("   [     ,  1   ]   ");
        Assert.assertNotNull(range);
        Assert.assertEquals(0, range.earliest);
        Assert.assertEquals(1, range.latest);
    }

    @Test
    public void testRangeIsParsedFromVersionRangeString5() throws Exception {
        SchemaVersionRange range = SchemaVersionRange.parse("   [  1   ,  1   ]   ");
        Assert.assertNotNull(range);
        Assert.assertEquals(1, range.earliest);
        Assert.assertEquals(1, range.latest);
    }

    @Test(expected=Exception.class)
    public void testRangeIsNotCreatedFromInvalidVersions1() throws Exception {
        SchemaVersionRange.with(-1, 0);
    }

    @Test(expected=Exception.class)
    public void testRangeIsNotCreatedFromInvalidVersions2() throws Exception {
        SchemaVersionRange.with(0, -1);
    }

    @Test(expected=Exception.class)
    public void testRangeIsNotCreatedFromInvalidVersions3() throws Exception {
        SchemaVersionRange.with(2, 1);
    }

    @Test
    public void testRangeIsBeforeVersion() throws Exception {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assert.assertTrue(range.before(2));
    }

    @Test
    public void testRangeIsNotBeforeVersion1() throws Exception {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assert.assertFalse(range.before(1));
    }

    @Test
    public void testRangeIsNotBeforeVersion2() throws Exception {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assert.assertFalse(range.before(0));
    }

    @Test
    public void testRangeIsAfterVersion() throws Exception {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assert.assertTrue(range.after(0));
    }

    @Test
    public void testRangeIsNotAfterVersion1() throws Exception {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assert.assertFalse(range.after(1));
    }

    @Test
    public void testRangeIsNotAfterVersion2() throws Exception {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assert.assertFalse(range.after(2));
    }

    @Test
    public void testVersionIsInRange() throws Exception {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assert.assertTrue(range.contains(1));
    }

    @Test
    public void testVersionIsNotInRange1() throws Exception {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assert.assertFalse(range.contains(0));
    }

    @Test
    public void testVersionIsNotInRange2() throws Exception {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assert.assertFalse(range.contains(2));
    }

}
