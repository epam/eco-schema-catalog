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
package com.epam.eco.schemacatalog.domain.schema;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrei_Tytsik
 */
public class SchemaVersionRangeTest {

    @Test
    public void testRangeIsParsedFromVersionString() {
        SchemaVersionRange range = SchemaVersionRange.parse("    1          ");
        Assertions.assertNotNull(range);
        Assertions.assertEquals(1, range.earliest);
        Assertions.assertEquals(1, range.latest);
    }

    @Test
    public void testRangeIsNotParsedFromInvalidVersionString1() {
        assertThrows(
                Exception.class,
                () -> SchemaVersionRange.parse("              ")
        );
    }

    @Test
    public void testRangeIsNotParsedFromInvalidVersionString2() {
        assertThrows(
                Exception.class,
                () -> SchemaVersionRange.parse("")
        );
    }

    @Test
    public void testRangeIsNotParsedFromInvalidVersionString3() {
        assertThrows(
                Exception.class,
                () -> SchemaVersionRange.parse("a")
        );
    }

    @Test
    public void testRangeIsParsedFromVersionRangeString1() {
        SchemaVersionRange range = SchemaVersionRange.parse("  [,]   ");
        Assertions.assertNotNull(range);
        Assertions.assertEquals(0, range.earliest);
        Assertions.assertEquals(Integer.MAX_VALUE, range.latest);
    }

    @Test
    public void testRangeIsParsedFromVersionRangeString2() {
        SchemaVersionRange range = SchemaVersionRange.parse("   [     ,     ]   ");
        Assertions.assertNotNull(range);
        Assertions.assertEquals(0, range.earliest);
        Assertions.assertEquals(Integer.MAX_VALUE, range.latest);
    }

    @Test
    public void testRangeIsParsedFromVersionRangeString3() {
        SchemaVersionRange range = SchemaVersionRange.parse("   [  1   ,     ]   ");
        Assertions.assertNotNull(range);
        Assertions.assertEquals(1, range.earliest);
        Assertions.assertEquals(Integer.MAX_VALUE, range.latest);
    }

    @Test
    public void testRangeIsParsedFromVersionRangeString4() {
        SchemaVersionRange range = SchemaVersionRange.parse("   [     ,  1   ]   ");
        Assertions.assertNotNull(range);
        Assertions.assertEquals(0, range.earliest);
        Assertions.assertEquals(1, range.latest);
    }

    @Test
    public void testRangeIsParsedFromVersionRangeString5() {
        SchemaVersionRange range = SchemaVersionRange.parse("   [  1   ,  1   ]   ");
        Assertions.assertNotNull(range);
        Assertions.assertEquals(1, range.earliest);
        Assertions.assertEquals(1, range.latest);
    }

    @Test
    public void testRangeIsNotCreatedFromInvalidVersions1() {
        assertThrows(
                Exception.class,
                () -> SchemaVersionRange.with(-1, 0)
        );
    }

    @Test
    public void testRangeIsNotCreatedFromInvalidVersions2() {
        assertThrows(
                Exception.class,
                () -> SchemaVersionRange.with(0, -1)
        );
    }

    @Test
    public void testRangeIsNotCreatedFromInvalidVersions3() {
        assertThrows(
                Exception.class,
                () -> SchemaVersionRange.with(2, 1)
        );
    }

    @Test
    public void testRangeIsBeforeVersion() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assertions.assertTrue(range.before(2));
    }

    @Test
    public void testRangeIsNotBeforeVersion1() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assertions.assertFalse(range.before(1));
    }

    @Test
    public void testRangeIsNotBeforeVersion2() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assertions.assertFalse(range.before(0));
    }

    @Test
    public void testRangeIsAfterVersion() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assertions.assertTrue(range.after(0));
    }

    @Test
    public void testRangeIsNotAfterVersion1() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assertions.assertFalse(range.after(1));
    }

    @Test
    public void testRangeIsNotAfterVersion2() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assertions.assertFalse(range.after(2));
    }

    @Test
    public void testVersionIsInRange() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assertions.assertTrue(range.contains(1));
    }

    @Test
    public void testVersionIsNotInRange1() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assertions.assertFalse(range.contains(0));
    }

    @Test
    public void testVersionIsNotInRange2() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        Assertions.assertFalse(range.contains(2));
    }

}
