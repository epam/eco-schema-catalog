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


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrei_Tytsik
 */
class SchemaVersionRangeTest {

    @Test
    void testRangeIsParsedFromVersionString() {
        SchemaVersionRange range = SchemaVersionRange.parse("    1          ");
        assertNotNull(range);
        assertEquals(1, range.earliest);
        assertEquals(1, range.latest);
    }

    @Test
    void testRangeIsNotParsedFromInvalidVersionString1() {
        assertThrows(
                Exception.class,
                () -> SchemaVersionRange.parse("              ")
        );
    }

    @Test
    void testRangeIsNotParsedFromInvalidVersionString2() {
        assertThrows(
                Exception.class,
                () -> SchemaVersionRange.parse("")
        );
    }

    @Test
    void testRangeIsNotParsedFromInvalidVersionString3() {
        assertThrows(
                Exception.class,
                () -> SchemaVersionRange.parse("a")
        );
    }

    @Test
    void testRangeIsParsedFromVersionRangeString1() {
        SchemaVersionRange range = SchemaVersionRange.parse("  [,]   ");
        assertNotNull(range);
        assertEquals(0, range.earliest);
        assertEquals(Integer.MAX_VALUE, range.latest);
    }

    @Test
    void testRangeIsParsedFromVersionRangeString2() {
        SchemaVersionRange range = SchemaVersionRange.parse("   [     ,     ]   ");
        assertNotNull(range);
        assertEquals(0, range.earliest);
        assertEquals(Integer.MAX_VALUE, range.latest);
    }

    @Test
    void testRangeIsParsedFromVersionRangeString3() {
        SchemaVersionRange range = SchemaVersionRange.parse("   [  1   ,     ]   ");
        assertNotNull(range);
        assertEquals(1, range.earliest);
        assertEquals(Integer.MAX_VALUE, range.latest);
    }

    @Test
    void testRangeIsParsedFromVersionRangeString4() {
        SchemaVersionRange range = SchemaVersionRange.parse("   [     ,  1   ]   ");
        assertNotNull(range);
        assertEquals(0, range.earliest);
        assertEquals(1, range.latest);
    }

    @Test
    void testRangeIsParsedFromVersionRangeString5() {
        SchemaVersionRange range = SchemaVersionRange.parse("   [  1   ,  1   ]   ");
        assertNotNull(range);
        assertEquals(1, range.earliest);
        assertEquals(1, range.latest);
    }

    @Test
    void testRangeIsNotCreatedFromInvalidVersions1() {
        assertThrows(
                Exception.class,
                () -> SchemaVersionRange.with(-1, 0)
        );
    }

    @Test
    void testRangeIsNotCreatedFromInvalidVersions2() {
        assertThrows(
                Exception.class,
                () -> SchemaVersionRange.with(0, -1)
        );
    }

    @Test
    void testRangeIsNotCreatedFromInvalidVersions3() {
        assertThrows(
                Exception.class,
                () -> SchemaVersionRange.with(2, 1)
        );
    }

    @Test
    void testRangeIsBeforeVersion() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        assertTrue(range.before(2));
    }

    @Test
    void testRangeIsNotBeforeVersion1() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        assertFalse(range.before(1));
    }

    @Test
    void testRangeIsNotBeforeVersion2() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        assertFalse(range.before(0));
    }

    @Test
    void testRangeIsAfterVersion() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        assertTrue(range.after(0));
    }

    @Test
    void testRangeIsNotAfterVersion1() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        assertFalse(range.after(1));
    }

    @Test
    void testRangeIsNotAfterVersion2() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        assertFalse(range.after(2));
    }

    @Test
    void testVersionIsInRange() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        assertTrue(range.contains(1));
    }

    @Test
    void testVersionIsNotInRange1() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        assertFalse(range.contains(0));
    }

    @Test
    void testVersionIsNotInRange2() {
        SchemaVersionRange range = SchemaVersionRange.with(1, 1);
        assertFalse(range.contains(2));
    }

}
