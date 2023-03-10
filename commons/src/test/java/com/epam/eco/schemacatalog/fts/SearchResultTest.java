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
package com.epam.eco.schemacatalog.fts;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.domain.schema.IdentitySchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.SchemaInfo;
import com.epam.eco.schemacatalog.testdata.SchemaTestData;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrei_Tytsik
 */
public class SearchResultTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testSerializedToJsonAndBack() throws Exception {
        SearchResult<SchemaInfo> origin = new SearchResult<>(
                Arrays.asList(
                        SchemaTestData.randomIdentitySchemaInfo(),
                        SchemaTestData.randomIdentitySchemaInfo(),
                        SchemaTestData.randomIdentitySchemaInfo(),
                        SchemaTestData.randomIdentitySchemaInfo(),
                        SchemaTestData.randomIdentitySchemaInfo()),
                10,
                10,
                1000L,
                null);

        String json = JsonMapper.toJson(origin);
        Assertions.assertNotNull(json);

        SearchResult<IdentitySchemaInfo> deserialized = JsonMapper.jsonToObject(json, SearchResult.class);

        Assertions.assertNotNull(deserialized);
        Assertions.assertEquals(origin, deserialized);
    }

    @Test
    public void testPagingFieldsValid() {
        SearchResult<IdentitySchemaInfo> searchResult = new SearchResult<>(
                Collections.emptyList(),
                1,
                5,
                100L,
                null);
        Assertions.assertEquals(20, searchResult.getTotalPages());
        Assertions.assertTrue(searchResult.isHasPreviousPage());
        Assertions.assertTrue(searchResult.isHasNextPage());
        Assertions.assertFalse(searchResult.isFirstPage());
        Assertions.assertFalse(searchResult.isLastPage());

        searchResult = new SearchResult<>(
                Collections.emptyList(),
                0,
                5,
                0L,
                null);
        Assertions.assertEquals(0, searchResult.getTotalPages());
        Assertions.assertFalse(searchResult.isHasPreviousPage());
        Assertions.assertFalse(searchResult.isHasNextPage());
        Assertions.assertTrue(searchResult.isFirstPage());
        Assertions.assertTrue(searchResult.isLastPage());

        searchResult = new SearchResult<>(
                Collections.emptyList(),
                0,
                5,
                1L,
                null);
        Assertions.assertEquals(1, searchResult.getTotalPages());
        Assertions.assertFalse(searchResult.isHasPreviousPage());
        Assertions.assertFalse(searchResult.isHasNextPage());
        Assertions.assertTrue(searchResult.isFirstPage());
        Assertions.assertTrue(searchResult.isLastPage());

        searchResult = new SearchResult<>(
                Collections.emptyList(),
                0,
                5,
                7L,
                null);
        Assertions.assertEquals(2, searchResult.getTotalPages());
        Assertions.assertFalse(searchResult.isHasPreviousPage());
        Assertions.assertTrue(searchResult.isHasNextPage());
        Assertions.assertTrue(searchResult.isFirstPage());
        Assertions.assertFalse(searchResult.isLastPage());

        searchResult = new SearchResult<>(
                Collections.emptyList(),
                0,
                5,
                11L,
                null);
        Assertions.assertEquals(3, searchResult.getTotalPages());
        Assertions.assertFalse(searchResult.isHasPreviousPage());
        Assertions.assertTrue(searchResult.isHasNextPage());
        Assertions.assertTrue(searchResult.isFirstPage());
        Assertions.assertFalse(searchResult.isLastPage());

        searchResult = new SearchResult<>(
                Collections.emptyList(),
                0,
                5,
                11L,
                5L,
                null);
        Assertions.assertEquals(1, searchResult.getTotalPages());
        Assertions.assertFalse(searchResult.isHasPreviousPage());
        Assertions.assertFalse(searchResult.isHasNextPage());
        Assertions.assertTrue(searchResult.isFirstPage());
        Assertions.assertTrue(searchResult.isLastPage());

        searchResult = new SearchResult<>(
                Collections.emptyList(),
                1,
                5,
                100000000L,
                17L,
                null);
        Assertions.assertEquals(4, searchResult.getTotalPages());
        Assertions.assertTrue(searchResult.isHasPreviousPage());
        Assertions.assertTrue(searchResult.isHasNextPage());
        Assertions.assertFalse(searchResult.isFirstPage());
        Assertions.assertFalse(searchResult.isLastPage());
    }

    @Test
    public void testFailsOnIllegalArguments1() {
        assertThrows(
                Exception.class,
                () -> new SearchResult<>(
                        null,
                        1,
                        1,
                        1L,
                        null
                )
        );
    }

    @Test
    public void testFailsOnIllegalArguments2() {
        assertThrows(
                Exception.class,
                () -> new SearchResult<>(
                        Collections.emptyList(),
                        -1,
                        1,
                        1L,
                        null
                )
        );
    }

    @Test
    public void testFailsOnIllegalArguments3() {
        assertThrows(
                Exception.class,
                () -> new SearchResult<>(
                        Collections.emptyList(),
                        1,
                        0,
                        1L,
                        null
                )
        );
    }

    @Test
    public void testFailsOnIllegalArguments5() {
        assertThrows(
                Exception.class,
                () -> new SearchResult<>(
                        Collections.emptyList(),
                        1,
                        1,
                        1L,
                        0L,
                        null
                )
        );
    }

    @Test
    public void testFailsOnIllegalArguments6() {
        assertThrows(
                Exception.class,
                () -> new SearchResult<>(
                        Collections.emptyList(),
                        1,
                        2,
                        1L,
                        1L,
                        null)
        );
    }
}
